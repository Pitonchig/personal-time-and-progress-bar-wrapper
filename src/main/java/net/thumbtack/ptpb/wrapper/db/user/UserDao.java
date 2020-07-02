package net.thumbtack.ptpb.wrapper.db.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDao {
    private final UserMapper userMapper;

    public Optional<User> getUserById(String id) {
        return userMapper.findById(id);
    }

    public void insertUsers(List<User> users) {
        userMapper.saveAll(users);
    }

    public void insertUser(User user) {
        userMapper.save(user);
    }
}
