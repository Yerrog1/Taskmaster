package es.iesteis.proyectoud4bugstars.service;

import com.opencsv.bean.CsvToBean;
import es.iesteis.proyectoud4bugstars.actions.commands.UpdateMemberInput;
import es.iesteis.proyectoud4bugstars.actions.projections.MemberSummary;
import es.iesteis.proyectoud4bugstars.entity.Member;
import es.iesteis.proyectoud4bugstars.entity.Project;
import es.iesteis.proyectoud4bugstars.exceptions.MemberNotFoundException;
import es.iesteis.proyectoud4bugstars.repository.MemberRepository;
import es.iesteis.proyectoud4bugstars.repository.ProjectRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
	private static final Logger logger = LogManager.getLogger(MemberService.class);
	private final MemberRepository memberRepository;
	private final ProjectRepository projectRepository;

	public MemberService(MemberRepository memberRepository,
						 ProjectRepository projectRepository) {
		this.memberRepository = memberRepository;
		this.projectRepository = projectRepository;
	}


	public Member findMember(String memberId) throws MemberNotFoundException {
		return memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
	}
	public List<MemberSummary> findAllMembers() {
		int ownProjects;
		List<Member> members = memberRepository.findAll();
		List<MemberSummary> memberSummaries = new ArrayList<>();
		for (Member member : members) {
			ownProjects = memberRepository.countOwnProjects(member);
			memberSummaries.add(new MemberSummary(member.getId(),
				member.getRealName(),
				member.getBiography(),
				member.getEmail(),
				member.getEmailVerified(),
				member.getSuperAdmin(),
				ownProjects,
				member.getProjects().size()));
		}

		return memberSummaries;
	}

	public void updateMember(Member member, UpdateMemberInput input) {
		if (input.name().isPresent()) {
			var name = input.name().get();
			if (name.isBlank()) return;

			member.setRealName(name);
		}

		if (input.biography().isPresent()) {
			var biography = input.biography().get();
			if (biography.isBlank()) return;

			member.setBiography(biography);
		}

		memberRepository.save(member);
	}

	public List<Member> searchMembers(String query) {
		return memberRepository.findByIdLikeIgnoreCaseOrRealNameLikeIgnoreCase(query);
	}
}