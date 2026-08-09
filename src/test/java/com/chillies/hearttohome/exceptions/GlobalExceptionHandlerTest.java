package com.chillies.hearttohome.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void resourceNotFoundMapsTo404() {
        var response = handler.handleResourceNotFound(
                new ResourceNotFoundException("Service", "id", 99L),
                request("/api/services/99")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("Service");
    }

    @Test
    void conflictMapsFieldErrorTo409ValidationResponse() {
        var response = handler.handleConflictException(
                new ConflictException("title", "Title already exists."),
                request("/api/admin/add-service")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getValidationErrors())
                .containsEntry("title", "Title already exists.");
    }

    @Test
    void accessDeniedMapsToForbidden() {
        var response = handler.handleAccessDenied(
                new AccessDeniedException("denied"),
                request("/api/admin/getusers")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getMessage()).isEqualTo("Access denied.");
    }

    private HttpServletRequest request(String uri) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(uri);
        return request;
    }
}
