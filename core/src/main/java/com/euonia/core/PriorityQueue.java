package com.euonia.core;

import java.util.Comparator;

public class PriorityQueue<E, K extends Comparable<K>> {
    private final java.util.PriorityQueue<Pair<K, E>> queue;

    public PriorityQueue() {
        this.queue = new java.util.PriorityQueue<>(Comparator.comparing(Pair::key));
    }

    /**
     * Inserts the specified element into this priority queue with the given priority.
     *
     * @param value    the element to be added
     * @param priority the priority of the element
     */
    public void add(E value, K priority) {
        queue.add(new Pair<>(priority, value));
    }

    /**
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     *
     * @return the value of the highest priority element, or null if the queue is empty
     */
    public E poll() {
        Pair<K, E> pair = queue.poll();
        return pair != null ? pair.value() : null;
    }

    /**
     * Retrieves, but does not remove, the head of this queue. This method differs from peek only in that it throws an exception if this queue is empty.
     *
     * @return the value of the highest priority element, or null if the queue is empty
     */
    public E peek() {
        Pair<K, E> pair = queue.peek();
        return pair != null ? pair.value() : null;
    }

    public void clear() {
        queue.clear();
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
