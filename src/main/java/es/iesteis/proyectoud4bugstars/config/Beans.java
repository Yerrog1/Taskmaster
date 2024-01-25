package es.iesteis.proyectoud4bugstars.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Beans {
	private final MemberDetailsService memberDetailsService;
	private final Environment environment;

	public Beans(MemberDetailsService memberDetailsService, Environment environment) {
		this.memberDetailsService = memberDetailsService;
		this.environment = environment;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		var auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(memberDetailsService);
		auth.setPasswordEncoder(passwordEncoder());
		return auth;
	}
}
