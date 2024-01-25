package es.iesteis.proyectoud4bugstars;

import com.auth0.jwt.JWT;
import es.iesteis.proyectoud4bugstars.service.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
	properties = {
		"app.jwt-secret=abc123",
	}
)
public class JwtServiceTests {
	private final String BADSIG_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6InRlc3R1c2VyIn0.bk_emq0DwqvWXZqmzFkqu3BDFdPT9mXxX10wn54wO9o";
	private final String EXPIRED_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhcmllbGNvc3RhcyIsIm5iZiI6MTUwMDAwMCwiZXhwIjoxNTAwMDAwLCJub25jZSI6InV0Y2gyNmtwTm1XVkdMTGlTTkxZckE9PSJ9.9nVZTVIQn0b5TM_thRpRKQyvmfXpbzkMKTj2uOMyRMI";

	@Autowired
	private JwtService jwtService;

	@Test
	public void testCreateToken() {
		var token = jwtService.emitToken("testuser");

		var decoded = JWT.decode(token);
		Assertions.assertEquals("testuser", decoded.getSubject());
	}

	@Test
	public void testGetValidUserIdFromTokenWithValidSecret() {
		var validToken = jwtService.emitToken("testuser");
		var user = jwtService.getValidUserIdFromToken(validToken);

		Assertions.assertEquals("testuser", user);
	}

	@Test
	public void testGetValidUserIdFromTokenWithInvalidSecret() {
		var user = jwtService.getValidUserIdFromToken(BADSIG_TOKEN);

		Assertions.assertNull(user);
	}

	@Test
	public void testGetValidUserIdFromTokenWithExpiredToken() {
		var user = jwtService.getValidUserIdFromToken(EXPIRED_TOKEN);

		Assertions.assertNull(user);
	}

}
