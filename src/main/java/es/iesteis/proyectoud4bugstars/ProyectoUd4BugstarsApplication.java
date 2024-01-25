package es.iesteis.proyectoud4bugstars;

import com.microsoft.applicationinsights.attach.ApplicationInsights;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ProyectoUd4BugstarsApplication {
	public static void main(String[] args) {
		ApplicationInsights.attach();
		SpringApplication.run(ProyectoUd4BugstarsApplication.class, args);
	}
}
