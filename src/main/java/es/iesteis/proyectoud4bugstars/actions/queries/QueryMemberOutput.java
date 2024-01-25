package es.iesteis.proyectoud4bugstars.actions.queries;

import es.iesteis.proyectoud4bugstars.actions.projections.LinkSummary;
import es.iesteis.proyectoud4bugstars.actions.projections.ProjectSummary;

import java.util.List;

public record QueryMemberOutput(
	String memberId,
	String name,
	String biography,
	String email,
	String profilePhotoUrl,
	List<ProjectSummary> projects,
	List<LinkSummary> links
) {
}
