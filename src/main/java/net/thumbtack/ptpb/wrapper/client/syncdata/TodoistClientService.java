package net.thumbtack.ptpb.wrapper.client.syncdata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.thumbtack.ptpb.wrapper.client.TodoistClientProperties;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class TodoistClientService {
    private final ObjectMapper objectMapper;
    private final TodoistClientProperties properties;

    private static final String TOKEN = "token";
    private static final String SYNC_TOKEN = "sync_token";
    private static final String RESOURCE_TYPES = "resource_types";
    private static final String COMMANDS = "commands";

    private final Client client;

    public TodoistClientService(TodoistClientProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY);
        client = ClientBuilder.newClient(clientConfig);
    }

    public SyncResponse getSyncData(String token, String syncToken, List<TodoistResourcesTypes> resources) throws JsonProcessingException {
        List<String> resourcesStringList = new LinkedList<>();
        for (TodoistResourcesTypes type : resources) {
            resourcesStringList.add(type.getType());
        }

        Form form = new Form();
        form.param(TOKEN, token);
        form.param(SYNC_TOKEN, syncToken);
        form.param(RESOURCE_TYPES, objectMapper.writeValueAsString(resourcesStringList));
        return send(SyncResponse.class, form);
    }

    public TodoistResponse postData(String token, TodoistCommand command) throws JsonProcessingException {
        return postData(token, Collections.singletonList(command));
    }

    public TodoistResponse postData(String token, List<TodoistCommand> commands) throws JsonProcessingException {
        Form form = new Form();
        form.param(TOKEN, token);
        form.param(COMMANDS, objectMapper.writeValueAsString(commands));

        return send(TodoistResponse.class, form);
    }

    private <T> T send(Class<T> clazz, Form form) {
        return (T) client.target(properties.getUri())
                .request()
                .post(Entity.form(form))
                .readEntity(clazz);
    }

}
