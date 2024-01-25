package es.iesteis.proyectoud4bugstars.controllers;

import es.iesteis.proyectoud4bugstars.actions.commands.ConfirmEmailInput;
import es.iesteis.proyectoud4bugstars.actions.commands.SignInCommandInput;
import es.iesteis.proyectoud4bugstars.actions.commands.SignInCommandOutput;
import es.iesteis.proyectoud4bugstars.actions.commands.SignUpCommandInput;
import es.iesteis.proyectoud4bugstars.actions.queries.ApiInfoOutput;
import es.iesteis.proyectoud4bugstars.actions.queries.MeQueryOutput;
import es.iesteis.proyectoud4bugstars.actions.queries.MemberIdAvailableInput;
import es.iesteis.proyectoud4bugstars.actions.queries.MemberIdAvailableOutput;
import es.iesteis.proyectoud4bugstars.config.MemberDetails;
import es.iesteis.proyectoud4bugstars.entity.Member;
import es.iesteis.proyectoud4bugstars.exceptions.*;
import es.iesteis.proyectoud4bugstars.files.Filer;
import es.iesteis.proyectoud4bugstars.service.AccountService;
import es.iesteis.proyectoud4bugstars.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * Controla todos los endpoints relacionados con la gestión de cuentas de miebros.
 *
 * @author Ariel Costas
 */
@RestController
public class AccountController {
	private final AccountService accountService;
	private final Environment environment;
	private final JwtService jwtService;
	private final Filer filer;

	public AccountController(AccountService accountService, Environment environment, JwtService jwtService, Filer filer) {
		this.accountService = accountService;
		this.environment = environment;
		this.jwtService = jwtService;
		this.filer = filer;
	}

	@GetMapping(value = "/", produces = "application/json")
	public ApiInfoOutput queryApiInfo() {
		return new ApiInfoOutput(
			environment.getActiveProfiles()[0],
			Instant.now()
		);
	}

	@GetMapping(value = "/api/v1/me", produces = "application/json")
	public MeQueryOutput queryMe(Authentication authentication) {
		MemberDetails principal = (MemberDetails) authentication.getPrincipal();
		Member member = principal.getMember();

		return new MeQueryOutput(
			member.getId(),
			member.getRealName(),
			principal.getAuthorities().stream().map(Object::toString).toArray(String[]::new),
			filer.getProfilePhotoUrl(member.getId())
		);
	}

	@PostMapping(value = "/api/v1/signup", produces = "application/json")
	public ResponseEntity<Void> commandSignUp(
		@RequestBody @Valid SignUpCommandInput signUpCommandInput,
		BindingResult bindingResult
	) throws CommandValidationException, MemberIdExistsException {
		if (bindingResult.hasErrors()) {
			throw new CommandValidationException(bindingResult);
		}

		accountService.signUpUser(
			signUpCommandInput.id(),
			signUpCommandInput.realName(),
			signUpCommandInput.plainPassword(),
			signUpCommandInput.email()
		);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping(value = "/api/v1/memberidAvailable", produces = "application/json")
	public MemberIdAvailableOutput queryMemberIdAvailable(MemberIdAvailableInput memberIdAvailableInput) {
		return new MemberIdAvailableOutput(
			memberIdAvailableInput.id(),
			!accountService.memberIdExists(memberIdAvailableInput.id())
		);
	}

	@PostMapping(value = "/api/v1/signin", produces = "application/json")
	public ResponseEntity<SignInCommandOutput> commandSignIn(
		@RequestBody SignInCommandInput signInCommandInput
	) throws InvalidCredentialsException, EmailNotVerifiedException {
		var token = accountService.signInUser(
			signInCommandInput.memberId(),
			signInCommandInput.plainPassword()
		);

		return new ResponseEntity<>(new SignInCommandOutput(token), HttpStatus.OK);
	}

	@PostMapping(value = "/api/v1/confirm", produces = "application/json")
	public ResponseEntity<Void> commandConfirmEmail(@RequestBody ConfirmEmailInput body) throws CannotValidateEmailException {
		accountService.confirmEmail(body.email(), body.token());

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "/api/v1/signout", produces = "application/json")
	public ResponseEntity<Void> commandSignOut(Authentication authentication) {
		MemberDetails principal = (MemberDetails) authentication.getPrincipal();
		jwtService.invalidateToken((String) authentication.getCredentials());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
