package net.thumbtack.ptpb.wrapper.service.synchronization;

import lombok.Getter;

@Getter
public enum ErrorCode {
    HANDLER_NOT_REGISTERED("Handler is not registered"),
    USER_NOT_FOUND("User not found");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
