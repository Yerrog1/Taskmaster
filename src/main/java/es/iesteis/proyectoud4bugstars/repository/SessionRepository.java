package es.iesteis.proyectoud4bugstars.repository;

import es.iesteis.proyectoud4bugstars.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, String> {
}
