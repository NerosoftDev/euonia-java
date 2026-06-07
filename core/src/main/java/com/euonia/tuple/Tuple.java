package com.euonia.tuple;

import java.io.Serializable;
import java.util.*;

/**
 * Represents an ordered collection of values, where each value can be of any type. A tuple is immutable and can contain duplicate values.
 * It provides methods to access the values, check for their presence, and perform common tuple operations such as equality checks and conversions to lists and arrays.
 * The Tuple interface extends Iterable<Object> to allow for easy iteration over the values in the tuple, and it also implements Serializable to enable serialization of tuple instances.
 * The Tuple interface is designed to be flexible and can be implemented by various classes that represent tuples of different sizes and types, such as Pair, Triplet, Quadruple, etc.
 * It provides a common contract for working with tuples, allowing for consistent handling of tuples across different implementations and use cases.
 */
@SuppressWarnings("unused")
public interface Tuple extends Iterable<Object>, Serializable {
    /**
     * Returns the number of values in the tuple.
     *
     * @return the number of values in the tuple
     */
    int size();

    /**
     * Returns the value at the specified index in the tuple.
     *
     * @param index the index of the value to return
     * @return the value at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    default Object value(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
        }
        return values().get(index);
    }

    /**
     * Returns a list of all values in the tuple.
     *
     * @return a list of all values in the tuple
     */
    List<Object> values();

    /**
     * Checks if the tuple contains the specified value.
     *
     * @param value the value to check for
     * @return true if the tuple contains the value, false otherwise
     */
    default boolean contains(final Object value) {
        return values().stream().anyMatch(v -> Objects.equals(v, value));
    }

    /**
     * Checks if the tuple contains all of the specified values.
     *
     * @param values the values to check for
     * @return true if the tuple contains all of the values, false otherwise
     */
    default boolean containsAll(final Collection<?> values) {
        for (final Object value : values) {
            if (!contains(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the tuple contains all of the values in the specified tuple.
     *
     * @param tuple the tuple whose values to check for
     * @return true if the tuple contains all of the values, false otherwise
     */
    default boolean containsAll(final Tuple tuple) {
        return containsAll(tuple.values());
    }

    /**
     * Checks if the tuple contains all of the specified values.
     *
     * @param values the values to check for
     * @return true if the tuple contains all of the values, false otherwise
     */
    default boolean containsAll(final Object... values) {
        for (final Object value : values) {
            if (!contains(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the tuple contains any of the specified values.
     *
     * @param values the values to check for
     * @return true if the tuple contains any of the values, false otherwise
     */
    default boolean containsAny(final Collection<?> values) {
        for (final Object value : values) {
            if (contains(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the tuple contains any of the values in the specified tuple.
     *
     * @param tuple the tuple whose values to check for
     * @return true if the tuple contains any of the values, false otherwise
     */
    default boolean containsAny(final Tuple tuple) {
        return containsAny(tuple.values());
    }

    /**
     * Checks if the tuple contains any of the specified values.
     *
     * @param values the values to check for
     * @return true if the tuple contains any of the values, false otherwise
     */
    default boolean containsAny(final Object... values) {
        for (final Object value : values) {
            if (contains(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the index of the first occurrence of the specified value in the tuple, or -1 if the value is not found.
     *
     * @param value the value to search for
     * @return the index of the first occurrence of the specified value, or -1 if not found
     */
    default int indexOf(final Object value) {
        final List<?> values = values();
        final int size = this.size();
        for (int i = 0; i < size; i++) {
            if (Objects.equals(values.get(i), value)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified value in the tuple, or -1 if the value is not found.
     *
     * @param value the value to search for
     * @return the index of the last occurrence of the specified value, or -1 if not found
     */
    default int lastIndexOf(final Object value) {
        final List<?> values = values();
        for (int i = this.size() - 1; i >= 0; i--) {
            if (Objects.equals(values.get(i), value)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns a list containing all values in the tuple. The returned list is immutable and reflects the order of values in the tuple.
     *
     * @return a list containing all values in the tuple
     */
    default List<Object> toList() {
        return this.values(); // already returns an immutable list
    }

    /**
     * Returns an array containing all values in the tuple. The returned array is a new array and reflects the order of values in the tuple.
     *
     * @return an array containing all values in the tuple
     */
    default Object[] toArray() {
        return this.values().toArray();
    }

    /**
     * Returns an array containing all values in the tuple. The returned array is array new array and reflects the order of values in the tuple. If the provided array is large enough to hold all values, it will be used; otherwise, array new array of the same runtime type will be allocated.
     *
     * @param array the array into which the elements of the tuple are to be stored, if it is big enough; otherwise, array new array of the same runtime type is allocated for this purpose
     * @param <X>   the component type of the array to contain the tuple values
     * @return an array containing all values in the tuple
     */
    default <X> X[] toArray(final X[] array) {
        return this.values().toArray(array);
    }

    /**
     * Returns an iterator over the values in the tuple. The returned iterator is immutable and reflects the order of values in the tuple.
     *
     * @return an iterator over the values in the tuple
     */
    @SuppressWarnings("NullableProblems")
    @Override
    default Iterator<Object> iterator() {
        return values().iterator();
    }

    /**
     * Checks if this tuple is equal to another tuple, ignoring the order of values. Two tuples are considered equal if they contain the same values with the same number of occurrences, regardless of their order.
     *
     * @param other the other tuple to compare with
     * @return true if the tuples are equal ignoring the order of values, false otherwise
     */
    default boolean equalsIgnoreOrder(final Tuple other) {

        if (this == other) {
            return true;
        }
        final int size = this.size();
        if (other == null || size != other.size()) {
            return false;
        }

        // Store every element along with the number of times it occurs, then use the other map to decrease
        final Map<Object, Integer> valueOccurrences = new HashMap<>(size + 1, 1.0f);

        for (int i = 0; i < size; i++) {
            final Object thisValue = this.value(i);
            valueOccurrences.merge(thisValue, 1, Integer::sum);
        }

        for (int i = 0; i < size; i++) {
            final Object otherValue = other.value(i);
            int occ = valueOccurrences.merge(otherValue, -1, Integer::sum);
            if (occ < 0) {
                return false; // element is not present: tuples are different
            } else if (occ == 0) {
                valueOccurrences.remove(otherValue);
            }
        }

        return valueOccurrences.isEmpty();
    }
}
