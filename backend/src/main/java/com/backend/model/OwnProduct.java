package com.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "own_products")
public class OwnProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Min(0)
    private BigDecimal basePrice;

    @Min(0)
    private BigDecimal price;

    @Min(0)
    @Max(100)
    private int discount;

    @Min(0)
    private int quantity;

    private String imageUrl;

    public OwnProduct() {
    }

    public OwnProduct(Long id, Store store, Product product, BigDecimal price, int quantity, String imageUrl) {
        this.id = id;
        this.store = store;
        this.product = product;
        this.basePrice = price;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.discount = 0; // Default discount value
    }

    public OwnProduct(Store store, Product product, BigDecimal price, int quantity, String imageUrl) {
        this.store = store;
        this.product = product;
        this.basePrice = price;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.discount = 0; // Default discount value
    }

    public void setPriceAfterDiscount(int discount) {
        if(discount == 0) {
            this.price = this.basePrice;
        }
        else {
            this.discount = discount;
            BigDecimal multiplier = BigDecimal.valueOf(1 - discount / 100.0);
            this.price = this.basePrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        }
    }

    @Override
    public String toString() {
        return "OwnProduct{" +
                "id=" + id +
                ", store=" + store +
                ", product=" + product +
                ", price=" + price +
                ", quantity=" + quantity +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof OwnProduct that)) return false;

        return getQuantity() == that.getQuantity() && Objects.equals(getStore(), that.getStore()) && Objects.equals(getProduct(), that.getProduct()) && Objects.equals(getPrice(), that.getPrice()) && Objects.equals(getImageUrl(), that.getImageUrl());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getStore());
        result = 31 * result + Objects.hashCode(getProduct());
        result = 31 * result + Objects.hashCode(getPrice());
        result = 31 * result + getQuantity();
        result = 31 * result + Objects.hashCode(getImageUrl());
        return result;
    }
}
