package com.library.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * Generic in-memory storage: one class works for items, members and loans.
 * A TreeMap keeps entities sorted by id, so listings are always in a stable order.
 */
public class InMemoryRepository<T> implements Repository<T> {

    private final Map<String, T> entities = new TreeMap<>();
    private final Function<T, String> idExtractor;

    public InMemoryRepository(Function<T, String> idExtractor) {
        this.idExtractor = idExtractor;
    }

    @Override
    public void save(T entity) {
        entities.put(idExtractor.apply(entity), entity);
    }

    @Override
    public Optional<T> findById(String id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public List<T> findAll() {
        // Return a copy so callers cannot modify the repository's content directly.
        return new ArrayList<>(entities.values());
    }
}