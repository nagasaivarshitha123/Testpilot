package com.innocito.testpilot.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    private final Logger logger = LogManager.getLogger(ExceptionHandlerControllerAdvice.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public @ResponseBody ResponseEntity<ExceptionResponse> handleResourceNotFound(final ResourceNotFoundException exception, final HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        Map<String, String> errors = new HashMap<>();
        errors.put(exception.getKey(), exception.getMessage());
        exceptionResponse.setErrors(errors);
        exceptionResponse.setRequestedURI(request.getRequestURI());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public @ResponseBody ResponseEntity<ExceptionResponse> handleException(final Exception exception, final HttpServletRequest request) {
        logger.error("Error occurred Request url is : {} and Error is : {}", request.getRequestURI(), exception.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        Map<String, String> errors = new HashMap<>();
        errors.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        exceptionResponse.setErrors(errors);
        exceptionResponse.setRequestedURI(request.getRequestURI());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(final MethodArgumentNotValidException exception, final HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        exceptionResponse.setErrors(errors);
        exceptionResponse.setRequestedURI(request.getRequestURI());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(final ConstraintViolationException exception, final HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        Map<String, String> errors = new HashMap<>();
        exception.getConstraintViolations().forEach((error) -> {
            String fieldName = error.getPropertyPath().toString();
            String errorMessage = error.getMessage();
            errors.put(fieldName, errorMessage);
        });
        exceptionResponse.setErrors(errors);
        exceptionResponse.setRequestedURI(request.getRequestURI());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EnumNotFoundException.class)
    public @ResponseBody ResponseEntity<ExceptionResponse> handleEnumNotFoundException(final EnumNotFoundException exception, final HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        Map<String, String> errors = new HashMap<>();
        errors.put(exception.getKey(), exception.getMessage());
        exceptionResponse.setErrors(errors);
        exceptionResponse.setRequestedURI(request.getRequestURI());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UniqueConstraintViolationException.class)
    public @ResponseBody ResponseEntity<ExceptionResponse> handleUniqueConstraintViolationException(final UniqueConstraintViolationException exception, final HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        Map<String, String> errors = new HashMap<>();
        errors.put(exception.getKey(), exception.getMessage());
        exceptionResponse.setErrors(errors);
        exceptionResponse.setRequestedURI(request.getRequestURI());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public @ResponseBody ResponseEntity<ExceptionResponse> handleValidationException(final ValidationException exception, final HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setErrors(exception.getErrors());
        exceptionResponse.setRequestedURI(request.getRequestURI());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public @ResponseBody ResponseEntity<ExceptionResponse> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException exception, final HttpServletRequest request) {
        logger.error("id can not be a float value, 1: {}, 2: {}", request.getRequestURI(), exception.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        Map<String, String> errors = new HashMap<>();
        errors.put(exception.getName(), exception.getName() + " should be of type " + exception.getRequiredType());
        exceptionResponse.setErrors(errors);
        exceptionResponse.setRequestedURI(request.getRequestURI());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public @ResponseBody ResponseEntity<ExceptionResponse> handleAuthenticationException(final AuthenticationException exception, final HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        Map<String, String> errors = new HashMap<>();
        errors.put(exception.getKey(), exception.getMessage());
        exceptionResponse.setErrors(errors);
        exceptionResponse.setRequestedURI(request.getRequestURI());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }

    // added me
    @ExceptionHandler(SecurityException.class)
    public @ResponseBody ResponseEntity<ExceptionResponse> handleSecurityException(final SecurityException exception, final HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        Map<String, String> errors = new HashMap<>();
        errors.put("authorization", exception.getMessage());
        exceptionResponse.setErrors(errors);
        exceptionResponse.setRequestedURI(request.getRequestURI());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.FORBIDDEN);
    }


}