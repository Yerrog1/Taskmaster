package es.iesteis.proyectoud4bugstars.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Alredy in project")
public class MemberAlredyInProject extends Exception{
	public MemberAlredyInProject(String member, String project) {
		super("Member " + member + " alredy in " + project);
	}
}
