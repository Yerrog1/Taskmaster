package es.iesteis.proyectoud4bugstars.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Project already in use")
public class ProjectIdAlredyInUseException extends Exception {
	public ProjectIdAlredyInUseException(String projectId) {
		super("Project " + projectId + " already in use");
	}
}
