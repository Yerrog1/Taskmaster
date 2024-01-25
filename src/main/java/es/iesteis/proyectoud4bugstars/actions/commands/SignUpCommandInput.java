package es.iesteis.proyectoud4bugstars.actions.commands;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record SignUpCommandInput(
		@Length(min = 3, max = 20)
		@Pattern(regexp = "^[a-zA-Z0-9]+$")
		@NotBlank
		String id,

		@Length(min = 3, max = 20)
		@NotBlank
		String realName,

		@Email
		String email,

		@Length(min = 8, max = 80)
		@NotBlank
		String plainPassword
) {
}
