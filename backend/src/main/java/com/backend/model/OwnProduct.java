package com.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "own_products")
public class OwnProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Min(0)
    private BigDecimal price;

    @Min(0)
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
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
