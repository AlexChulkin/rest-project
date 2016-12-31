package com.example.handler;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by alexc_000 on 2016-12-30.
 */
class ValidationErrorDTO {

    private List<ErrorMessageDTO> fieldErrors = new LinkedList<>();

    ValidationErrorDTO() {
    }

    ValidationErrorDTO(ErrorMessageDTO ErrorMessageDTO) {
        fieldErrors.add(ErrorMessageDTO);
    }

    ValidationErrorDTO(List<ErrorMessageDTO> fieldErrors) {
        this.fieldErrors.addAll(fieldErrors);
    }

    void addErrorMessage(ErrorMessageDTO ErrorMessageDTO) {
        fieldErrors.add(ErrorMessageDTO);
    }

    public List<ErrorMessageDTO> getFieldErrors() {
        return fieldErrors;
    }
}
