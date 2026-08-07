package com.chillies.hearttohome.mapper;

import com.chillies.hearttohome.entity.Role;
import com.chillies.hearttohome.entity.User;
import com.chillies.hearttohome.security.response.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "role")
    UserInfoResponse toResponse(User user);

    default List<String> map(Role role) {
        if (role == null) {
            return Collections.emptyList();
        }

        return Collections.singletonList(role.getRoleName().name());
    }
}