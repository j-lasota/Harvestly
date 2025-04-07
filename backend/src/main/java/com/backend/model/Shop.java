package com.backend.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shops")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private double Latitude;

    private double Longitude;

    private String City;

    private String address;

    private String openingHours;

    private String imageUrl;

    private boolean isVerified;

    @OneToMany(mappedBy = "shop")
    private List<OwnProduct> ownProducts = new ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<BusinessHours> businessHours = new ArrayList<>();

    public Shop() {
    }

}
