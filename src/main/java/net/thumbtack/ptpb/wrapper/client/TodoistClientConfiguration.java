package net.thumbtack.ptpb.wrapper.client;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@Configuration
public class TodoistClientConfiguration {

    @Bean
    public Client createClient() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY);
        return ClientBuilder.newClient(clientConfig);
    }
}
