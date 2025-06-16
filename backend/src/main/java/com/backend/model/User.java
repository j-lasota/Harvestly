package com.backend.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = {"id", "firstName", "lastName", "email", "phoneNumber", "tier", "img"})
@Table(name = "users")
public class User {

    @Id
    private String id;

    private String firstName;

    private String lastName;

    //Nickname
    private String name;

    @Nullable
    @Column(unique = true)
    @Email(message = "Email must be valid")
    private String email;

    @Nullable
    @Column(unique = true)
    @Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$", message = "Phone number must be valid")
    private String phoneNumber;

    @Min(0)
    @Max(1)
    private int tier;
    private String img;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Store> stores = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_favorite_stores",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "store_id")
    )
    private Set<Store> favoriteStores = new HashSet<>();

    private String facebook_nickname;

    public User() {
    }

    public User(String id, String firstName, String lastName, String email, String phoneNumber, int tier, String img) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.tier = tier;
        this.img = img;
    }

    public User(String id, String firstName, String lastName,String nickName, String email, String phoneNumber, int tier, String img) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = nickName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.tier = tier;
        this.img = img;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", tier=" + tier +
                ", img='" + img + '\'' +
                '}';
    }


}

