package es.iesteis.proyectoud4bugstars.actions.projections;

public record MemberSummary(

	String id,
	String realName,
	String biography,
	String email,
	boolean isEmailVerified,
	boolean isSuperAdmin,
	int projectsCreatedCount,

	int projectsParticipateCount

) {
}
