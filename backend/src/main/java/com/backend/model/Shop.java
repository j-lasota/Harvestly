package com.backend.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "shops")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private double latitude;

    private double longitude;

    private String city;

    private String address;

    private String imageUrl;

    private boolean verified;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OwnProduct> ownProducts = new ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<BusinessHours> businessHours = new ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Verification> verifications = new ArrayList<>();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public List<OwnProduct> getOwnProducts() {
        return ownProducts;
    }

    public void setOwnProducts(List<OwnProduct> ownProducts) {
        this.ownProducts = ownProducts;
    }

    public List<BusinessHours> getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(List<BusinessHours> businessHours) {
        this.businessHours = businessHours;
    }

    public List<Verification> getVerifications() {
        return verifications;
    }

    public void setVerifications(List<Verification> verifications) {
        this.verifications = verifications;
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
