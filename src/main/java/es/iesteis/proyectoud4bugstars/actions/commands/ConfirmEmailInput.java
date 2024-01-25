package es.iesteis.proyectoud4bugstars.actions.commands;

public record ConfirmEmailInput(
	String email,
	String token
) {
}
