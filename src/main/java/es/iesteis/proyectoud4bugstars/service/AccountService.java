package es.iesteis.proyectoud4bugstars.service;

import es.iesteis.proyectoud4bugstars.email.Mailer;
import es.iesteis.proyectoud4bugstars.entity.Member;
import es.iesteis.proyectoud4bugstars.exceptions.CannotValidateEmailException;
import es.iesteis.proyectoud4bugstars.exceptions.EmailNotVerifiedException;
import es.iesteis.proyectoud4bugstars.exceptions.InvalidCredentialsException;
import es.iesteis.proyectoud4bugstars.exceptions.MemberIdExistsException;
import es.iesteis.proyectoud4bugstars.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class AccountService {
	private final MemberRepository memberRepository;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;
	private final Mailer mailer;
	private final Logger logger = LoggerFactory.getLogger(AccountService.class);

	public AccountService(MemberRepository memberRepository, JwtService jwtService, PasswordEncoder passwordEncoder, Mailer mailer) {
		this.memberRepository = memberRepository;
		this.jwtService = jwtService;
		this.passwordEncoder = passwordEncoder;
		this.mailer = mailer;
	}

	public void signUpUser(String id, String realName, String plainPassword, String email) throws MemberIdExistsException {
		if (memberIdExists(id)) {
			throw new MemberIdExistsException();
		}

		var member = new Member();
		member.setId(id);
		member.setRealName(realName);
		member.setEncryptedPassword(passwordEncoder.encode(plainPassword));
		member.setSuperAdmin(false);
		member.setEmail(email);
		member.setEmailVerified(false);

		var memberVerificationToken = generateEmailVerificationToken();
		member.setEmailCode(memberVerificationToken);

		memberRepository.save(member);

		// Envía el correo de bienvenida de forma asíncrona
		Thread confirmEmailThread =
			new Thread(() -> mailer.sendPleaseConfirmYourEmail(
				member.getRealName(),
				member.getEmail(),
				memberVerificationToken
			));
		confirmEmailThread.start();
	}

	public Boolean memberIdExists(String id) {
		return memberRepository.existsById(id);
	}

	public String signInUser(String userId, String plainPassword) throws InvalidCredentialsException, EmailNotVerifiedException {
		var member = memberRepository.findById(userId).orElseThrow(InvalidCredentialsException::new);

		if (!member.getEmailVerified()) {
			throw new EmailNotVerifiedException();
		}

		if (!passwordEncoder.matches(plainPassword, member.getEncryptedPassword())) {
			throw new InvalidCredentialsException();
		}

		return jwtService.emitToken(member.getId());
	}

	private String generateEmailVerificationToken() {
		var bytes = new byte[20];
		try {
			SecureRandom.getInstance("SHA1PRNG").nextBytes(bytes);
		} catch (Exception ignored) {
		}

		return String.valueOf(Hex.encode(bytes));
	}

	public void confirmEmail(String email, String token) throws CannotValidateEmailException {
		var member = memberRepository
			.buscaEmailVerificar(email, token)
			.orElseThrow(() -> {
				logger.error("Cannot find member with email: " + email);
				return new CannotValidateEmailException();
			});

		if (member.getEmailCode() == null || member.getEmailVerified()) {
			logger.error("Member " + member.getId() + " already verified or verification token not found");
			throw new CannotValidateEmailException();
		}

		if (member.getEmailCode().equals(token)) {
			member.setEmailVerified(true);
			member.setEmailCode(null);
			memberRepository.save(member);
			logger.info("Email verified for member: " + member.getId());
		} else {
			logger.error("Invalid token " + token + " for member: " + member.getId());
			throw new CannotValidateEmailException();
		}

		// Envía el correo de bienvenida de forma asíncrona
		Thread welcomeEmailThread =
			new Thread(() -> mailer.sendWelcomeEmail(member.getRealName(), member.getEmail()));
		welcomeEmailThread.start();
	}
}
