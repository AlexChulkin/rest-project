package com.example.validation;

/**
 * Created by alexc_000 on 2016-12-30.
 */
class ResourceNotFoundException extends IllegalArgumentException {
    ResourceNotFoundException() {
        super();
    }

    ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    ResourceNotFoundException(String message) {
        super(message);
    }

    ResourceNotFoundException(Throwable cause) {
        super(cause);
    }

}
