package es.iesteis.proyectoud4bugstars.actions.commands;

import es.iesteis.proyectoud4bugstars.entity.TaskStatus;

import java.util.Optional;

public record UpdateTaskInput(
	Optional<String> name,
	Optional<String> description,
	Optional<TaskStatus> status,
	Optional<String> assigneeId
) {
}
