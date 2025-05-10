package com.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@Entity
public class BusinessHours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @NotNull(message = "Day of week cannot be null")
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Opening time cannot be blank")
    private LocalTime openingTime;

    @NotNull(message = "Closing time cannot be blank")
    private LocalTime closingTime;

    public BusinessHours(Store store, DayOfWeek dayOfWeek, LocalTime openingTime, LocalTime closingTime) {
        this.store = store;
        this.dayOfWeek = dayOfWeek;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

}
