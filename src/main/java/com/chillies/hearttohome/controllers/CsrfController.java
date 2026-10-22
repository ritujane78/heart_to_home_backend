
package com.chillies.hearttohome.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CsrfController {

  @GetMapping("/api/csrf-token")
  public Map<String, String> csrfToken(HttpServletRequest request) {
    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    return Map.of(
      "headerName", csrfToken.getHeaderName(),
      "token", csrfToken.getToken()
    );
  }
}
