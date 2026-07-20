package com.marketlocalshops.users;

public interface UserMapper {
    UserDTO toDto(User user);
    User toEntity(UserDTO userDTO);
}
