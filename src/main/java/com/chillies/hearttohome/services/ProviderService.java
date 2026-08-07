package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.ProviderRequest;
import com.chillies.hearttohome.DTO.ProviderResponse;

import java.util.List;

public interface ProviderService {

    List<ProviderResponse> getProviders();

    ProviderResponse addProvider(ProviderRequest providerRequest);
}