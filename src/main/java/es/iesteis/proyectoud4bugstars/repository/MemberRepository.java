package es.iesteis.proyectoud4bugstars.repository;

import es.iesteis.proyectoud4bugstars.entity.Member;
import es.iesteis.proyectoud4bugstars.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
	Optional<Member> findByIdIgnoreCase(String id);

	@Query("select m from Member m where m.id like %?1% or m.realName like %?1%")
	List<Member> findByIdLikeIgnoreCaseOrRealNameLikeIgnoreCase(String query);

	@Query("SELECT m FROM Member m WHERE (m.id LIKE %?1% OR m.realName LIKE %?1%) AND ?2 MEMBER OF m.projects")
	List<Member> findMembersInProject(String id, Project project);

	@Query("SELECT m.projects FROM Member m WHERE m.id=?1")
	List<Project> getMemberProjects(String id);

	@Query("SELECT m FROM Member m WHERE m.email=?1 AND m.emailCode=?2")
	Optional<Member> buscaEmailVerificar(String email, String code);

	@Query("select count(p) from Project p where p.owner = ?1")
	int countOwnProjects(Member member);
}
