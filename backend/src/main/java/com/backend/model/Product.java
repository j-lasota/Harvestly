package com.backend.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;


    private boolean isVerified;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @OneToMany(mappedBy = "product")
    private List<OwnProduct> ownProducts = new ArrayList<>();;

    public Product() {
    }

    public Product(String name, boolean isVerified, ProductCategory category) {
        this.name = name;
        this.isVerified = isVerified;
        this.category = category;
    }

    public Product(String name, ProductCategory category) {
        this.isVerified = false;
        this.name = name;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public List<OwnProduct> getOwnProducts() {
        return ownProducts;
    }

    public void setOwnProducts(List<OwnProduct> ownProducts) {
        this.ownProducts = ownProducts;
    }

    public void addOwnProduct(OwnProduct ownProduct) {
        this.ownProducts.add(ownProduct);
    }

    public void verify() {
        this.isVerified = true;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;

        return isVerified() == product.isVerified() && getName().equals(product.getName()) && getCategory() == product.getCategory() && Objects.equals(getOwnProducts(), product.getOwnProducts());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + Boolean.hashCode(isVerified());
        result = 31 * result + getCategory().hashCode();
        result = 31 * result + Objects.hashCode(getOwnProducts());
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isVerified=" + isVerified +
                ", category=" + category +
                ", ownProducts=" + ownProducts +
                '}';
    }
}
