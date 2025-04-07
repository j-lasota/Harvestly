package com.backend.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "shops")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "shop")
    private List<OwnProduct> ownProducts;
}
