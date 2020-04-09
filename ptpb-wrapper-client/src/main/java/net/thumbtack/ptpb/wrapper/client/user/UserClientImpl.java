package net.thumbtack.ptpb.wrapper.client.user;

import lombok.RequiredArgsConstructor;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistClientImpl;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserClientImpl implements UserClient {

    private final TodoistClientImpl client;


}
