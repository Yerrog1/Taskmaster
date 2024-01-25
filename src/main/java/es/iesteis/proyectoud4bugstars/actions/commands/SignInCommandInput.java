package es.iesteis.proyectoud4bugstars.actions.commands;

public record SignInCommandInput(
	String memberId,
	String plainPassword
) {
}
