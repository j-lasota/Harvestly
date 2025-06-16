package com.backend.repository;

import com.backend.model.Store;
import com.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Store getByName(String name);
    Store findBySlug(String slug);
    List<Store> findAllByReportedTrue();

    List<Store> findByUserId(String userId);
    /**
     * Znajduje wszystkie sklepy, których właścicielem jest jeden z użytkowników
     * znajdujących się na podanej liście.
     *
     * @param users Lista obiektów użytkowników.
     * @return Lista pasujących sklepów.
     */
    List<Store> findByUserIn(List<User> users);
}
