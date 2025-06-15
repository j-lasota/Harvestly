package com.backend.model;

import java.time.LocalTime;

public record BusinessHoursInput(
        DayOfWeek dayOfWeek,
        LocalTime openingTime,
        LocalTime closingTime
) {
}
