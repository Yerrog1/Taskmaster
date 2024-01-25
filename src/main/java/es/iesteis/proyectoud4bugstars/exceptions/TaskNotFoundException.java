package es.iesteis.proyectoud4bugstars.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Task not found")
public class TaskNotFoundException extends Exception {
	public TaskNotFoundException(int taskId, String projectId, String ownerId) {
		super("Could not find task " + taskId + " in project " + projectId + " owned by " + ownerId);
	}
}

