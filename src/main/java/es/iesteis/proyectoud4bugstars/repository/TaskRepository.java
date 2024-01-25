package es.iesteis.proyectoud4bugstars.repository;

import es.iesteis.proyectoud4bugstars.entity.Member;
import es.iesteis.proyectoud4bugstars.entity.Project;
import es.iesteis.proyectoud4bugstars.entity.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
	@Query("SELECT t FROM Task t WHERE t.project.owner.id = ?1 AND t.project.projectId = ?2")
	List<Task> findByProjectCoordinates(String ownerid, String projectid);

	@Query("SELECT  t.taskNumber from Task t where t.project = ?1 order by t.taskNumber desc limit 1")
	Integer findLastTask(Project project);


	@Query("SELECT t FROM Task t WHERE t.project = ?1 ORDER BY t.createdAt DESC, t.taskNumber DESC")
	List<Task> findProjectTasks(Project project, Pageable pageable);

	@Query("SELECT t FROM Task t WHERE t.project.projectId = ?2 AND t.taskNumber = ?3 AND t.project.owner.id = ?1")
	Optional<Task> findTask(String ownerId,String projectId, int taskId);

	/**
	 * Metodo para borrar todas las tareas de un proyecto
	 */
	@Modifying
	@Transactional
	@Query("DELETE FROM Task t WHERE t.project = ?1")
	void deleteProjectTasks(Project project);

	@Query("SELECT t FROM Task t where t.project in ?1 and t.assignedMember = ?2 ORDER BY t.createdAt DESC, t.taskNumber DESC")
	List<Task> findMemberTasks(List<Project> projects,Member member,Pageable page);
}
