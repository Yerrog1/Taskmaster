package es.iesteis.proyectoud4bugstars.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileLinkRepository extends JpaRepository<ProfileLink, Long> {
	List<ProfileLink> findByMember(Member actor);
}