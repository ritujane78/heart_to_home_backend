package com.chillies.hearttohome.security;

import com.chillies.hearttohome.entity.AppRole;
import com.chillies.hearttohome.security.services.UserDetailsImpl;
import com.chillies.hearttohome.testutil.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsImplTest {

    @Test
    void buildMapsUserRoleToGrantedAuthority() {
        UserDetailsImpl details = UserDetailsImpl.build(
                TestFixtures.user(1L, "admin", AppRole.ROLE_ADMIN)
        );

        assertThat(details.getUsername()).isEqualTo("admin");
        assertThat(details.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }
}
