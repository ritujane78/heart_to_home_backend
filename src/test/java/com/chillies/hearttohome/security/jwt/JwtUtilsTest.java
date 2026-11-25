package com.chillies.hearttohome.security.jwt;

import com.chillies.hearttohome.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {
    private JwtUtils jwt;
    private static final String SECRET = "VGhpc0lzQVN1cGVyU2VjcmV0S2V5Rm9yVGVzdGluZ0NhdXNlSXQxMjM0NTY3ODkw";

    @BeforeEach void setup() {
        jwt = new JwtUtils();
        ReflectionTestUtils.setField(jwt, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwt, "jwtExpirationMs", 3600000);
    }

    @Test void readsOnlyBearerAuthorizationHeaders() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertNull(jwt.getJwtFromHeader(request));
        request.addHeader("Authorization", "Basic abc");
        assertNull(jwt.getJwtFromHeader(request));
        MockHttpServletRequest bearerRequest = new MockHttpServletRequest();
        bearerRequest.addHeader("Authorization", "Bearer token-value");
        assertEquals("token-value", jwt.getJwtFromHeader(bearerRequest));
    }

    @Test void generatedUsernameTokenValidatesAndReturnsSubject() {
        String token = jwt.generateTokenFromUsername("asha");
        assertTrue(jwt.validateJwtToken(token));
        assertEquals("asha", jwt.getUserNameFromJwtToken(token));
        assertFalse(jwt.validateJwtToken("not-a-jwt"));
    }

    @Test void userDetailsClaimsAreEncodedInToken() {
        UserDetailsImpl details = new UserDetailsImpl(8L, "asha", "asha@example.com", "pw", false,
                List.of(new SimpleGrantedAuthority("ROLE_USER")), "Asha", "Shah");
        String token = jwt.generateTokenFromUsername(details);
        assertTrue(jwt.validateJwtToken(token));
        assertEquals("asha", jwt.getUserNameFromJwtToken(token));
    }
}
