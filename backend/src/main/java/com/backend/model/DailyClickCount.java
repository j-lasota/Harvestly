package com.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "daily_click_count")
@Getter @Setter @NoArgsConstructor
public class DailyClickCount {

    @EmbeddedId
    private Key id;

    @Column(name = "store_page_clicks", nullable = false)
    private long storePageClicks;

    @Column(name = "map_pin_clicks", nullable = false)
    private long mapPinClicks;

    public DailyClickCount(String storeSlug, LocalDate day) {
        this.id = new Key(storeSlug, day);
        this.storePageClicks = 0;
        this.mapPinClicks = 0;
    }

    @Embeddable
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Key {

        @Column(name = "store_slug", nullable = false)
        private String storeSlug;

        @Column(name = "day", nullable = false)
        private LocalDate day;
    }
}