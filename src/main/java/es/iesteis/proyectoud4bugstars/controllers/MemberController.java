package es.iesteis.proyectoud4bugstars.controllers;

import es.iesteis.proyectoud4bugstars.actions.commands.UpdateMemberInput;
import es.iesteis.proyectoud4bugstars.actions.projections.LinkSummary;
import es.iesteis.proyectoud4bugstars.actions.projections.MemberSummary;
import es.iesteis.proyectoud4bugstars.actions.projections.ProjectSummary;
import es.iesteis.proyectoud4bugstars.actions.queries.MemberBadge;
import es.iesteis.proyectoud4bugstars.actions.queries.QueryMemberListOutput;
import es.iesteis.proyectoud4bugstars.actions.queries.QueryMemberOutput;
import es.iesteis.proyectoud4bugstars.config.MemberDetails;
import es.iesteis.proyectoud4bugstars.entity.Member;
import es.iesteis.proyectoud4bugstars.entity.Project;
import es.iesteis.proyectoud4bugstars.exceptions.MemberNotFoundException;
import es.iesteis.proyectoud4bugstars.files.Filer;
import es.iesteis.proyectoud4bugstars.service.LinkService;
import es.iesteis.proyectoud4bugstars.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@RestController
public class MemberController {
	private final MemberService memberService;
	private final Filer filer;
	private final LinkService linkService;

	public MemberController(MemberService memberService, Filer filer, LinkService linkService) {
		this.memberService = memberService;
		this.filer = filer;
		this.linkService = linkService;
	}

	@GetMapping("/api/v1/members/{memberid}")
	public QueryMemberOutput getMember(
		@PathVariable("memberid") String memberId
	) throws MemberNotFoundException {
		Member member = memberService.findMember(memberId);
		Collection<Project> projects = member.getProjects();
		List<ProjectSummary> projectSummaries = projects
			.stream()
			.map(project -> new ProjectSummary(project.getOwner().getRealName(), project.getProjectId(), project.getName(), project.getDescription(), project.getWebsite()))
			.toList();

		return new QueryMemberOutput(
			member.getId(),
			member.getRealName(),
			member.getBiography(),
			member.getEmail(),
			filer.getProfilePhotoUrl(member.getId()),
			projectSummaries,
			member
				.getProfileLinks()
				.stream()
				.map(link -> new LinkSummary(link.getName(), link.getUrl(), link.getVerified()))
				.toList()
		);
	}

	@GetMapping("/api/v1/members")
	@Secured("ROLE_ADMIN")
	public QueryMemberListOutput getMembers() {
		List<MemberSummary> memberSummaries = memberService.findAllMembers();
		return new QueryMemberListOutput(memberSummaries);
	}

	@GetMapping("/api/v1/members/search")
	public List<MemberBadge> searchMembers(
		Authentication authentication,
		@RequestParam("query") String query
	) {
		MemberDetails principal = (MemberDetails) authentication.getPrincipal();
		Member member = principal.getMember();

		return memberService
			.searchMembers(query)
			.stream()
			.filter(m -> !m.getId().equals(member.getId()))
			.map(m -> new MemberBadge(
				m.getId(),
				m.getRealName(),
				filer.getProfilePhotoUrl(m.getId())
			))
			.toList();
	}


	@PostMapping("/api/v1/profile/photo")
	public ResponseEntity<Void> uploadPhoto(
		Authentication authentication,
		MultipartFile photo
	) throws IOException {
		MemberDetails principal = (MemberDetails) authentication.getPrincipal();
		Member member = principal.getMember();

		if (photo.getContentType() == null) {
			throw new IllegalArgumentException("File type not supported");
		}

		if (photo.getSize() > 1024 * 1024 * 5) {
			return new ResponseEntity<>(HttpStatus.PAYLOAD_TOO_LARGE);
		}

		if (!photo.getContentType().equals("image/jpeg") && !photo.getContentType().equals("image/png")) {
			return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		}

		filer.saveProfilePhoto(member.getId(), photo.getBytes());

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PatchMapping("/api/v1/profile")
	public ResponseEntity<Void> updateProfile(
		Authentication authentication,
		@RequestBody @Valid UpdateMemberInput input
	) {
		MemberDetails principal = (MemberDetails) authentication.getPrincipal();
		Member actor = principal.getMember();

		memberService.updateMember(actor, input);
		input.links().ifPresent(l -> linkService.setProfileLinks(actor, l));

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
