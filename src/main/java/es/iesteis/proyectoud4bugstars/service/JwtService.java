package es.iesteis.proyectoud4bugstars.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import es.iesteis.proyectoud4bugstars.AppSettings;
import es.iesteis.proyectoud4bugstars.entity.Session;
import es.iesteis.proyectoud4bugstars.repository.SessionRepository;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;

@Service
public class JwtService {
	private final AppSettings appSettings;
	private static final Logger logger = LogManager.getLogger(JwtService.class);
	private final SessionRepository sessionRepository;

	public JwtService(AppSettings appSettings,
					  SessionRepository sessionRepository) {
		this.appSettings = appSettings;
		this.sessionRepository = sessionRepository;
	}

	public String emitToken(String user_id) {
		SecureRandom srand;
		try {
			srand = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		byte[] nonce = new byte[16];
		srand.nextBytes(nonce);
		var nonceFinal = HexFormat.of().withUpperCase().formatHex(nonce);

		Instant now = Instant.now();
		Instant expiration = now.plusSeconds(60 * 60 * 24 * 30); // 30 days

		sessionRepository.save(
			new Session()
				.setEmmisionDate(now)
				.setExpirationDate(expiration)
				.setNonce(nonceFinal)
				.setUserId(user_id)
		);

		return JWT.create()
			.withNotBefore(now)
			.withExpiresAt(expiration)
			.withSubject(user_id)
			.withClaim("nonce", nonceFinal)
			.sign(Algorithm.HMAC256(appSettings.getJwtSecret()));
	}

	/**
	 * Verifica que el token esté bien formado y que no haya expirado. Si es así, devuelve el id del usuario.
	 * Si no, devuelve null.
	 *
	 * @param token Token a verificar
	 * @return Id del usuario o null si el token no es válido
	 */
	public String getValidUserIdFromToken(@NotNull String token) {
		if (Strings.isBlank(token)) {
			return null;
		}

		try {
			var verifiedToken = JWT.require(Algorithm.HMAC256(appSettings.getJwtSecret()))
				.build()
				.verify(token);

			if (verifiedToken.getExpiresAtAsInstant().isBefore(Instant.now())) {
				return null;
			}

			var nonce = verifiedToken.getClaim("nonce").asString();
			var dbSession = sessionRepository.findById(nonce).orElse(null);

			var subject = verifiedToken.getSubject();
			if (subject == null || Strings.isBlank(subject)) {
				return null;
			}

			if (dbSession == null) return null;
			if (dbSession.getExpirationDate().isBefore(Instant.now())) return null;
			if (dbSession.isRevoked()) return null;
			if (!dbSession.getUserId().equals(subject)) return null;

			return subject;
		} catch (Exception e) {
			logger.error("Error al verificar el token", e);
			return null;
		}
	}

	public void invalidateToken(String credentials) {
		var token = JWT.require(Algorithm.HMAC256(appSettings.getJwtSecret()))
			.build()
			.verify(credentials);

		var sess = sessionRepository.findById(token.getClaim("nonce").asString())
			.orElse(null);

		if (sess == null) return;

		sess.setRevoked(true);
		sessionRepository.save(sess);
	}
}
