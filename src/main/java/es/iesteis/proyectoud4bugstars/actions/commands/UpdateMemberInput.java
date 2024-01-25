package es.iesteis.proyectoud4bugstars.actions.commands;

import java.util.Optional;

public record UpdateMemberInput(
	Optional<String> name,
	Optional<String> biography,
	Optional<ProfileLinkInput[]> links
) {
}
