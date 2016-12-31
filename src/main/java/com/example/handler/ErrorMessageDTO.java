package com.example.handler;


/**
 * Created by alexc_000 on 2016-12-30.
 */
class ErrorMessageDTO {
    private String message;
    private ErrorMessageType type;

    public ErrorMessageDTO() {
    }

    ErrorMessageDTO(ErrorMessageType type, String message) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public ErrorMessageType getType() {
        return type;
    }
}
