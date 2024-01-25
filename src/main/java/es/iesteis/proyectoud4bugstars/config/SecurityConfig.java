package es.iesteis.proyectoud4bugstars.config;

import es.iesteis.proyectoud4bugstars.AppSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableWebSecurity
@Component
public class SecurityConfig {
	private final MemberDetailsService memberDetailsService;
	private final AppSettings appSettings;
	private final JwtFilter jwtFilter;
	private final AuthenticationProvider authenticationProvider;
	private final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

	public SecurityConfig(MemberDetailsService memberDetailsService, AppSettings appSettings, JwtFilter jwtFilter, AuthenticationProvider authenticationProvider) {
		this.memberDetailsService = memberDetailsService;
		this.jwtFilter = jwtFilter;
		this.appSettings = appSettings;
		this.authenticationProvider = authenticationProvider;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		logger.info("Allowed origins: " + appSettings.getCorsAllowedOrigins());
		configuration.setAllowedOrigins(appSettings.getCorsAllowedOrigins());
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PATCH"));
		configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "X-Requested-With", "Content-Length", "Accept", "Origin"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.cors(Customizer.withDefaults())
			.authorizeHttpRequests(auth -> {
				auth.requestMatchers(HttpMethod.POST, "/api/v1/confirm").permitAll();
				auth.requestMatchers(HttpMethod.POST, "/api/v1/signin").permitAll();
				auth.requestMatchers(HttpMethod.POST, "/api/v1/signup").permitAll();
				auth.requestMatchers(HttpMethod.GET, "/api/v1/memberidAvailable").permitAll();
				auth.requestMatchers(HttpMethod.GET, "/api/v1/members").hasRole("ADMIN");
				auth.requestMatchers(HttpMethod.GET, "/api/v1/members/**").permitAll();
				auth.requestMatchers("/api/v1/**").authenticated();
				auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
				auth.requestMatchers("/**").permitAll();
			})
			.userDetailsService(memberDetailsService)
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authenticationProvider(authenticationProvider)
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
