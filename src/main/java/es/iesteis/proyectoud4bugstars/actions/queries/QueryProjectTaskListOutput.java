package es.iesteis.proyectoud4bugstars.actions.queries;

import es.iesteis.proyectoud4bugstars.actions.projections.TaskSummary;
import es.iesteis.proyectoud4bugstars.entity.Task;

import java.util.List;

public record QueryProjectTaskListOutput(
	List<TaskSummary> tasks
) {
}
