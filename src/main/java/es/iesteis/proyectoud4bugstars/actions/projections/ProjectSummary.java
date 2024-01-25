package es.iesteis.proyectoud4bugstars.actions.projections;

import es.iesteis.proyectoud4bugstars.entity.Project;
import es.iesteis.proyectoud4bugstars.entity.Task;

public record ProjectSummary(
	String owner,
	String projectID,
	String name,
	String description,
	String website
) {
	public ProjectSummary(Project project) {
		this(
			project.getOwner().getId(),
			project.getProjectId(),
			project.getName(),
			project.getDescription(),
			project.getWebsite()
		);
	}
}
