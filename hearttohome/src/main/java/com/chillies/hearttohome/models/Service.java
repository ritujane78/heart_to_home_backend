package com.chillies.hearttohome.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "services")
@Data
public class Service {

    @Id
    private String id;

    private String code;

    private String providerId;

    private String title;

    @Column(length = 2000)
    private String description;

    private Double price;
}