package com.library.repository;

import java.util.List;
import java.util.Optional;

/**
 * Storage contract. Services depend on this abstraction, not on how data is stored
 * (in memory today, maybe a database tomorrow).
 */
public interface Repository<T> {

    void save(T entity);

    Optional<T> findById(String id);

    List<T> findAll();
}
