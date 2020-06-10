package net.thumbtack.ptpb.wrapper.service.synchronization.structmappers;

import net.thumbtack.ptpb.wrapper.client.user.UserDto;
import net.thumbtack.ptpb.wrapper.db.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserStructMapper {

    static public User toUser(UserDto dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return User.builder()
                .id(dto.getId())
                .name(dto.getFullName())
                .registered(LocalDateTime.parse(dto.getRegistered(), formatter))
                .build();
    }

    static public UserDto fromUser(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getName())
                .registered(user.getRegistered().format(formatter))
                .build();
    }


}
