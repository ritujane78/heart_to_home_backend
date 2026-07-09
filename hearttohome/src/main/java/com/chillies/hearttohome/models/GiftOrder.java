package com.chillies.hearttohome.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gift_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who purchased the gift
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Recipient Details
    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String recipientPhone;

    private String recipientEmail;

    @Column(nullable = false)
    private String relationship;

    // Sender Details
    @Column(nullable = false)
    private String senderName;

    @Column(nullable = false)
    private String senderEmail;

    @Column(length = 1000)
    private String message;

    // Selected Services
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "order_services",
            joinColumns = @JoinColumn(name = "order_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "service_id",referencedColumnName = "id")
    )
    @Builder.Default
    private List<ServiceEntity> serviceIds = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private BigDecimal exchangeRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        orderedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (orderStatus == null) {
            orderStatus = OrderStatus.IN_PROCESS;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
