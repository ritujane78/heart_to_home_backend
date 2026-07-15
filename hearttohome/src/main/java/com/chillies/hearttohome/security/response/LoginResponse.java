package com.chillies.hearttohome.security.response;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {
    private String jwtToken;
    private String refreshToken;  // Add this field
    private String username;
    private List<String> roles;

    public LoginResponse(String username, List<String> roles, String jwtToken, String refreshToken) {
        this.username = username;
        this.roles = roles;
        this.jwtToken = jwtToken;
        this.refreshToken = refreshToken;
    }

    // Keep the old constructor for backward compatibility
    public LoginResponse(String username, List<String> roles, String jwtToken) {
        this.username = username;
        this.roles = roles;
        this.jwtToken = jwtToken;
    }
}

