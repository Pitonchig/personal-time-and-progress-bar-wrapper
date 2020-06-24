package net.thumbtack.ptpb.wrapper.db.todoist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TodoistDao {
    private final TodoistMapper todoistMapper;

    public void insertTodoist(Todoist todoist) {
        todoistMapper.save(todoist);
    }

    public Optional<Todoist> getTodoistByUserUuid(String userUuid) {
        return todoistMapper.findById(userUuid);
    }

    public boolean isTodoistLinked(String userUuid) {
        return todoistMapper.existsById(userUuid);
    }

    public void delete(String userUuid) {
        todoistMapper.deleteById(userUuid);
    }

    public void deleteAllTodoists() {
        todoistMapper.deleteAll();
    }
}
