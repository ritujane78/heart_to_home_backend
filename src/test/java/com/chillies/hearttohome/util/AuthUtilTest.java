package com.chillies.hearttohome.util;

import com.chillies.hearttohome.entity.AppRole;
import com.chillies.hearttohome.entity.User;
import com.chillies.hearttohome.repositories.UserRepository;
import com.chillies.hearttohome.testutil.TestFixtures;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthUtilTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void loggedInUserIdReturnsAuthenticatedUsersId() {
        UserRepository userRepository = mock(UserRepository.class);
        AuthUtil authUtil = new AuthUtil();
        ReflectionTestUtils.setField(authUtil, "userRepository", userRepository);

        User user = TestFixtures.user(77L, "ritu", AppRole.ROLE_USER);
        when(userRepository.findByUsername("ritu")).thenReturn(Optional.of(user));
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("ritu", "password"));

        assertThat(authUtil.loggedInUserId()).isEqualTo(77L);
        assertThat(authUtil.loggedInUser()).isSameAs(user);
    }
}
