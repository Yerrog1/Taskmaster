package es.iesteis.proyectoud4bugstars.actions.commands;

import es.iesteis.proyectoud4bugstars.entity.Member;

public record UpdateTaskArgs(
	Member actor,
	String ownerId,
	String projectId,
	Integer taskNumber,
	UpdateTaskInput input
) {
}
