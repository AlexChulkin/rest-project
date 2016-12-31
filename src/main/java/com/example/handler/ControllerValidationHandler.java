package com.example.handler;

import com.example.domain.Product;
import com.example.service.ProductService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by alexc_000 on 2016-12-30.
 */
@ControllerAdvice
@Log4j
public abstract class ControllerValidationHandler<T> {
    @Autowired
    private MessageSource messageSource;

    /**
     * Exception handler for the cases when @Valid-annotated parameters fail validation process.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> errors = result.getFieldErrors();
        return ResponseEntity
                .badRequest()
                .body(processFieldErrors(errors));
    }

    /**
     * Exception handler for the cases when binding errors are considered fatal.
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<?> handleBindException(BindException exception) {
        return ResponseEntity
                .badRequest()
                .body(new ValidationErrorDTO(convertObjectErrorsToErrorMessages(exception.getAllErrors())));
    }

    /**
     * When resource id is illegal.
     *
     * @param ex
     * @param request
     * @param response
     * @return
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public
    @ResponseBody
    ValidationErrorDTO handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request, HttpServletResponse response) {
        log.info("ResourceNotFoundException handler: " + ex.getMessage());
        return new ValidationErrorDTO((new ErrorMessageDTO(ErrorMessageType.ERROR, ex.getMessage())));
    }

    /**
     * Exception handler for missing required parameters errors.
     */
    @ExceptionHandler
    protected ResponseEntity<?> handleServletRequestBindingException(ServletRequestBindingException ex) {
        return ResponseEntity.badRequest().body(new ErrorMessageDTO(ErrorMessageType.ERROR, ex.getMessage()));
    }

    /**
     * Exception handler for invalid payload (e.g. json invalid format error).
     */
    @ExceptionHandler
    protected ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        return ResponseEntity.badRequest().body(new ErrorMessageDTO(ErrorMessageType.ERROR, ex.getMessage()));
    }

    /**
     * Handler for the case when entity and id in update method are inconsistent.
     */
    @ExceptionHandler(InconsistentEntityAndIdException.class)
    public
    @ResponseBody
    ResponseEntity<?> handleInconsistentEntityAndIdException(InconsistentEntityAndIdException ex, WebRequest request, HttpServletResponse response) {
        log.info("InconsistentEntityAndIdException handler:" + ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(new ValidationErrorDTO((new ErrorMessageDTO(ErrorMessageType.ERROR, ex.getMessage()))));
    }

    protected void checkIfIdExists(Long id) {
        Product idEntity = getService().findProductById(id);
        Optional.ofNullable(idEntity).orElseThrow(()
                -> new ResourceNotFoundException("Entity with input id = " + id + " does not exist"));
    }

    protected void checkIdAndEntityForConsistency(Long id, Product entity) {
        if (id != entity.getId()) {
            throw new InconsistentEntityAndIdException("Input id = " + id + " and entity do not correspond");
        }
    }

    protected abstract ProductService getService();

    private ValidationErrorDTO processFieldErrors(List<FieldError> errors) {
        ValidationErrorDTO validationErrorDTO = new ValidationErrorDTO();

        if (Optional.ofNullable(errors).isPresent()) {
            for (FieldError error : errors) {
                Locale currentLocale = LocaleContextHolder.getLocale();
                String msg = messageSource.getMessage(error.getDefaultMessage(), null, currentLocale);
                validationErrorDTO.addErrorMessage(new ErrorMessageDTO(ErrorMessageType.ERROR, msg));
            }
        }
        return validationErrorDTO;
    }

    private List<ErrorMessageDTO> convertObjectErrorsToErrorMessages(List<ObjectError> objectErrors) {
        return objectErrors.stream().map(e -> new ErrorMessageDTO(ErrorMessageType.ERROR, e.getDefaultMessage()))
                .collect(Collectors.toList());
    }
}
