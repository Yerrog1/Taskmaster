package es.iesteis.proyectoud4bugstars.actions.queries;

public record ProjectIdAvailableOutput(
	String ownerId,
	String projectId,
	boolean available
) {
}
