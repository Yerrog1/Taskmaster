package es.iesteis.proyectoud4bugstars.controllers;

import es.iesteis.proyectoud4bugstars.actions.commands.AddMemberToProjectInput;
import es.iesteis.proyectoud4bugstars.actions.commands.CreateProjectInput;
import es.iesteis.proyectoud4bugstars.actions.queries.SearchInput;
import es.iesteis.proyectoud4bugstars.actions.commands.UpdateProjectInput;
import es.iesteis.proyectoud4bugstars.actions.projections.ProjectSummary;
import es.iesteis.proyectoud4bugstars.actions.queries.*;
import es.iesteis.proyectoud4bugstars.config.MemberDetails;
import es.iesteis.proyectoud4bugstars.entity.Member;
import es.iesteis.proyectoud4bugstars.entity.Project;
import es.iesteis.proyectoud4bugstars.exceptions.*;
import es.iesteis.proyectoud4bugstars.files.Filer;
import es.iesteis.proyectoud4bugstars.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProjectController {

	private final ProjectService projectService;
	private final Filer filer;

	public ProjectController(ProjectService projectService, Filer filer) {
		this.projectService = projectService;
		this.filer = filer;
	}

	@GetMapping("/api/v1/projects/{ownerid}/{projectid}")
	public QueryProjectOutput getProject(
		@PathVariable("ownerid") String ownerId,
		@PathVariable("projectid") String projectId,
		Authentication authentication
	) throws ProjectNotFoundException {
		MemberDetails principal = (MemberDetails) authentication.getPrincipal();
		Member member = principal.getMember();
		Project project = projectService.findProject(ownerId, projectId, member);
		return new QueryProjectOutput(project.getOwner().getRealName(), project.getProjectId(), project.getName(), project.getDescription(), project.getWebsite());
	}

	@PostMapping("/api/v1/projects")
	public ResponseEntity<Void> createProject(
		Authentication authentication,
		@RequestBody @Valid CreateProjectInput input,
		BindingResult bindingResult
	) throws ProjectIdAlredyInUseException, CommandValidationException, InvalidProjectIdException {
		if (bindingResult.hasErrors()) {
			throw new CommandValidationException(bindingResult);
		}

		MemberDetails principal = (MemberDetails) authentication.getPrincipal();
		Member member = principal.getMember();
		projectService.createProject(member, input.id(), input.name(), input.description(), input.isPublic());

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping("/api/v1/projects/{ownerid}")
	public QueryProjectListOutput getProjectList(
		Authentication authentication,
		@PathVariable("ownerid") String ownerId
	) throws ProjectNotFoundException, MemberNotFoundException {
		MemberDetails principal = (MemberDetails) authentication.getPrincipal();
		Member member = principal.getMember();
		List<Project> projects = projectService.findMemberProjects(ownerId, member);
		List<ProjectSummary> projectSummaries = new ArrayList<>();
		for (Project project : projects) {
			projectSummaries.add(new ProjectSummary(project.getOwner().getRealName(), project.getProjectId(), project.getName(), project.getDescription(), project.getWebsite()));
		}
		return new QueryProjectListOutput(projectSummaries);
	}

	@DeleteMapping("/api/v1/projects/{ownerid}/{projectid}")
	public ResponseEntity<Void> deleteProject(
		Authentication authentication,
		@PathVariable("ownerid") String ownerId,
		@PathVariable("projectid") String projectId
	) throws ProjectNotFoundException, MemberNotFoundException {
		MemberDetails principal = (MemberDetails) authentication.getPrincipal();
		Member member = principal.getMember();
		projectService.deleteProject(ownerId, projectId, member);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);

	}

	@GetMapping("/api/v1/projects/search")
	public List<ProjectSummary> searchProjects(
		Authentication authentication,
		@Valid SearchInput searchInput
	) throws ProjectNotFoundException, MemberNotFoundException {
		MemberDetails principal = (MemberDetails) authentication.getPrincipal();
		Member member = principal.getMember();
		List<ProjectSummary> projects = projectService.searchProjects(searchInput, member);
		return projects;
	}

	@GetMapping("/api/v1/projects/{ownerid}/{projectid}/members")
	public List<MemberBadge> searchMembersInProject(
		Authentication authentication,
		@PathVariable("ownerid") String ownerId,
		@PathVariable("projectid") String projectId,
		@RequestParam String query
	) throws ProjectNotFoundException {
		MemberDetails principal = (MemberDetails) authentication.getPrincipal();
		Member member = principal.getMember();

		return projectService
			.searchMembersInProject(ownerId, projectId, member, query)
			.stream()
			.map(m -> {
				return new MemberBadge(m.getId(), m.getRealName(), filer.getProfilePhotoUrl(m.getId()));
			})
			.toList();
	}

	@PatchMapping("/api/v1/projects/{ownerid}/{projectid}/")
	public ResponseEntity<Void> updateProject(
		Authentication authentication,
		@PathVariable("ownerid") String ownerId,
		@PathVariable("projectid") String projectId,
		@RequestBody @Valid UpdateProjectInput updateProjectInput
	) throws ProjectNotFoundException, MemberNotFoundException, InvalidProjectIdException, ProjectIdAlredyInUseException {
		MemberDetails principal = (MemberDetails) authentication.getPrincipal();
		Member member = principal.getMember();
		projectService.updateProject(ownerId, projectId, member, updateProjectInput);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	@PostMapping("/api/v1/{ownerid}/{projectid}/members")
	public ResponseEntity<Void> addMemberToProject(
		@PathVariable("projectid") String projectId,
		@PathVariable("ownerid") String ownerId,
		@RequestBody @Valid AddMemberToProjectInput addMemberToProjectInput,
		Authentication authentication
	) throws MemberAlredyInProject, ProjectNotFoundException, MemberNotFoundException {
		MemberDetails principal = (MemberDetails) authentication.getPrincipal();
		Member member = principal.getMember();
		projectService.addMemberToProject(member,projectId,ownerId, addMemberToProjectInput);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
