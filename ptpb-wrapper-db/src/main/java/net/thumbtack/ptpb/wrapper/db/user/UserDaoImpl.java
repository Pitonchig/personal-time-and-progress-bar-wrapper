package net.thumbtack.ptpb.wrapper.db.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {
    private final UserMapper userMapper;

    @Override
    public Optional<User> getUserById(long id) {
        return userMapper.findById(id);
    }

    @Override
    public void insertUser(User user) {
        userMapper.save(user);
    }
}
