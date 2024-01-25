package es.iesteis.proyectoud4bugstars.actions.queries;

import es.iesteis.proyectoud4bugstars.actions.projections.MemberSummary;

import java.util.List;

public record QueryMemberListOutput(
	List<MemberSummary> members
) {
}
