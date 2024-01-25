package es.iesteis.proyectoud4bugstars.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Email not verified")
public class EmailNotVerifiedException extends Exception {
	public EmailNotVerifiedException() {
		super("Email not verified");
	}
}
