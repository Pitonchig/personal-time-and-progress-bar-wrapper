package net.thumbtack.ptpb.wrapper.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thumbtack.ptpb.wrapper.client.syncdata.SyncResponse;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistCommand;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistResourcesTypes;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistResponse;

import java.util.List;

public interface TodoistClientService {

    TodoistResponse postData(String token, List<TodoistCommand> commands) throws JsonProcessingException;

    TodoistResponse postData(String token, TodoistCommand command) throws JsonProcessingException;

    SyncResponse getSyncData(String token, String syncToken, List<TodoistResourcesTypes> resources) throws JsonProcessingException;

}
