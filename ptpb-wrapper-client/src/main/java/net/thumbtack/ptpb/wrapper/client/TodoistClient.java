package net.thumbtack.ptpb.wrapper.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistResponse;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import java.util.List;

@Slf4j
@Component
public class TodoistClient {
    private final ObjectMapper objectMapper;
    private final TodoistClientProperties properties;

    private static final String TOKEN = "token";
    private static final String SYNC_TOKEN = "sync_token";
    private static final String RESOURCE_TYPES = "resource_types";

    private Client client;

    public TodoistClient(TodoistClientProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY);
        client = ClientBuilder.newClient(clientConfig);
    }

    public TodoistResponse getSyncData(String token, String syncToken, List<String> resources) throws JsonProcessingException {
        Form form = new Form();
        form.param(TOKEN, token);
        form.param(SYNC_TOKEN, syncToken);
        form.param(RESOURCE_TYPES, objectMapper.writeValueAsString(resources));

        return client.target(properties.getUri())
                .request()
                .post(Entity.form(form))
                .readEntity(TodoistResponse.class);
    }


}
