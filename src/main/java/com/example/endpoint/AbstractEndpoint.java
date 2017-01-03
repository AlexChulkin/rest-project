package com.example.endpoint;

import lombok.extern.log4j.Log4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//import com.example.endpoint.validation.*;

/**
 * Created by alexc_000 on 2016-12-30.
 */
@ControllerAdvice
@Log4j
public abstract class AbstractEndpoint {

    /**
     * Exception validation for the cases when @Valid-annotated parameters fail validation process.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<Error>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(convertObjectErrorsToErrors(ex.getBindingResult().getAllErrors()));

    }

    /**
     * Exception handler for validation errors caused by method parameters @RequestParam, @PathVariable, @RequestHeader annotated with javax.validation constraints.
     * Active only given the @Validated is set for the class(endpoint) or method.
     */
    @ExceptionHandler
    protected ResponseEntity<List<Error>> handleConstraintViolationException(ConstraintViolationException ex) {
        List<Error> errors = convertConstraintViolationExceptionToErrors(ex);
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Exception validation for invalid payload (e.g. json invalid format error).
     */
    @ExceptionHandler
    protected ResponseEntity<Error> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.info("HttpMessageNotReadableException validation:" + ex.getMessage());
        return ResponseEntity.badRequest().body(new Error(ex.getMessage()));
    }

    /**
     * Database integrity exception handler
     */
    @ExceptionHandler
    protected ResponseEntity<Error> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.info("DataIntegrityViolationException handling:" + ex.getMessage());
        return ResponseEntity.badRequest().body(new Error(ex.getMessage()));
    }

    /**
     * When resource id is illegal.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Error> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.info("ResourceNotFoundException validation: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Error(ex.getMessage()));
    }

    /**
     * Handler for the case when entity and id in update method are inconsistent.
     */
    @ExceptionHandler(InconsistentEntityAndIdException.class)
    public ResponseEntity<Error> handleInconsistentEntityAndIdException(InconsistentEntityAndIdException ex) {
        log.info("InconsistentEntityAndIdException validation:" + ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(new Error(ex.getMessage()));
    }

    /**
     * Handler for the case of improper URI argument values.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> handleException(IllegalArgumentException ex) {
        log.info("IllegalArgumentException handling:" + ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new Error(ex.getMessage()));
    }


    /**
     * Handler for the case of improper method argument types.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Error> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.info(" MethodArgumentTypeMismatchException handling:" + ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new Error(ex.getMessage()));
    }

    /**
     * Handler for the case of unexpected errors.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleException(Exception ex) {
        log.info(" Default Exception handling:" + ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Error(ex.getMessage()));
    }

    private List<Error> convertObjectErrorsToErrors(List<ObjectError> objectErrors) {

        List<Error> errors = new ArrayList<>();

        for (ObjectError objectError : objectErrors) {

            String message = objectError.getDefaultMessage();

            Error error;
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                String value = (fieldError.getRejectedValue() == null ? null : fieldError.getRejectedValue().toString());
                error = new Error(fieldError.getField(), value, message);
            } else {
                error = new Error(objectError.getObjectName(), objectError.getCode(), objectError.getDefaultMessage());
            }

            errors.add(error);
        }

        return errors;
    }

    private List<Error> convertConstraintViolationExceptionToErrors(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                .stream()
                .map(v -> new Error(v.getPropertyPath().toString(),
                        Optional.ofNullable(v.getInvalidValue()).isPresent() ? v.getInvalidValue().toString() : null,
                        v.getMessage()))
                .collect(Collectors.toList())
                ;
    }
}
