package es.iesteis.proyectoud4bugstars.actions.commands;

import java.util.Optional;

public record UpdateProjectInput(
	Optional<String> name,
	Optional<String> description,
	Optional<String> website,
	Optional<Boolean> isPublic
) {
}
