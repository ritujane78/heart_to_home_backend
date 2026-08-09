package com.chillies.hearttohome.services;

import com.chillies.hearttohome.entity.AppRole;
import com.chillies.hearttohome.entity.RefreshToken;
import com.chillies.hearttohome.entity.User;
import com.chillies.hearttohome.exceptions.BadRequestException;
import com.chillies.hearttohome.exceptions.ResourceNotFoundException;
import com.chillies.hearttohome.repositories.RefreshTokenRepository;
import com.chillies.hearttohome.repositories.UserRepository;
import com.chillies.hearttohome.testutil.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefreshTokenServiceImplTest {

    private RefreshTokenRepository refreshTokenRepository;
    private UserRepository userRepository;
    private RefreshTokenServiceImpl refreshTokenService;

    @BeforeEach
    void setUp() {
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        userRepository = mock(UserRepository.class);
        refreshTokenService = new RefreshTokenServiceImpl();
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenRepository", refreshTokenRepository);
        ReflectionTestUtils.setField(refreshTokenService, "userRepository", userRepository);
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 60_000L);
    }

    @Test
    void createRefreshTokenDeletesExistingTokenAndSavesNewOne() {
        User user = TestFixtures.user(1L, "ritu", AppRole.ROLE_USER);
        RefreshToken existing = new RefreshToken();
        existing.setUser(user);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.of(existing));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken token = refreshTokenService.createRefreshToken(1L);

        assertThat(token.getUser()).isSameAs(user);
        assertThat(token.getToken()).isNotBlank();
        assertThat(token.isRevoked()).isFalse();
        assertThat(token.getExpiryDate()).isAfter(Instant.now());
        verify(refreshTokenRepository).delete(existing);
    }

    @Test
    void verifyExpirationDeletesExpiredTokenAndThrows() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().minusSeconds(1));

        assertThatThrownBy(() -> refreshTokenService.verifyExpiration(token))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("expired");
        verify(refreshTokenRepository).delete(token);
    }

    @Test
    void verifyExpirationRejectsRevokedToken() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusSeconds(60));
        token.setRevoked(true);

        assertThatThrownBy(() -> refreshTokenService.verifyExpiration(token))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("revoked");
    }

    @Test
    void revokeTokenMarksTokenRevoked() {
        RefreshToken token = new RefreshToken();
        token.setToken("refresh-token");
        when(refreshTokenRepository.findByToken("refresh-token")).thenReturn(Optional.of(token));

        refreshTokenService.revokeToken("refresh-token");

        assertThat(token.isRevoked()).isTrue();
        verify(refreshTokenRepository).save(token);
    }

    @Test
    void deleteByUserIdThrowsWhenUserMissing() {
        when(userRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.deleteByUserId(404L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
