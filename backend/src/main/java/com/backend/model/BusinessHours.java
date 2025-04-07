package com.backend.model;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.Objects;

@Entity
public class BusinessHours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private LocalTime openingTime;
    private LocalTime closingTime;

    public BusinessHours() {
    }

    public BusinessHours(Shop shop, DayOfWeek dayOfWeek, LocalTime openingTime, LocalTime closingTime) {
        this.shop = shop;
        this.dayOfWeek = dayOfWeek;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
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

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BusinessHours that)) return false;

        return Objects.equals(getShop(), that.getShop()) && getDayOfWeek() == that.getDayOfWeek() && Objects.equals(getOpeningTime(), that.getOpeningTime()) && Objects.equals(getClosingTime(), that.getClosingTime());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getShop());
        result = 31 * result + Objects.hashCode(getDayOfWeek());
        result = 31 * result + Objects.hashCode(getOpeningTime());
        result = 31 * result + Objects.hashCode(getClosingTime());
        return result;
    }

    @Override
    public String toString() {
        return "BusinessHours{" +
                "id=" + id +
                ", shop=" + shop +
                ", dayOfWeek=" + dayOfWeek +
                ", openingTime=" + openingTime +
                ", closingTime=" + closingTime +
                '}';
    }
}
