package com.chillies.hearttohome.DTO;

import com.chillies.hearttohome.models.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class AllOrdersDTO {
    private Long id;
    private String senderEmail;
    private LocalDateTime orderedAt;
    private OrderStatus orderStatus;
    public AllOrdersDTO(Long id,
                        String senderEmail,
                        LocalDateTime orderedAt,
                        OrderStatus orderStatus) {
        this.id = id;
        this.senderEmail = senderEmail;
        this.orderedAt = orderedAt;
        this.orderStatus = orderStatus;
    }
}
