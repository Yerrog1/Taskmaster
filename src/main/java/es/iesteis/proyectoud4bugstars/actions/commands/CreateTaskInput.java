package es.iesteis.proyectoud4bugstars.actions.commands;

import es.iesteis.proyectoud4bugstars.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.bind.DefaultValue;

public record CreateTaskInput(
	@NotBlank
	String name,
	@NotBlank
	String description,
	@DefaultValue("reportada")
	String status,
	@DefaultValue("")
	String assignedMemberId

) {
}
