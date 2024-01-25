package es.iesteis.proyectoud4bugstars.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MemberNotFoundException extends Exception {
	public MemberNotFoundException(String memberId) {
		super("No se ha encontrado ningún miembro con el id " + memberId + ".");
	}
}
