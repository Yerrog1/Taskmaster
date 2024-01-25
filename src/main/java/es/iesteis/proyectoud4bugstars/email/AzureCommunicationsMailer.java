package es.iesteis.proyectoud4bugstars.email;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailAddress;
import com.azure.communication.email.models.EmailContent;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailRecipients;
import es.iesteis.proyectoud4bugstars.AppSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Component
public class AzureCommunicationsMailer implements Mailer {
	private final Logger logger = LoggerFactory.getLogger(AzureCommunicationsMailer.class);
	private final AppSettings appSettings;

	public AzureCommunicationsMailer(AppSettings appSettings) {
		this.appSettings = appSettings;
	}

	@Override
	public void sendPleaseConfirmYourEmail(String realName, String email, String emailCode) {
		var cs = appSettings.getAzureCsConnString();

		EmailClient emailClient = new EmailClientBuilder()
			.connectionString(cs)
			.buildClient();

		ArrayList<EmailAddress> to = new ArrayList<>();
		to.add(new EmailAddress("<" + email + ">"));

		ArrayList<EmailAddress> replyTo = new ArrayList<>();
		replyTo.add(new EmailAddress("<a21arielcg@iesteis.es>"));

		EmailRecipients emailRecipients = new EmailRecipients(to);

		var encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
		var fullUrl = appSettings.getFrontendBaseUrl() + "/confirm?email=" + encodedEmail + "&code=" + emailCode;

		var body = """
			<h1>Verifica tu cuenta de TaskMaster</h1>
			<p>¡Hola %s!</p>
			<p>Alguien (esperemos que tú) ha creado una cuenta en TaskMaster con esta dirección de correo electrónico.
			Si has sido tú, por favor pulsa el siguiente enlace para activar totalmente tu cuenta y empezar a gestionar
			tus tareas de forma más eficiente.</p>
			<a href="https://%s">Confirmar cuenta</a>
			<p>Alternativamente, puedes copiar y pegar el siguiente enlace en tu navegador:</p>
			<pre>https://%s</pre>
			<p>Si no has sido tú, por favor ignora este correo electrónico.</p>
			<p>¡Gracias!</p>
			<p>El equipo de TaskMaster</p>
			""".formatted(
			realName,
			fullUrl,
			fullUrl
		);

		EmailContent emailContent = new EmailContent(
			"Tu cuenta de TaskMaster"
		).setHtml(body);

		logger.info("Sending email to {}", email);

		var message = new EmailMessage(
			"<" + appSettings.getAzureCsSenderEmail() + ">",
			emailContent
		)
			.setRecipients(emailRecipients)
			.setReplyTo(replyTo);

		var sent = emailClient.send(message);
		logger.info("Email sent: {}", sent.getMessageId());
	}

	@Override
	public void sendWelcomeEmail(String email, String realName) {
		var cs = appSettings.getAzureCsConnString();

		EmailClient emailClient = new EmailClientBuilder()
			.connectionString(cs)
			.buildClient();

		ArrayList<EmailAddress> to = new ArrayList<>();
		to.add(new EmailAddress("<" + email + ">"));

		ArrayList<EmailAddress> replyTo = new ArrayList<>();
		replyTo.add(new EmailAddress("<a21arielcg@iesteis.es>"));

		EmailRecipients emailRecipients = new EmailRecipients(to);

		var body = """
			<h1>Bienvenido a TaskMaster</h1>
			<p>¡Hola %s!</p>
			<p>Gracias por unirte a TaskMaster. Tu cuenta fue activada correctamente.</p>
			<p>Hemos hecho un videotuto to' wapo en español cienporcien real no fake latino para que aprendas a usar la aplicación:</p>
			<a href="https://www.youtube.com/watch?v=dQw4w9WgXcQ">Tutorial de TaskMaster</a>
			<p>Esperamos que la herramienta sea útil para tí y para tu equipo. Si tienes cualquier
			consulta, no dudes en ponerte en contactar con nosotros (puedes hacerlo respondiendo a este correo).</p>
			<p>No olvides iniciar sesión en la aplicación para empezar a gestionar tus tareas.</p>
			<p>Un saludo,</p>
			<p>El equipo de TaskMaster</p>
			""".formatted(
			realName
		);

		EmailContent emailContent = new EmailContent(
			"Bienvenido a TaskMaster"
		).setHtml(body);

		logger.info("Sending email to {}", email);

		var message = new EmailMessage(
			"<" + appSettings.getAzureCsSenderEmail() + ">",
			emailContent
		)
			.setRecipients(emailRecipients)
			.setReplyTo(replyTo);

		var sent = emailClient.send(message);
		logger.info("Email sent: {}", sent.getMessageId());
	}
}
