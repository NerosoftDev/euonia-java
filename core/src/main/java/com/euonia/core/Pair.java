package com.euonia.core;

/**
 * A generic class that represents a pair of key and value. The key must be comparable to ensure that pairs can be sorted based on their keys.
 *
 * @param key   the key of the pair
 * @param value the value associated with the key
 * @param <K>   the type of the key
 * @param <V>   the type of the value
 */
public record Pair<K extends Comparable<K>, V>(K key, V value) {
    public static <K extends Comparable<K>, V> Pair<K, V> of(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        return new Pair<>(key, value);
    }
}
