package com.chillies.hearttohome.security;

import com.chillies.hearttohome.security.jwt.JwtUtils;
import com.chillies.hearttohome.security.services.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(
                jwtUtils,
                "jwtSecret",
                "dGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQ="
        );
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3_600_000);
    }

    @Test
    void generateTokenFromUserDetailsIncludesSubjectAndValidSignature() {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L,
                "ritu",
                "ritu@example.com",
                "encoded",
                false,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")),
                "Ritu",
                "Shrestha"
        );

        String token = jwtUtils.generateTokenFromUsername(userDetails);

        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
        assertThat(jwtUtils.getUserNameFromJwtToken(token)).isEqualTo("ritu");
    }

    @Test
    void getJwtFromHeaderExtractsBearerToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer abc.def.ghi");

        assertThat(jwtUtils.getJwtFromHeader(request)).isEqualTo("abc.def.ghi");
    }

    @Test
    void validateJwtTokenReturnsFalseForMalformedToken() {
        assertThat(jwtUtils.validateJwtToken("not-a-token")).isFalse();
    }
}
