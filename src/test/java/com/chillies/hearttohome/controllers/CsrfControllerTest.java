package com.chillies.hearttohome.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.csrf.CsrfToken;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsrfControllerTest {
    @Test void exposesCsrfHeaderNameAndTokenFromRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(CsrfToken.class.getName(), new DefaultCsrfToken("X-XSRF-TOKEN", "_csrf", "csrf-value"));
        var result = new CsrfController().csrfToken(request);
        assertEquals("X-XSRF-TOKEN", result.get("headerName"));
        assertEquals("csrf-value", result.get("token"));
    }
}
