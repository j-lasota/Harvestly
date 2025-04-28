package com.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private BigDecimal price;

    private int quantity;

    private String imageUrl;

    public OwnProduct() {
    }

    public OwnProduct(Long id, Shop shop, Product product, BigDecimal price, int quantity, String imageUrl) {
        this.id = id;
        this.shop = shop;
        this.product = product;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public OwnProduct(Shop shop, Product product, BigDecimal price, int quantity, String imageUrl) {
        this.shop = shop;
        this.product = product;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }


    @Override
    public String toString() {
        return "OwnProduct{" +
                "id=" + id +
                ", shop=" + shop +
                ", product=" + product +
                ", price=" + price +
                ", quantity=" + quantity +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof OwnProduct that)) return false;

        return getQuantity() == that.getQuantity() && Objects.equals(getShop(), that.getShop()) && Objects.equals(getProduct(), that.getProduct()) && Objects.equals(getPrice(), that.getPrice()) && Objects.equals(getImageUrl(), that.getImageUrl());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getShop());
        result = 31 * result + Objects.hashCode(getProduct());
        result = 31 * result + Objects.hashCode(getPrice());
        result = 31 * result + getQuantity();
        result = 31 * result + Objects.hashCode(getImageUrl());
        return result;
    }
}
