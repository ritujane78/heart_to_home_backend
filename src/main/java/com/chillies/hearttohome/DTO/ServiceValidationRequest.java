package com.chillies.hearttohome.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ServiceValidationRequest {

    private List<Long> serviceIds;
}