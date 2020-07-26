package net.thumbtack.ptpb.wrapper.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.thumbtack.ptpb.wrapper.rabbitmq.dto.ResponseWrapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static net.thumbtack.ptpb.wrapper.common.ErrorCode.HANDLER_NOT_REGISTERED;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMqMessageProvider {
    private final ObjectMapper objectMapper;

    Map<String, Function<String, ResponseWrapper>> handlers = new HashMap<>();

    @RabbitListener(queues = "ptpb-wrapper")
    public Message messageHandler(Message message, @Header("type") String type) throws JsonProcessingException {
        log.info("message handler: message={}", message);

        if (!handlers.containsKey(type)) {
            return sendData(false, type, HANDLER_NOT_REGISTERED.getMessage());
        }

        String requestJson = new String(message.getBody());
        ResponseWrapper responseWrapper = handlers.get(type).apply(requestJson);
        return sendData(responseWrapper.isOk(), type, objectMapper.writeValueAsString(responseWrapper));
    }

    public void registerHandler(String type, Function<String, ResponseWrapper> handler) {
        log.info("register handler: type={}, handler={}", type, handler);
        if (handlers.containsKey(type)) {
            log.warn("overwriting handler!");
        }

        handlers.put(type, handler);
    }

    public void removeHandler(String type) {
        log.info("remove handler: type={}", type);
        if (!handlers.containsKey(type)) {
            log.warn("handler is not registered!");
        }
        handlers.remove(type);
    }

    private Message sendData(boolean isOk, String type, String data) {
        MessageProperties properties = MessagePropertiesBuilder.newInstance()
                .setContentType("application/json")
                .setHeader("type", type)
                .setHeader("status", isOk)
                .build();

        return MessageBuilder.withBody(data.getBytes())
                .andProperties(properties)
                .build();
    }
}
