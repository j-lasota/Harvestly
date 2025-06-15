package com.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.*;

@Entity
@Data
@Table(name = "stores")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Store name cannot be blank")
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

    private String slug;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OwnProduct> ownProducts = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<BusinessHours> businessHours = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Verification> verifications = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Opinion> opinions = new ArrayList<>();

    @ManyToMany(mappedBy = "favoriteStores")
    private Set<User> likedByUsers = new HashSet<>();

    private Boolean reported;

    public Store() {
    }

    public Store(String name, String description, double latitude, double longitude, String city, String address,
                 String imageUrl) {
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.address = address;
        this.imageUrl = imageUrl;
        this.verified = false;
        this.reported = false;
    }

    public Store(User user, String name, String description, double latitude, double longitude, String city, String address, String imageUrl, String slug) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.address = address;
        this.imageUrl = imageUrl;
        this.verified = false;
        this.slug = slug;
        this.reported = false;
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

    public List<Opinion> getOpinions() {
        return opinions;
    }

    public void setOpinions(List<Opinion> opinions) {
        this.opinions = opinions;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Store store)) return false;

        return Double.compare(getLatitude(), store.getLatitude()) == 0 && Double.compare(getLongitude(), store.getLongitude()) == 0 && isVerified() == store.isVerified() && Objects.equals(getName(), store.getName()) && Objects.equals(getDescription(), store.getDescription()) && Objects.equals(getCity(), store.getCity()) && Objects.equals(getAddress(), store.getAddress()) && Objects.equals(getImageUrl(), store.getImageUrl()) && Objects.equals(getOwnProducts(), store.getOwnProducts()) && Objects.equals(getBusinessHours(), store.getBusinessHours()) && Objects.equals(getVerifications(), store.getVerifications());
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
        return "Store{" +
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
