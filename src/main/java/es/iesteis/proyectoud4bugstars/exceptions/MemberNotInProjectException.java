package es.iesteis.proyectoud4bugstars.exceptions;


import es.iesteis.proyectoud4bugstars.entity.Member;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Member ID not in proyect")
public class MemberNotInProjectException extends Exception{
	public MemberNotInProjectException(){
		super("El usuario no está en el proyecto");
	}
}
