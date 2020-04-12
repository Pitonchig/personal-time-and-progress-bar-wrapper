package net.thumbtack.ptpb.wrapper.db.user;

import java.util.Optional;

public interface UserDao {
    Optional<User> getUserById(long id);

    void insertUser(User user);
}
