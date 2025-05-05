package com.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.*;

@Data
@Entity
@Table(name = "shops")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Shop name cannot be blank")
    private String name;

    private String description;

    @DecimalMin(value = "-90", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90", message = "Latitude must be between -90 and 90")
    private double latitude;

    @DecimalMin(value = "-180", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180", message = "Longitude must be between -180 and 180")
    private double longitude;

    @NotBlank(message = "City cannot be blank")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "City name contains invalid characters")
    private String city;

    @NotBlank(message = "Address cannot be blank")
    @Pattern(regexp = "^[\\p{L}\\d\\s.,'\\-/#]+$", message = "Address contains invalid characters")
    private String address;

    private String imageUrl;

    private boolean verified;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OwnProduct> ownProducts = new ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<BusinessHours> businessHours = new ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Verification> verifications = new ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Opinion> opinions = new ArrayList<>();

    @ManyToMany(mappedBy = "favoriteShops")
    private Set<User> likedByUsers = new HashSet<>();

    public Shop() {
    }

    public Shop(String name, String description, double latitude, double longitude, String city, String address,
                String imageUrl) {
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.address = address;
        this.imageUrl = imageUrl;
        this.verified = false;
    }

    public void addVerification(Verification verification) {
        this.verifications.add(verification);
        if(verifications.size() >= 5) {
            this.verified = true;
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shop shop)) return false;

        return Double.compare(getLatitude(), shop.getLatitude()) == 0 && Double.compare(getLongitude(), shop.getLongitude()) == 0 && isVerified() == shop.isVerified() && Objects.equals(getName(), shop.getName()) && Objects.equals(getDescription(), shop.getDescription()) && Objects.equals(getCity(), shop.getCity()) && Objects.equals(getAddress(), shop.getAddress()) && Objects.equals(getImageUrl(), shop.getImageUrl()) && Objects.equals(getOwnProducts(), shop.getOwnProducts()) && Objects.equals(getBusinessHours(), shop.getBusinessHours()) && Objects.equals(getVerifications(), shop.getVerifications());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Double.hashCode(getLatitude());
        result = 31 * result + Double.hashCode(getLongitude());
        result = 31 * result + Objects.hashCode(getCity());
        result = 31 * result + Objects.hashCode(getAddress());
        result = 31 * result + Objects.hashCode(getImageUrl());
        result = 31 * result + Boolean.hashCode(isVerified());
        result = 31 * result + Objects.hashCode(getOwnProducts());
        result = 31 * result + Objects.hashCode(getBusinessHours());
        result = 31 * result + Objects.hashCode(getVerifications());
        return result;
    }

    @Override
    public String toString() {
        return "Shop{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", verified=" + verified +
                ", ownProducts=" + ownProducts +
                ", businessHours=" + businessHours +
                ", verifications=" + verifications +
                '}';
    }
}
