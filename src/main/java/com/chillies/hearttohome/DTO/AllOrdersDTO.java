package com.chillies.hearttohome.DTO;

import com.chillies.hearttohome.utils.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class AllOrdersDTO {
    private Long id;
    private String senderEmail;
    private OrderStatus orderStatus;
    private Instant createdAt;

    public AllOrdersDTO(Long id,
                        String senderEmail,
                        OrderStatus orderStatus,
                        Instant createdAt) {
        this.id = id;
        this.senderEmail = senderEmail;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
    }
}
