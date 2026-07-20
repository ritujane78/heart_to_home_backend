package com.chillies.hearttohome.services;



import com.chillies.hearttohome.DTO.UserDTO;
import com.chillies.hearttohome.models.Role;
import com.chillies.hearttohome.models.User;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
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

    void generatePasswordResetToken(String email) throws MessagingException, UnsupportedEncodingException;

    void resetPassword(String token, String newPassword);

    void updateAccountExpiryStatus(Long userId, boolean expire);

    void updateAccountEnabledStatus(Long userId, boolean enabled);

    void updateCredentialsExpiryStatus(Long userId, boolean expire);

    void updateAccountLockStatus(Long userId, boolean lock);
}
