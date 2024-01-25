package es.iesteis.proyectoud4bugstars.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Project not found")
public class ProjectNotFoundException extends Exception {
	public ProjectNotFoundException(String projectId) {
		super("Project " + projectId + " not found");
	}
}
