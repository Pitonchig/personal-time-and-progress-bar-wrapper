package net.thumbtack.ptpb.wrapper.client.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thumbtack.ptpb.wrapper.client.TodoistClient;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistResponse;

public interface ProjectClient extends TodoistClient {
    TodoistResponse addProject(String token, String name, int color, long parentId, int childOrder, boolean isFavorite) throws JsonProcessingException;

    TodoistResponse updateProject(String token, long id, String name, int color, boolean isCollapsed, boolean isFavorite) throws JsonProcessingException;

    TodoistResponse deleteProject(String token, long id) throws JsonProcessingException;

}
