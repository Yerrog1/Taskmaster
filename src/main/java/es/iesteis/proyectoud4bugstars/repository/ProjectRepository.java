package es.iesteis.proyectoud4bugstars.repository;

import es.iesteis.proyectoud4bugstars.entity.Member;
import es.iesteis.proyectoud4bugstars.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
	@Query("SELECT p FROM Project p WHERE p.owner.id = ?1 AND p.projectId = ?2")
	Optional<Project> findByProjectCoordinates(String ownerid, String projectid);

	@Query("SELECT p FROM Project p WHERE p.name LIKE %?1%")
	List<Project> findProjectsContainingProjectName(String projectName);

	@Query("SELECT p FROM Project p WHERE p.owner.id like %?1%")
	List<Project> findProjectsContainingOwnerId(String ownerId);

	@Query("SELECT p FROM Project p WHERE p.owner.id LIKE %?1% AND p.name LIKE %?2%")
	List<Project> findProjectsContainingOwnerIdAndProjectName(String ownerId, String projectName);
}
