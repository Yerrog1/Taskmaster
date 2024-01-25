package es.iesteis.proyectoud4bugstars.actions.queries;

import es.iesteis.proyectoud4bugstars.actions.projections.ProjectSummary;
import es.iesteis.proyectoud4bugstars.entity.Project;

import java.util.List;

public record QueryProjectListOutput(
	List<ProjectSummary> projects
) {
}