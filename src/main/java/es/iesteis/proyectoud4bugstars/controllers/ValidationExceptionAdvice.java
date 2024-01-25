package es.iesteis.proyectoud4bugstars.controllers;

import es.iesteis.proyectoud4bugstars.exceptions.CommandValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ValidationExceptionAdvice {
	@ExceptionHandler(CommandValidationException.class)
	public ResponseEntity<Map<String, Object>> handleValidationException(CommandValidationException e) {
		var firstError = e.getBindingResult().getAllErrors().get(0);
		var firstErrorDefaultMessage = firstError.getDefaultMessage();
		if (firstErrorDefaultMessage == null) {
			firstErrorDefaultMessage = "Validation error";
		}

		return new ResponseEntity<>(Map.of(
			"error", "ValidationException",
			"message", firstErrorDefaultMessage
		), HttpStatus.BAD_REQUEST);
	}

}
