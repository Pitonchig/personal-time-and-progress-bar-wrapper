package net.thumbtack.ptpb.wrapper.common;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNKNOWN_ERROR("Unknown error."),
    HANDLER_NOT_REGISTERED("Handler is not registered"),
    JSON_PARSE_ERROR("JSON parse error"),

    USER_NOT_FOUND("User not found"),
    ITEM_NOT_FOUND("Item not found"),
    PROJECT_NOT_FOUND("Project not found"),

    TODOIST_TOKEN_NOT_FOUND("user's todoist token is not found"),
    TODOIST_SYNC_ERROR("a todoist synchronization errors is occurred"),
    TODOIST_SERVICE_TIMEOUT("timeout while waiting response from a todoist.com");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
