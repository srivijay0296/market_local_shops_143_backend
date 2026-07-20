package com.marketlocalshops.users;

import java.util.List;
public interface UserService {
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    UserDTO createUser(UserDTO userDTO);
    void deleteUser(Long id);
}
