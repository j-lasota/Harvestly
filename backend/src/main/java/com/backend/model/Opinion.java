package com.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "opinions")
public class Opinion {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false, updatable = false)
    private Store store;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String description;

    @NotNull
    @Min(1)
    @Max(5)
    private int stars;

    private boolean reported;

    public Opinion(Store store, User user, String description, int stars) {
        this.store = store;
        this.user = user;
        this.description = description;
        this.stars = stars;
        this.reported = false;
    }

}
