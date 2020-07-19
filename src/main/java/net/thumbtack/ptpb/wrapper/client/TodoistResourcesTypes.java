package net.thumbtack.ptpb.wrapper.client;

import lombok.Getter;

@Getter
public enum TodoistResourcesTypes {
    USER("user"),
    PROJECTS("projects"),
    ITEMS("items");

    private String type;

    TodoistResourcesTypes(String type) {
        this.type = type;
    }

}
