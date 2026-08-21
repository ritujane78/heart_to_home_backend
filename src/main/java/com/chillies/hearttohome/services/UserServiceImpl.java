package com.chillies.hearttohome.services;



import com.chillies.hearttohome.DTO.UserDTO;
import com.chillies.hearttohome.exceptions.BadRequestException;
import com.chillies.hearttohome.exceptions.EmailSendingException;
import com.chillies.hearttohome.exceptions.ResourceNotFoundException;
import com.chillies.hearttohome.entity.AppRole;
import com.chillies.hearttohome.entity.PasswordResetToken;
import com.chillies.hearttohome.entity.Role;
import com.chillies.hearttohome.entity.User;
import com.chillies.hearttohome.repositories.PasswordResetTokenRepository;
import com.chillies.hearttohome.repositories.RoleRepository;
import com.chillies.hearttohome.repositories.UserRepository;
import com.chillies.hearttohome.utils.EmailService;
import com.chillies.hearttohome.utils.NameUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    @Value("${frontend.url}")
    String frontendUrl;

    @Override
    public void updateUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new ResourceNotFoundException(
                "User",
                "id",
                userId
        ));
        AppRole appRole = AppRole.valueOf(roleName);
        Role role = roleRepository.findByRoleName(appRole)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role",
                                "name",
                                roleName
                        ));
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                "User",
                "id",
                id
        ));
        return convertToDto(user);
    }

    private UserDTO convertToDto(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isAccountNonLocked(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.getTwoFactorSecret(),
                user.isTwoFactorEnabled(),
                user.getSignupMethod(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElseThrow(() -> new ResourceNotFoundException(
                "User",
                "username",
                username
        ));
    }


    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public void updatePassword(Long userId, String password) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User",
                            "id",
                            userId
                    ));
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        } catch (Exception e) {
            throw new BadRequestException(
                    "Failed to update password."
            );
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User registerUser(User user){
        if (user.getPassword() != null)
            user.setPassword(user.getPassword());
        return userRepository.save(user);
    }

    @Override
    public void generatePasswordResetToken(String email) throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User",
                        "email",
                        email
                ));

        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plus(10, ChronoUnit.HOURS);
        PasswordResetToken resetToken = new PasswordResetToken(token, expiryDate, user);
        passwordResetTokenRepository.save(resetToken);

        String resetUrl = frontendUrl + "/reset-password?token=" + token;
        // Send email to user
        try {
            emailService.sendEmailForPasswordReset(
                    NameUtils.formatFirstName(user.getFirstName()),
                    user.getEmail(),
                    resetUrl
            );
        } catch (EmailSendingException ex) {
            log.error(
                    "Unable to send password reset email to {}",
                    user.getEmail(),
                    ex
            );
            throw ex;
        }
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid password reset token."));
        if(resetToken.isUsed()) {
            throw new BadRequestException(
                    "Password reset token has already been used."
            );
        }

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new BadRequestException(
                    "Password reset token has expired."
            );
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Invalidate the token after use
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    @Override
    public void updateAccountExpiryStatus(Long userId, boolean expire) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User",
                                "id",
                                userId
                        ));
        user.setAccountNonExpired(!expire);
        userRepository.save(user);
    }

    @Override
    public void updateAccountEnabledStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User",
                                "id",
                                userId
                        ));
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    @Override
    public void updateCredentialsExpiryStatus(Long userId, boolean expire) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User",
                                "id",
                        userId
                ));
        user.setCredentialsNonExpired(!expire);
        userRepository.save(user);
    }
    @Override
    public void updateAccountLockStatus(Long userId, boolean lock) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User",
                                "id",
                                userId
                        ));
        user.setAccountNonLocked(!lock);
        userRepository.save(user);
    }

}
