package es.iesteis.proyectoud4bugstars.actions.queries;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.bind.DefaultValue;

public record SearchInput(

	String ownerid,

	String projectName,
	@Max(100)
	@Min(0)
	@DefaultValue("10")
	int limit,

	@Min(1)
	@DefaultValue("1")
	int page
) {
}
