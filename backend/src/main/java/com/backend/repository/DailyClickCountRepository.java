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
public interface DailyClickCountRepository
        extends JpaRepository<DailyClickCount, Key> {

    @Modifying(clearAutomatically = true)
    @Query(value = """
        INSERT INTO daily_click_count(store_id, day, store_page_clicks, map_pin_clicks)
        VALUES(:storeId, :day, 1, 0)
        ON CONFLICT (store_id, day)
        DO UPDATE SET store_page_clicks = daily_click_count.store_page_clicks + 1
        """, nativeQuery = true)
    void upsertStorePageClick(@Param("storeId") Long storeId,
                              @Param("day") LocalDate day);

    @Modifying(clearAutomatically = true)
    @Query(value = """
        INSERT INTO daily_click_count(store_id, day, store_page_clicks, map_pin_clicks)
        VALUES(:storeId, :day, 0, 1)
        ON CONFLICT (store_id, day)
        DO UPDATE SET map_pin_clicks = daily_click_count.map_pin_clicks + 1
        """, nativeQuery = true)
    void upsertMapPinClick(@Param("storeId") Long storeId,
                           @Param("day") LocalDate day);
}