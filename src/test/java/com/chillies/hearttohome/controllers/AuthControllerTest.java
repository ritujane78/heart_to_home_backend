package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.security.request.LoginRequest;
import com.chillies.hearttohome.services.RefreshTokenService;
import com.chillies.hearttohome.services.UserService;
import com.chillies.hearttohome.utils.AuthUtil;
import com.chillies.hearttohome.utils.EmailService;
import com.chillies.hearttohome.mapper.UserMapper;
import com.chillies.hearttohome.repositories.RoleRepository;
import com.chillies.hearttohome.repositories.UserRepository;
import com.chillies.hearttohome.security.jwt.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock JwtUtils jwtUtils;
    @Mock AuthenticationManager authenticationManager;
    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock PasswordEncoder encoder;
    @Mock UserService userService;
    @Mock RefreshTokenService refreshTokenService;
    @Mock AuthUtil authUtil;
    @Mock EmailService emailService;
    @Mock UserMapper userMapper;
    @Mock CompromisedPasswordChecker compromisedPasswordChecker;
    @Mock UserDetails principal;
    @InjectMocks AuthController controller;

    @Test void returnsUsernameAndEmptyStringWithoutPrincipal() {
        when(principal.getUsername()).thenReturn("asha");
        assertEquals("asha", controller.getUsername(principal));
        assertEquals("", controller.getUsername(null));
    }

    @Test void returnsBadCredentialsResponseWhenAuthenticationFails() {
        LoginRequest request = new LoginRequest();
        request.setUsername("asha");
        request.setPassword("wrong");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));
        var response = controller.authenticateUser(request);
        assertEquals(404, response.getStatusCode().value());
        assertEquals(false, ((java.util.Map<?, ?>) response.getBody()).get("status"));
        assertEquals("Bad credentials", ((java.util.Map<?, ?>) response.getBody()).get("message"));
    }

    @Test void returnsOkWhenPasswordResetEmailWasRequested() throws Exception {
        var response = controller.forgotPassword("asha@example.com");
        assertEquals(200, response.getStatusCode().value());
        verify(userService).generatePasswordResetToken("asha@example.com");
    }

    @Test void mapsPasswordResetFailureToBadRequest() {
        doThrow(new IllegalArgumentException("invalid token")).when(userService).resetPassword("bad", "password");
        var response = controller.resetPassword("bad", "password");
        assertEquals(400, response.getStatusCode().value());
        assertEquals("invalid token", ((com.chillies.hearttohome.security.response.MessageResponse) response.getBody()).getMessage());
    }
}
