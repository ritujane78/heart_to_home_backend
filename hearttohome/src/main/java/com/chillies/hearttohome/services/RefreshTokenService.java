package com.chillies.hearttohome.services;

import com.chillies.hearttohome.models.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(Long userId);

    RefreshToken verifyExpiration(RefreshToken token);

    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(Long userId);

    void revokeToken(String token);
}