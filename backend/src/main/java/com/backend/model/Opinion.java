package com.backend.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "opinions")
public class Opinion {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String description;

    private int stars;

    public Opinion(UUID id, Shop shop, User user, String description, int stars) {
        this.id = id;
        this.shop = shop;
        this.user = user;
        this.description = description;
        this.stars = stars;
    }

    public Opinion() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }
}
