package net.thumbtack.ptpb.wrapper.client.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistClientService;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistCommand;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProjectClient {

//    private final TodoistClientService client;
//
//    private String PROJECT_ID = "id";
//    private String PROJECT_NAME = "name";
//    private String PROJECT_COLOR = "color";
//    private String PROJECT_PARENT_ID = "parent_id";
//    private String PROJECT_CHILD_ORDER = "child_order";
//    private String PROJECT_IS_FAVORITE = "is_favorite";
//    private String PROJECT_IS_COLLAPSED = "is_collapsed";
//
//    public TodoistResponse addProject(String token, String name, int color, long parentId, int childOrder, boolean isFavorite) throws JsonProcessingException {
//        TodoistCommand command = TodoistCommand.builder()
//                .type("project_add")
//                .tempId(UUID.randomUUID().toString())
//                .uuid(UUID.randomUUID().toString())
//                .arg(PROJECT_NAME, name)
//                .arg(PROJECT_COLOR, color)
//                .arg(PROJECT_PARENT_ID, parentId)
//                .arg(PROJECT_CHILD_ORDER, childOrder)
//                .arg(PROJECT_IS_FAVORITE, isFavorite ? 1 : 0)
//                .build();
//
//        return client.postData(token, command);
//    }
//
//    public TodoistResponse updateProject(String token, long id, String name, int color, boolean isCollapsed, boolean isFavorite) throws JsonProcessingException {
//        TodoistCommand command = TodoistCommand.builder()
//                .type("project_update")
//                .tempId(UUID.randomUUID().toString())
//                .uuid(UUID.randomUUID().toString())
//                .arg(PROJECT_ID, id)
//                .arg(PROJECT_NAME, name)
//                .arg(PROJECT_COLOR, color)
//                .arg(PROJECT_IS_COLLAPSED, isCollapsed)
//                .arg(PROJECT_IS_FAVORITE, isFavorite ? 1 : 0)
//                .build();
//
//        return client.postData(token, command);
//    }
//
//    public TodoistResponse deleteProject(String token, long id) throws JsonProcessingException {
//        TodoistCommand command = TodoistCommand.builder()
//                .type("project_delete")
//                .tempId(UUID.randomUUID().toString())
//                .uuid(UUID.randomUUID().toString())
//                .arg(PROJECT_ID, id)
//                .build();
//
//        return client.postData(token, command);
//    }

}
