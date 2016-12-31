package com.example.validation;

/**
 * Created by alexc_000 on 2016-12-30.
 */
final class InconsistentEntityAndIdException extends IllegalArgumentException {
    InconsistentEntityAndIdException() {
        super();
    }

    InconsistentEntityAndIdException(String message, Throwable cause) {
        super(message, cause);
    }

    InconsistentEntityAndIdException(String message) {
        super(message);
    }

    InconsistentEntityAndIdException(Throwable cause) {
        super(cause);
    }
}
