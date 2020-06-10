package net.thumbtack.ptpb.wrapper.client.user;

import lombok.RequiredArgsConstructor;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistClientService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserClient {

    private final TodoistClientService client;


}
