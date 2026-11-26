
package com.chillies.hearttohome.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 20,
    message = "Username must be between 3 and 20 characters")
  @Pattern(
    regexp = "^[a-zA-Z0-9_]+$",
    message = "Username can contain only letters, numbers and underscores"
  )
  private String username;

  @NotBlank(message = "First name is required")
  @Size(max = 20,
    message = "First name cannot exceed 20 characters")
  @Pattern(
    regexp = "^[\\p{L}]+(?:[ '\\u2019-][\\p{L}]+)*$",
    message = "Invalid first name"
  )
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(max = 20,
    message = "Last name cannot exceed 20 characters")
  @Pattern(
    regexp = "^[\\p{L}]+(?:[ '\\u2019-][\\p{L}]+)*$",
    message = "Invalid last name"
  )
  private String lastName;

  @NotBlank(message = "Email is required")
  @Size(max = 50,
    message = "Email cannot exceed 50 characters")
  @Email(message = "Invalid email address")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 40,
    message = "Password must be between 8 and 40 characters")
  private String password;
}
