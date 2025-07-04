package com.backend.repository;

import com.backend.model.DailyClickCount;
import com.backend.model.DailyClickCount.Key;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DailyClickCountRepository extends JpaRepository<DailyClickCount, Key> {

    @Modifying(clearAutomatically = true)
    @Query(value = """
        INSERT INTO daily_click_count (
          store_slug,
          day,
          store_page_clicks,
          map_pin_clicks
        )
        VALUES (
          :slug,
          :day,
          1,
          0
        )
        ON CONFLICT (store_slug, day)
        DO UPDATE
          SET store_page_clicks = daily_click_count.store_page_clicks + 1
        """, nativeQuery = true)
    void upsertStorePageClick(
            @Param("slug") String slug,
            @Param("day") LocalDate day
    );

    @Modifying(clearAutomatically = true)
    @Query(value = """
        INSERT INTO daily_click_count (
          store_slug,
          day,
          store_page_clicks,
          map_pin_clicks
        )
        VALUES (
          :slug,
          :day,
          0,
          1
        )
        ON CONFLICT (store_slug, day)
        DO UPDATE
          SET map_pin_clicks = daily_click_count.map_pin_clicks + 1
        """, nativeQuery = true)
    void upsertMapPinClick(
            @Param("slug") String slug,
            @Param("day") LocalDate day
    );

    @Query(value = """
        SELECT
          COALESCE(SUM(map_pin_clicks), 0)
        FROM daily_click_count
        WHERE store_slug = :slug
        """, nativeQuery = true)
    long totalMapPinClicks(@Param("slug") String slug);

    @Query(value = """
        SELECT
          COALESCE(SUM(store_page_clicks), 0)
        FROM daily_click_count
        WHERE store_slug = :slug
        """, nativeQuery = true)
    long totalStorePageClicks(@Param("slug") String slug);

    @Query(value = """
        SELECT
          COALESCE(SUM(map_pin_clicks), 0)
        FROM daily_click_count
        WHERE store_slug = :slug
          AND day >= :fromDate
        """, nativeQuery = true)
    long mapPinClicksSince(
            @Param("slug") String slug,
            @Param("fromDate") LocalDate fromDate
    );

    @Query(value = """
        SELECT
          COALESCE(SUM(store_page_clicks), 0)
        FROM daily_click_count
        WHERE store_slug = :slug
          AND day >= :fromDate
        """, nativeQuery = true)
    long storePageClicksSince(
            @Param("slug") String slug,
            @Param("fromDate") LocalDate fromDate
    );
}