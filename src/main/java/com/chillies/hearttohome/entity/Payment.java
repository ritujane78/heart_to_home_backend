package com.chillies.hearttohome.entity;

import lombok.Data;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payment")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private String paymentIntentId;

    @Column(name="user_email")
    private String userEmail;

    @Column(name = "total")
    private String total;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amountNpr;

    @Column(nullable = false)
    private String payerName;
}
