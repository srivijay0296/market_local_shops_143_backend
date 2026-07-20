package com.marketlocalshops.users;

import com.marketlocalshops.roles.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        if (user.getRole() != null) {
            userDTO.setRole(user.getRole().name());
        }
        userDTO.setName(user.getName());
        userDTO.setPhone(user.getPhone());
        userDTO.setAddress(user.getAddress());
        userDTO.setIsApproved(user.getIsApproved());
        userDTO.setCreatedAt(user.getCreatedAt());

        return userDTO;
    }

    @Override
    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User.UserBuilder user = User.builder();
        user.username(userDTO.getUsername());
        user.email(userDTO.getEmail());
        if (userDTO.getRole() != null) {
            user.role(Role.valueOf(userDTO.getRole()));
        }
        user.name(userDTO.getName());
        user.phone(userDTO.getPhone());
        user.address(userDTO.getAddress());
        if (userDTO.getIsApproved() != null) {
            user.isApproved(userDTO.getIsApproved());
        }

        return user.build();
    }
}
