package es.iesteis.proyectoud4bugstars.actions.queries;

import java.time.Instant;

public record ApiInfoOutput(
		String environment,
		Instant currentTimestamp
) {
}
