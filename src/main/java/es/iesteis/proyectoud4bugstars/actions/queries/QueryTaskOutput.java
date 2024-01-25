package es.iesteis.proyectoud4bugstars.actions.queries;

import es.iesteis.proyectoud4bugstars.actions.projections.TaskSummary;
import es.iesteis.proyectoud4bugstars.entity.Task;

public record QueryTaskOutput (
	TaskSummary task
){
}
