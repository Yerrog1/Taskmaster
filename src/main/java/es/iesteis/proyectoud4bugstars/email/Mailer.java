package es.iesteis.proyectoud4bugstars.email;

public interface Mailer {
	void sendPleaseConfirmYourEmail(String email, String realName, String emailCode);

	void sendWelcomeEmail(String email, String realName);
}
