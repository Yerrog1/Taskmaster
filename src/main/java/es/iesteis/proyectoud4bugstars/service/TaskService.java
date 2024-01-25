package es.iesteis.proyectoud4bugstars.service;

import es.iesteis.proyectoud4bugstars.actions.commands.CreateTaskInput;
import es.iesteis.proyectoud4bugstars.actions.queries.GetProjectTaskListInput;
import es.iesteis.proyectoud4bugstars.actions.commands.UpdateTaskArgs;
import es.iesteis.proyectoud4bugstars.actions.projections.TaskSummary;
import es.iesteis.proyectoud4bugstars.entity.Member;
import es.iesteis.proyectoud4bugstars.entity.Project;
import es.iesteis.proyectoud4bugstars.entity.Task;
import es.iesteis.proyectoud4bugstars.entity.TaskStatus;
import es.iesteis.proyectoud4bugstars.exceptions.MemberNotFoundException;
import es.iesteis.proyectoud4bugstars.exceptions.MemberNotInProjectException;
import es.iesteis.proyectoud4bugstars.exceptions.ProjectNotFoundException;
import es.iesteis.proyectoud4bugstars.exceptions.TaskNotFoundException;
import es.iesteis.proyectoud4bugstars.repository.MemberRepository;
import es.iesteis.proyectoud4bugstars.repository.ProjectRepository;
import es.iesteis.proyectoud4bugstars.repository.TaskRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
	private final Logger logger = LogManager.getLogger(TaskService.class);
	private final TaskRepository taskRepository;
	private final ProjectRepository projectRepository;
	private final MemberRepository memberRepository;

	public TaskService(TaskRepository taskRepository,
					   ProjectRepository projectRepository,
					   MemberRepository memberRepository) {
		this.taskRepository = taskRepository;
		this.projectRepository = projectRepository;
		this.memberRepository = memberRepository;
	}

	public Task createTask(
		Member member,
		String memberId,
		String projectId,
		CreateTaskInput createTaskInput
	) throws ProjectNotFoundException, MemberNotInProjectException {
		var project = projectRepository
			.findByProjectCoordinates(memberId, projectId)
			.orElseThrow(() -> new ProjectNotFoundException(projectId));
		boolean memberIsInProject = project
			.getMembers()
			.stream()
			.anyMatch(m -> m.getId().equals(member.getId()));
		if (!memberIsInProject) {
			logger.error("Cannot create task because member is not in project");
			throw new MemberNotInProjectException();
		}
		int lastTaskNumber;
		try {
			lastTaskNumber = taskRepository.findLastTask(project);
		} catch (Exception e) {
			lastTaskNumber = 0;
		}
		TaskStatus taskStatus;
		if (createTaskInput.status() == null) {
			taskStatus = TaskStatus.REPORTADA;
		} else {
			switch (createTaskInput.status().toLowerCase()) {
				case "reportada":
					taskStatus = TaskStatus.REPORTADA;
					break;
				case "por_hacer":
					taskStatus = TaskStatus.POR_HACER;
					break;
				case "en_proceso":
					taskStatus = TaskStatus.EN_PROCESO;
					break;
				case "en_revision":
					taskStatus = TaskStatus.EN_REVISION;
					break;
				case "terminada":
					taskStatus = TaskStatus.TERMINADA;
					break;
				case "cancelada":
					taskStatus = TaskStatus.CANCELADA;
					break;
				default:
					taskStatus = TaskStatus.REPORTADA;
					break;
			}
		}
		Task task = new Task()
			.setProject(project)
			.setTaskNumber(lastTaskNumber + 1)
			.setCreator(member)
			.setName(createTaskInput.name())
			.setDescription(createTaskInput.description())
			.setTaskStatus(taskStatus);
		if (createTaskInput.assignedMemberId() != null) {
			Optional<Member> assignedMember = memberRepository.findById(createTaskInput.assignedMemberId());
			if (assignedMember.isPresent()) {
				if (!project.getMembers().contains(assignedMember.get())) {
					logger.error("Cannot assign task to member because member is not in project");
					throw new MemberNotInProjectException();
				}
				task.setAssignedMember(assignedMember.get());
			}
		}
		Task savedTask = taskRepository.save(task);
		logger.info("Task created: " + savedTask);
		return savedTask;
	}

	public List<TaskSummary> getProjectTasks(Member member, String ownerId, String projectId, GetProjectTaskListInput getProjectTaskListInput) throws ProjectNotFoundException, MemberNotInProjectException {
		var project = findProject(member, ownerId, projectId);

		var page = Pageable.ofSize(getProjectTaskListInput.limit())
			.withPage(getProjectTaskListInput.page() - 1);

		return taskRepository
			.findProjectTasks(project, page)
			.stream()
			.map(TaskSummary::new)
			.toList();
	}

	public TaskSummary getTask(Member member, String ownerId, String projectId, int taskNumber) throws ProjectNotFoundException, MemberNotInProjectException, TaskNotFoundException {
		var project = findProject(member, ownerId, projectId);

		Task task = taskRepository
			.findTask(ownerId, project.getProjectId(), taskNumber)
			.orElseThrow(() -> new TaskNotFoundException(taskNumber, projectId, ownerId));

		var summary = new TaskSummary(task);
		logger.info("Task found from " + summary.projectId() + " task number:" + summary.taskNumber());
		return summary;
	}

	public void deleteTask(Member actor, String ownerId, String projectId, int parseInt) throws ProjectNotFoundException, MemberNotInProjectException, TaskNotFoundException {
		var project = findProject(actor, ownerId, projectId);

		// Solo se puede eliminar si el actor es dueño del proyecto (projectId) o es el creador de la tarea (taskNumber)
		Task task = taskRepository
			.findTask(ownerId, project.getProjectId(), parseInt)
			.orElseThrow(() -> new TaskNotFoundException(parseInt, projectId, ownerId));

		if (cannotUpdateTask(actor, task)) {
			logger.error("Cannot delete task because actor is not the creator or the project owner");
			throw new MemberNotInProjectException();
		}

		taskRepository.delete(task);
	}

	private Project findProject(
		Member member,
		String ownerId,
		String projectId
	) throws ProjectNotFoundException, MemberNotInProjectException {
		var project = projectRepository
			.findByProjectCoordinates(ownerId, projectId)
			.orElseThrow(() -> new ProjectNotFoundException("Project not found"));

		if (project.isPublic()) {
			return project;
		}

		boolean memberIsInProject = project
			.getMembers()
			.stream()
			.anyMatch(m -> m.getId().equals(member.getId()));

		if (!memberIsInProject) {
			logger.error("Cannot get project tasks because member is not in project");
			throw new MemberNotInProjectException();
		}

		return project;
	}

	public Task updateTask(UpdateTaskArgs input) throws TaskNotFoundException, MemberNotInProjectException, MemberNotFoundException {
		Task task = taskRepository
			.findTask(input.ownerId(), input.projectId(), input.taskNumber())
			.orElseThrow(() -> new TaskNotFoundException(input.taskNumber(), input.projectId(), input.ownerId()));

		if (cannotUpdateTask(input.actor(), task)) {
			logger.error("Cannot update task because actor is not the creator or the project owner");
			throw new MemberNotInProjectException();
		}

		var patch = input.input();
		patch.name().ifPresent(task::setName);
		patch.description().ifPresent(task::setDescription);
		patch.status().ifPresent(task::setTaskStatus);
		if (patch.assigneeId().isPresent()) {
			var v = patch.assigneeId().get();
			var assignee = memberRepository
				.findById(v)
				.orElseThrow(() -> new MemberNotFoundException(v));

			task.setAssignedMember(assignee);
		}

		return taskRepository.save(task);
	}

	private boolean cannotUpdateTask(Member actor, Task task) {
		return !task.getCreator().getId().equals(actor.getId()) && !task.getProject().getOwner().getId().equals(actor.getId());
	}

	public List<TaskSummary> findMemberAssignedTasks(Member member, String memberId) throws MemberNotFoundException {
		Member memberToFind = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
		List<Project> projects = (List<Project>) memberToFind.getProjects();
		var page = Pageable.ofSize(10);
		List<TaskSummary> tasks = new ArrayList<>();
		if (member.getId().equals(memberId)) {
			tasks = taskRepository
				.findMemberTasks(projects,memberToFind, page)
				.stream()
				.map(TaskSummary::new)
				.toList();
		} else {
			List<Project> avaiableProjects = new ArrayList<>();
			for (Project p : projects) {
				if (p.isPublic()) {
					avaiableProjects.add(p);
				} else {
					if (p.getMembers().stream().anyMatch(m -> m.getId().equals(member.getId()))) {
						avaiableProjects.add(p);
					}
				}
			}
			tasks = taskRepository
				.findMemberTasks(avaiableProjects, memberToFind, page)
				.stream()
				.map(TaskSummary::new)
				.toList();
		}
		return tasks;
	}
}
