package com.chillies.hearttohome.services;

import com.chillies.hearttohome.models.ProviderEntity;

import java.util.List;

public interface ProviderService {

    List<ProviderEntity> getProviders();

    ProviderEntity addProvider(ProviderEntity provider);
}