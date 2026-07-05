package com.chillies.hearttohome.services;



import com.chillies.hearttohome.DTO.UserDTO;
import com.chillies.hearttohome.models.Role;
import com.chillies.hearttohome.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void updateUserRole(Long userId, String roleName);

    List<User> getAllUsers();

    UserDTO getUserById(Long id);

    User findByUsername(String username);

    List<Role> getAllRoles();

    void updatePassword(Long userId, String password);

    Optional<User> findByEmail(String email);

    User registerUser(User user);

    void generatePasswordResetToken(String email);

    void resetPassword(String token, String newPassword);
}
