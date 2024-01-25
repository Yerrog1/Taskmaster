package es.iesteis.proyectoud4bugstars.actions.projections;

import es.iesteis.proyectoud4bugstars.entity.Task;

import java.time.Instant;

public record TaskSummary(
	int taskNumber,
	String projectId,
	String name,
	String description,
	String status,
	String assignedTo,
	String createdBy,
	Instant createdAt,
	Instant updatedAt
) {
	public TaskSummary(Task task) {
		this(
			task.getTaskNumber(),
			task.getProject().getProjectId(),
			task.getName(),
			task.getDescription(),
			task.getTaskStatus().toString(),
			task.getAssignedMember() != null ? task.getAssignedMember().getId() : null,
			task.getCreator() != null ? task.getCreator().getId() : null,
			task.getCreatedAt(),
			task.getLastUpdatedAt()
		);
	}
}
