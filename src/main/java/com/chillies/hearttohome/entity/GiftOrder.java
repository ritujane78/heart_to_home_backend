package com.chillies.hearttohome.entity;

import com.chillies.hearttohome.utils.OrderStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gift_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who purchased the gift
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

  // Recipient Details

  @NotBlank(message = "Recipient name is required")
  @Size(max = 100, message = "Recipient name cannot exceed 100 characters")
  @Pattern(
    regexp = "^[\\p{L}]+(?:[ '’-][\\p{L}]+)*$",
    message = "Invalid name"
  )
  @Column(nullable = false)
  private String recipientName;

  @NotBlank(message = "Phone number is required")
  @Pattern(
    regexp = "^(01\\d{6}|9[78]\\d{8})$",
    message = "Invalid Nepal phone number"
  )
  @Column(nullable = false)
  private String recipientPhone;


// Sender Details

  @NotBlank(message = "Sender name is required")
  @Size(max = 100, message = "Sender name cannot exceed 100 characters")
  @Pattern(
    regexp = "^[\\p{L}]+(?:[ '’-][\\p{L}]+)*$",
    message = "Invalid name"
  )
  @Column(nullable = false)
  private String senderName;

  @NotBlank(message = "Sender email is required")
  @Size(max = 50, message = "Email cannot exceed 50 characters")
  @Email(message = "Invalid email address")
  @Column(nullable = false)
  private String senderEmail;


// Optional Message

  @Size(max = 1000, message = "Message cannot exceed 1000 characters")
  @Column(length = 1000)
  private String message;

    @Builder.Default
    @OneToMany(
            mappedBy = "giftOrder",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<OrderService> services = new ArrayList<>();

    @Column(nullable = false)
    private String totalPrice;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false, precision = 38, scale = 6)
    private BigDecimal exchangeRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    private boolean emailSent;

    @Override
    public String toString() {
        return "GiftOrder{" +
                "id=" + id +
                ", recipientName='" + recipientName + '\'' +
                ", recipientPhone='" + recipientPhone + '\'' +
                ", senderName='" + senderName + '\'' +
                ", senderEmail='" + senderEmail + '\'' +
                ", totalPrice=" + totalPrice +
                ", exchangeRate=" + exchangeRate +
                ", createdAt=" + getCreatedAt() +
                ", services=" + services +
                '}';
    }
}
