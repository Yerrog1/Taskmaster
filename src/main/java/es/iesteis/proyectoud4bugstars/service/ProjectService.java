package es.iesteis.proyectoud4bugstars.service;

import es.iesteis.proyectoud4bugstars.actions.commands.AddMemberToProjectInput;
import es.iesteis.proyectoud4bugstars.actions.queries.SearchInput;
import es.iesteis.proyectoud4bugstars.actions.commands.UpdateProjectInput;
import es.iesteis.proyectoud4bugstars.actions.projections.ProjectSummary;
import es.iesteis.proyectoud4bugstars.entity.Member;
import es.iesteis.proyectoud4bugstars.entity.Project;
import es.iesteis.proyectoud4bugstars.exceptions.*;
import es.iesteis.proyectoud4bugstars.repository.MemberRepository;
import es.iesteis.proyectoud4bugstars.repository.ProjectRepository;
import es.iesteis.proyectoud4bugstars.repository.TaskRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
	private static final Logger logger = LogManager.getLogger(ProjectService.class);
	private final ProjectRepository projectRepository;
	private final MemberRepository memberRepository;
	private final TaskRepository taskRepository;

	public ProjectService(ProjectRepository projectRepository,
						  MemberRepository memberRepository,
						  TaskRepository taskRepository) {
		this.projectRepository = projectRepository;
		this.memberRepository = memberRepository;
		this.taskRepository = taskRepository;
	}

	public Project findProject(
		String memberId,
		String projectId,
		Member member
	) throws ProjectNotFoundException {
		Project project = projectRepository
			.findByProjectCoordinates(memberId, projectId)
			.orElseThrow(() -> new ProjectNotFoundException(projectId));
		if (project.isPublic()) {
			logger.info("El proyecto " + memberId + "/" + projectId + " ha sido encontrado");
			return project;
		}
		boolean memberIsInProject = project
			.getMembers()
			.stream()
			.anyMatch(m -> m.getId().equals(member.getId()));

		if (!memberIsInProject) {
			logger.info("El proyecto " + memberId + "/" + projectId + "no se ha encontrado");
			throw new ProjectNotFoundException(projectId);
		}
		logger.info("El proyecto " + memberId + "/" + projectId + " ha sido encontrado");
		return project;
	}

	public void createProject(
		Member member,
		String projectId,
		String name,
		String description,
		boolean isPublic
	) throws ProjectIdAlredyInUseException, InvalidProjectIdException {
		Optional<Project> projectResult = projectRepository
			.findByProjectCoordinates(member.getId(), projectId);
		if (projectResult.isPresent()) {
			logger.info("El proyecto " + member.getId() + "/" + projectId + " ya existe");
			throw new ProjectIdAlredyInUseException(projectId);
		}
		String projectIdRegex = "^[a-z0-9_-]{1,40}$";
		if (!projectId.matches(projectIdRegex)) {
			logger.info("El proyecto "+member.getId()+"/"+projectId + " no cumple con el formato");
			throw new InvalidProjectIdException();
		}
		Project project = new Project()
			.setOwner(member)
			.setProjectId(projectId)
			.setName(name)
			.setPublic(isPublic)
			.setDescription(description)
			.setMembers(List.of(member));
		projectRepository.save(project);
		logger.info("El proyecto " + member.getId() + "/" + projectId + " ha sido creado");
	}

	public boolean projectIdAvaiable(String ownerId, String projectId) {
		String projectIdRegex = "^[a-z0-9_-]{1,40}$";
		if (!projectId.matches(projectIdRegex)) {
			return false;
		}
		Optional<Project> projectResult = projectRepository
			.findByProjectCoordinates(ownerId, projectId);
		return !projectResult.isPresent();
	}

	public List<Project> findMemberProjects(
		String memberId,
		Member member
	) throws ProjectNotFoundException {
		List<Project> projects = memberRepository.getMemberProjects(memberId);
		List<Project> publics;
		if (memberId.equals(member.getId())) {
			return projects;
		} else {
			publics = new ArrayList<>();
			for (Project project : projects) {
				if (project.isPublic()) {
					publics.add(project);
				}
			}

		}
		logger.info("Los proyectos del miembro " + memberId + " han sido encontrados");
		return publics;
	}

	public void deleteProject(
		String memberId,
		String projectId,
		Member member
	) throws ProjectNotFoundException {
		Project project = projectRepository.findByProjectCoordinates(memberId, projectId)
			.orElseThrow(() -> new ProjectNotFoundException(projectId));
		if (!project.getOwner().getId().equals(member.getId())) {
			logger.info("El proyecto " + memberId + "/" + projectId + " no se ha encontrado");
			throw new ProjectNotFoundException(projectId);
		}
		taskRepository.deleteProjectTasks(project);
		projectRepository.delete(project);
		logger.info("El proyecto " + memberId + "/" + projectId + " ha sido eliminado");
	}

	public List<ProjectSummary> searchProjects(SearchInput searchInput, Member member) {
		List<Project> projects = new ArrayList<>();
		if (searchInput.ownerid() == null) {
			projects = projectRepository.findProjectsContainingProjectName(searchInput.projectName());
		} else if (searchInput.projectName() == null) {
			projects = projectRepository.findProjectsContainingOwnerId(searchInput.ownerid());
		} else {
			projects = projectRepository.findProjectsContainingOwnerIdAndProjectName(searchInput.ownerid(), searchInput.projectName());
		}
		ArrayList<Project> invalidProjects = new ArrayList<>();
		for (int i = 0; i < projects.size(); i++) {
			if (!projects.get(i).isPublic()) {
				boolean memberIsInProject = projects.get(i)
					.getMembers()
					.stream()
					.anyMatch(m -> m.getId().equals(member.getId()));
				if (!memberIsInProject) {
					invalidProjects.add(projects.get(i));
				}
			}
		}
		for (int i = 0; i < invalidProjects.size(); i++) {
			projects.remove(invalidProjects.get(i));
		}
		List<Project> finalProjects = projects.subList((searchInput.page() - 1) * searchInput.limit(),
			Math.min(searchInput.page() * searchInput.limit(), projects.size()));
		List<ProjectSummary> projectSummaries = finalProjects.stream().map(ProjectSummary::new).toList();
		return projectSummaries;
	}

	public List<Member> searchMembersInProject(String ownerId, String projectId, Member member, String query) throws ProjectNotFoundException {
		Project project = projectRepository.findByProjectCoordinates(ownerId, projectId)
			.orElseThrow(() -> new ProjectNotFoundException(projectId));

		return memberRepository.findMembersInProject(query, project);
	}

	public void updateProject(String ownerId, String projectId, Member member, UpdateProjectInput updateProjectInput) throws ProjectNotFoundException {
		Project project = projectRepository.findByProjectCoordinates(ownerId, projectId)
			.orElseThrow(() -> new ProjectNotFoundException(projectId));
		if (!project.getOwner().getId().equals(member.getId())) {
			logger.info("El proyecto " + ownerId + "/" + projectId + " no se ha encontrado");
			throw new ProjectNotFoundException(projectId);
		}

		updateProjectInput.name().ifPresent(project::setName);
		updateProjectInput.description().ifPresent(project::setDescription);
		updateProjectInput.website().ifPresent(project::setWebsite);
		updateProjectInput.isPublic().ifPresent(project::setPublic);

		projectRepository.save(project);
	}
	public void addMemberToProject(Member member, String projectId, String ownerid, AddMemberToProjectInput addMemberToProjectInput) throws MemberAlredyInProject, ProjectNotFoundException, MemberNotFoundException {
		if (member.getId().equals(ownerid)) {
			Optional<Project> project = projectRepository.findByProjectCoordinates(ownerid, projectId);
			if (project.isEmpty()) {
				throw new ProjectNotFoundException(projectId);
			}
			Optional<Member> memberToAdd = memberRepository.findById(addMemberToProjectInput.memberToAddId());
			if (memberToAdd.isEmpty()) {
				throw new MemberNotFoundException(addMemberToProjectInput.memberToAddId());
			}
			Collection<Member> members = project.get().getMembers();
			if (members.contains(memberToAdd.get())) {
				throw new MemberAlredyInProject(addMemberToProjectInput.memberToAddId(), projectId);
			}
			members.add(memberToAdd.get());
			memberRepository.save(member);
		}
	}
}
