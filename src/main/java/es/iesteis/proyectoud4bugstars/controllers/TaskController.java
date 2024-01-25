package es.iesteis.proyectoud4bugstars.controllers;

import es.iesteis.proyectoud4bugstars.actions.commands.*;
import es.iesteis.proyectoud4bugstars.actions.projections.TaskSummary;
import es.iesteis.proyectoud4bugstars.actions.queries.GetProjectTaskListInput;
import es.iesteis.proyectoud4bugstars.actions.queries.QueryProjectTaskListOutput;
import es.iesteis.proyectoud4bugstars.actions.queries.QueryTaskOutput;
import es.iesteis.proyectoud4bugstars.config.MemberDetails;
import es.iesteis.proyectoud4bugstars.entity.Member;
import es.iesteis.proyectoud4bugstars.entity.Task;
import es.iesteis.proyectoud4bugstars.exceptions.*;
import es.iesteis.proyectoud4bugstars.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TaskController {
	private final TaskService taskService;

	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}

	@PostMapping("/api/v1/{ownerid}/{projectid}/tasks")
	public ResponseEntity<CreateTaskOutput> createTask(
		@PathVariable("ownerid") String ownerId,
		@PathVariable("projectid") String projectId,
		Authentication authentication,
		@RequestBody @Valid CreateTaskInput createTaskInput,
		BindingResult bindingResult
	) throws ProjectNotFoundException, CommandValidationException, MemberNotInProjectException {
		if (bindingResult.hasErrors()) {
			throw new CommandValidationException(bindingResult);
		}

		MemberDetails principal = (MemberDetails) authentication.getPrincipal();
		Member member = principal.getMember();

		Task task = taskService.createTask(
			member,
			ownerId,
			projectId,
			createTaskInput
		);
		return new ResponseEntity<>(
			new CreateTaskOutput(task.getTaskNumber()),
			HttpStatus.CREATED
		);
	}

	@GetMapping("/api/v1/{ownerid}/{projectid}/tasks")
	public QueryProjectTaskListOutput getProjectTasks(
		@PathVariable("ownerid") String ownerId,
		@PathVariable("projectid") String projectId,
		Authentication authentication,
		@Valid GetProjectTaskListInput getProjectTaskListInput
	) throws ProjectNotFoundException, MemberNotInProjectException {
		MemberDetails principal = (MemberDetails) authentication.getPrincipal();
		Member member = principal.getMember();
		List<TaskSummary> tasks = taskService.getProjectTasks(member, ownerId, projectId, getProjectTaskListInput);

		return new QueryProjectTaskListOutput(tasks);
	}

	@GetMapping("/api/v1/{ownerid}/{projectid}/tasks/{tasknumber}")
	public QueryTaskOutput getTask(
		@PathVariable("ownerid") String ownerId,
		@PathVariable("projectid") String projectId,
		@PathVariable("tasknumber") String taskNumber,
		Authentication authentication
	) throws ProjectNotFoundException, MemberNotInProjectException, TaskNotFoundException {
		MemberDetails principal = (MemberDetails) authentication.getPrincipal();
		Member member = principal.getMember();
		TaskSummary task = taskService.getTask(member, ownerId, projectId, Integer.parseInt(taskNumber));
		return new QueryTaskOutput(task);
	}

	@PatchMapping("/api/v1/{ownerid}/{projectid}/{taskid}")
	public ResponseEntity<TaskSummary> updateTask(
		@PathVariable("ownerid") String ownerId,
		@PathVariable("projectid") String projectId,
		@PathVariable("taskid") Integer taskId,
		Authentication authentication,
		@RequestBody @Valid UpdateTaskInput updateTaskInput,
		BindingResult bindingResult
	) throws CommandValidationException, TaskNotFoundException, MemberNotInProjectException, MemberNotFoundException {
		if (bindingResult.hasErrors()) {
			throw new CommandValidationException(bindingResult);
		}

		MemberDetails principal = (MemberDetails) authentication.getPrincipal();
		Member member = principal.getMember();

		Task task = taskService.updateTask(
			new UpdateTaskArgs(member, ownerId, projectId, taskId, updateTaskInput)
		);

		return new ResponseEntity<>(
			new TaskSummary(task),
			HttpStatus.NO_CONTENT
		);
	}

	@DeleteMapping("/api/v1/{ownerid}/{projectid}/tasks/{tasknumber}")
	public ResponseEntity<Void> deleteTask(
		@PathVariable("ownerid") String ownerId,
		@PathVariable("projectid") String projectId,
		@PathVariable("tasknumber") String taskNumber,
		Authentication authentication
	) throws ProjectNotFoundException, MemberNotInProjectException, TaskNotFoundException {
		MemberDetails principal = (MemberDetails) authentication.getPrincipal();
		Member member = principal.getMember();
		taskService.deleteTask(member, ownerId, projectId, Integer.parseInt(taskNumber));
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/api/v1/members/{memberid}/tasks") //Esta ruta es provisional
	public List<TaskSummary> getMemberAssignedTasks(
		@PathVariable("memberid") String memberId,
		Authentication authentication
	) throws ProjectNotFoundException, MemberNotInProjectException, TaskNotFoundException, MemberNotFoundException {
		MemberDetails principal = (MemberDetails) authentication.getPrincipal();
		Member member = principal.getMember();
		List<TaskSummary> tasks = taskService.findMemberAssignedTasks(member, memberId);
		return tasks;
	}
}
