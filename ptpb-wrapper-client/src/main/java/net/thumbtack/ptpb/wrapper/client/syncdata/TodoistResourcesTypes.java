package net.thumbtack.ptpb.wrapper.client.syncdata;

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
