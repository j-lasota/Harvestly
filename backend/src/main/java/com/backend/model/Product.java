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

    private boolean verified;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;



    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OwnProduct> ownProducts = new ArrayList<>();;

    public Product() {
    }

    public Product(Long id, String name, ProductCategory category, boolean verified) {
        this.id = id;
        this.name = name;
        this.verified = verified;
        this.category = category;
    }
    public Product(String name, ProductCategory category, boolean verified) {
        this.name = name;
        this.verified = verified;
        this.category = category;
    }

    public Product(String name, ProductCategory category) {
        this.verified = false;
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
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
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
        this.verified = true;
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
                ", isVerified=" + verified +
                ", category=" + category +
                ", ownProducts=" + ownProducts +
                '}';
    }
}
