package com.chillies.hearttohome.DTO;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CheckoutRequest {

  @NotBlank(message = "Checkout ID is required")
  private String checkoutId;

  @NotBlank(message = "Recipient name is required")
  @Size(max = 100, message = "Recipient name cannot exceed 100 characters")
  @Pattern(
    regexp = "^[\\p{L}]+(?:[ '’-][\\p{L}]+)*$",
    message = "Invalid recipient name"
  )
  private String recipientName;

  @NotBlank(message = "Phone number is required")
  @Pattern(
    regexp = "^(01\\d{6}|9[78]\\d{8})$",
    message = "Invalid Nepal phone number"
  )
  private String recipientPhone;

  @NotBlank(message = "Relationship is required")
  @Size(max = 50, message = "Relationship cannot exceed 50 characters")
  private String relationship;

  @NotBlank(message = "Sender name is required")
  @Size(max = 100, message = "Sender name cannot exceed 100 characters")
  @Pattern(
    regexp = "^[\\p{L}]+(?:[ '’-][\\p{L}]+)*$",
    message = "Invalid sender name"
  )
  private String senderName;

  @NotBlank(message = "Sender email is required")
  @Size(max = 50, message = "Email cannot exceed 50 characters")
  @Email(message = "Invalid email address")
  private String senderEmail;

  @Size(max = 1000, message = "Message cannot exceed 1000 characters")
  private String message;

  @NotEmpty(message = "Please select at least one service")
  private List<@NotNull(message = "Invalid service selection") @Positive(message = "Invalid service ID") Long> serviceIds;

  @NotBlank(message = "Currency is required")
  @Pattern(
    regexp = "^(USD|GBP|EUR|AUD|CAD|JPY|NPR)$",
    message = "Unsupported currency"
  )
  private String currency;
}
