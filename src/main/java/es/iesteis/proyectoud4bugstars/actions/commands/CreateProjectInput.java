package es.iesteis.proyectoud4bugstars.actions.commands;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.bind.DefaultValue;

public record CreateProjectInput(
	@Length(min = 3, max = 50)
	String id,

	@NotBlank
	@Length(min = 3, max = 50)
	String name,

	@Length(max = 250)
	String description,

	@NotNull
	Boolean isPublic,

	@Max(100)
	@Min(0)
	@DefaultValue("10")
	Integer limit,

	@Min(1)
	@DefaultValue("1")
	Integer page
) {

}
