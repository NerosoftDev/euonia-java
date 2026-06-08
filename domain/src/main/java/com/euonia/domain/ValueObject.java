package com.euonia.domain;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * A base class for value objects that provides implementations for equals, hashCode, and compareTo methods.
 *
 * @param <T> the type of the value object
 */
public class ValueObject<T extends ValueObject<T>> implements Comparable<T> {
    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(T other) {
        if (other == null) {
            return 1;
        }

        var fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                var left = field.get(this);
                var right = field.get(other);

                if (left == right) {
                    continue;
                }

                if (left == null) {
                    return -1;
                }

                if (right == null) {
                    return 1;
                }

                if (left instanceof Comparable && right instanceof Comparable) {
                    @SuppressWarnings("unchecked")
                    int result = ((Comparable<Object>) left).compareTo(right);
                    if (result != 0) {
                        return result;
                    }
                } else {
                    int result = left.toString().compareTo(right.toString());
                    if (result != 0) {
                        return result;
                    }
                }

            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    public boolean equals(T obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        var fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                var left = field.get(this);
                var right = field.get(obj);

                if (left == right) {
                    continue;
                }

                if (left == null || right == null) {
                    return false;
                }

                if (!Objects.equals(left, right)) {
                    return false;
                }

            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        return equals((T) obj);
    }

    @Override
    public int hashCode() {
        var hashCode = 31;
        var changeMultiplier = false;
        var fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                var value = field.get(this);
                if (value != null) {
                    hashCode = hashCode * (changeMultiplier ? 59 : 114) + value.hashCode();
                    changeMultiplier = !changeMultiplier;
                } else {
                    hashCode ^= 13;
                }
            } catch (Exception e) {
                continue;
            }
        }
        return hashCode;
    }
}
