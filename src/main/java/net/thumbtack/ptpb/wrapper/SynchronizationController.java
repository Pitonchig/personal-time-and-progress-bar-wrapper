package net.thumbtack.ptpb.wrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.thumbtack.ptpb.wrapper.common.ErrorCode;
import net.thumbtack.ptpb.wrapper.common.PtpbException;
import net.thumbtack.ptpb.wrapper.service.SynchronizationService;
import net.thumbtack.ptpb.wrapper.service.synchronization.RabbitMqMessageProvider;
import net.thumbtack.ptpb.wrapper.service.synchronization.ResponseWrapper;
import net.thumbtack.ptpb.wrapper.service.synchronization.dto.ErrorAmqpDto;
import net.thumbtack.ptpb.wrapper.service.synchronization.requests.*;
import net.thumbtack.ptpb.wrapper.service.synchronization.responses.ErrorAmqpResponse;
import net.thumbtack.ptpb.wrapper.service.synchronization.responses.SyncProjectsAmqpResponse;
import net.thumbtack.ptpb.wrapper.service.synchronization.responses.SyncUserTokenAmqpResponse;
import org.springframework.stereotype.Component;

import java.util.*;

import static net.thumbtack.ptpb.wrapper.common.ErrorCode.JSON_PARSE_ERROR;

@Slf4j
@Component
public class SynchronizationController {

    private final ObjectMapper objectMapper;
    private final SynchronizationService synchronizationService;
    private final RabbitMqMessageProvider rabbitMqMessageProvider;


    public SynchronizationController(ObjectMapper mapper, SynchronizationService service, RabbitMqMessageProvider provider) {
        objectMapper = mapper;
        synchronizationService = service;
        rabbitMqMessageProvider = provider;

        provider.registerHandler(SyncUserTokenAmqpRequest.class.getSimpleName(), this::syncUserToken);
        provider.registerHandler(SyncProjectsAmqpRequest.class.getSimpleName(), this::syncProjects);
    }

    public ResponseWrapper syncUserToken(String data) {
        try {
            SyncUserTokenAmqpRequest request = objectMapper.readValue(data, SyncUserTokenAmqpRequest.class);
            SyncUserTokenAmqpResponse response = synchronizationService.syncUserToken(request);
            return ResponseWrapper.builder()
                    .isOk(true)
                    .data(objectMapper.writeValueAsString(response))
                    .build();
        } catch (PtpbException e) {
            ErrorAmqpResponse errorAmqpResponse = ptpbExceptionHandler(e);
            return wrapAmqpErrorResponse(errorAmqpResponse);
        } catch (JsonProcessingException e) {
            ErrorAmqpResponse errorAmqpResponse = jsonProcessingExceptionHandler(e);
            return wrapAmqpErrorResponse(errorAmqpResponse);
        }
    }

    public ResponseWrapper syncProjects(String data) {
        try {
            SyncProjectsAmqpRequest request = objectMapper.readValue(data, SyncProjectsAmqpRequest.class);
            SyncProjectsAmqpResponse response = synchronizationService.syncProjects(request);

            return ResponseWrapper.builder()
                    .isOk(true)
                    .data(objectMapper.writeValueAsString(response))
                    .build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseWrapper(false, e.getMessage());
        } catch (PtpbException e) {
            ErrorAmqpResponse errorAmqpResponse = ptpbExceptionHandler(e);
            return wrapAmqpErrorResponse(errorAmqpResponse);
        }
    }

    private ErrorAmqpResponse ptpbExceptionHandler(PtpbException ex) {
        log.error("PtpbExcetion error: ", ex);
        List<ErrorCode> codeList = ex.getErrors();
        ErrorAmqpResponse.ErrorAmqpResponseBuilder errorResponseBuilder = ErrorAmqpResponse.builder();
        codeList.forEach(errorCode -> errorResponseBuilder.error(
                new ErrorAmqpDto(errorCode.toString(), errorCode.getMessage())
        ));
        return errorResponseBuilder.build();
    }

    private ErrorAmqpResponse jsonProcessingExceptionHandler(JsonProcessingException ex) {
        log.error("PtpbExcetion error: ", ex);
        return ErrorAmqpResponse.builder()
                .error(new ErrorAmqpDto(JSON_PARSE_ERROR.toString(), ex.getMessage()))
                .build();
    }


    private ResponseWrapper wrapAmqpErrorResponse(ErrorAmqpResponse response) {
        String json;
        try {
            json = objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException jsonProcessingException) {
            json = null;
        }

        return ResponseWrapper.builder()
                .isOk(false)
                .data(json)
                .build();
    }
////////////////////////////////////////


}
