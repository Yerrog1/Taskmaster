package es.iesteis.proyectoud4bugstars.actions.queries;

import es.iesteis.proyectoud4bugstars.entity.Member;
import es.iesteis.proyectoud4bugstars.entity.Project;

public record QueryProjectOutput(

	String owner,
	String projectID,
	String name,
	String description,
	String website
) {

}
