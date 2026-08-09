package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.DTO.TokenRefreshRequest;
import com.chillies.hearttohome.entity.AppRole;
import com.chillies.hearttohome.entity.RefreshToken;
import com.chillies.hearttohome.entity.User;
import com.chillies.hearttohome.exceptions.ConflictException;
import com.chillies.hearttohome.repositories.RoleRepository;
import com.chillies.hearttohome.repositories.UserRepository;
import com.chillies.hearttohome.security.request.LoginRequest;
import com.chillies.hearttohome.security.request.SignupRequest;
import com.chillies.hearttohome.services.RefreshTokenService;
import com.chillies.hearttohome.testutil.TestFixtures;
import com.chillies.hearttohome.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    private AuthController controller;
    private UserRepository userRepository;
    private RefreshTokenService refreshTokenService;
    private AuthUtil authUtil;

    @BeforeEach
    void setUp() {
        controller = new AuthController();
        userRepository = mock(UserRepository.class);
        refreshTokenService = mock(RefreshTokenService.class);
        authUtil = mock(AuthUtil.class);

        ReflectionTestUtils.setField(controller, "userRepository", userRepository);
        ReflectionTestUtils.setField(controller, "roleRepository", mock(RoleRepository.class));
        ReflectionTestUtils.setField(controller, "encoder", mock(PasswordEncoder.class));
        ReflectionTestUtils.setField(controller, "authenticationManager", mock(AuthenticationManager.class));
        ReflectionTestUtils.setField(controller, "refreshTokenService", refreshTokenService);
        ReflectionTestUtils.setField(controller, "authUtil", authUtil);
    }

    @Test
    void signupThrowsConflictWhenUsernameAlreadyExists() {
        SignupRequest request = signupRequest();
        when(userRepository.existsByUsername("ritu")).thenReturn(true);

        assertThatThrownBy(() -> controller.registerUser(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Username is already taken");
    }

    @Test
    void signinReturnsNotFoundForBadCredentials() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        ReflectionTestUtils.setField(controller, "authenticationManager", authenticationManager);
        LoginRequest request = new LoginRequest();
        request.setUsername("ritu");
        request.setPassword("wrong");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad credentials"));

        assertThat(controller.authenticateUser(request).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void refreshTokenReturnsNewAccessAndRefreshToken() {
        User user = TestFixtures.user(1L, "ritu", AppRole.ROLE_USER);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("old-refresh");
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(60));
        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken("new-refresh");
        newRefreshToken.setUser(user);
        newRefreshToken.setExpiryDate(Instant.now().plusSeconds(60));
        TokenRefreshRequest request = new TokenRefreshRequest();
        request.setRefreshToken("old-refresh");

        when(refreshTokenService.findByToken("old-refresh")).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
        when(refreshTokenService.createRefreshToken(1L)).thenReturn(newRefreshToken);

        ReflectionTestUtils.setField(controller, "jwtUtils", mock(com.chillies.hearttohome.security.jwt.JwtUtils.class));
        when(((com.chillies.hearttohome.security.jwt.JwtUtils) ReflectionTestUtils.getField(controller, "jwtUtils"))
                .generateTokenFromUsername("ritu")).thenReturn("new-access");

        assertThat(controller.refreshToken(request).getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void logoutDeletesRefreshTokenForCurrentUser() {
        when(authUtil.loggedInUserId()).thenReturn(1L);

        assertThat(controller.logoutUser().getStatusCode().is2xxSuccessful()).isTrue();
        verify(refreshTokenService).deleteByUserId(1L);
    }

    private SignupRequest signupRequest() {
        SignupRequest request = new SignupRequest();
        request.setUsername("ritu");
        request.setFirstName("Ritu");
        request.setLastName("Shrestha");
        request.setEmail("ritu@example.com");
        request.setPassword("password");
        request.setRole(Set.of("user"));
        return request;
    }
}
