package com.chillies.hearttohome.DTO;

import com.chillies.hearttohome.utils.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AllOrdersDTO {
    private Long id;
    private String senderEmail;
    private OrderStatus orderStatus;

    public AllOrdersDTO(Long id,
                        String senderEmail,
                        OrderStatus orderStatus) {
        this.id = id;
        this.senderEmail = senderEmail;
        this.orderStatus = orderStatus;
    }
}
