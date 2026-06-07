package com.euonia.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ShortUniqueId")
class ShortUniqueIdTest {

    @Test
    @DisplayName("Given default instance accessor when calling twice then same instance is returned")
    void givenDefaultAccessorWhenCallingTwiceThenReturnSameInstance() {
        assertSame(ShortUniqueId.getDefault(), ShortUniqueId.getDefault());
    }

    @Test
    @DisplayName("Given numbers when encoding and decoding then original values are restored")
    void givenNumbersWhenEncodingAndDecodingThenRestoreOriginalValues() {
        ShortUniqueId hashId = new ShortUniqueId();

        String encodedInt = hashId.encode(12345);
        String encodedLongs = hashId.encode(1L, 2L, 300L);
        String encodedCollection = hashId.encode(List.of(7, 8, 9));

        assertArrayEquals(new int[]{12345}, hashId.decode(encodedInt));
        assertArrayEquals(new long[]{1L, 2L, 300L}, hashId.decodeLong(encodedLongs));
        assertArrayEquals(new int[]{7, 8, 9}, hashId.decode(encodedCollection));
    }

    @Test
    @DisplayName("Given invalid inputs when encoding longs then empty hash is returned")
    void givenInvalidLongInputsWhenEncodingThenReturnEmptyHash() {
        ShortUniqueId hashId = new ShortUniqueId();

        assertEquals("", hashId.encode((long[]) null));
        assertEquals("", hashId.encode(new long[]{}));
        assertEquals("", hashId.encode(1L, -2L));
    }

    @Test
    @DisplayName("Given blank hash when decoding then empty arrays are returned")
    void givenBlankHashWhenDecodingThenReturnEmptyArrays() {
        ShortUniqueId hashId = new ShortUniqueId();

        assertArrayEquals(new int[0], hashId.decode(" "));
        assertArrayEquals(new long[0], hashId.decodeLong(" "));
    }

    @Test
    @DisplayName("Given hexadecimal string when encoding and decoding then original uppercase hex is restored")
    void givenHexStringWhenEncodingAndDecodingThenRestoreUppercaseHex() {
        ShortUniqueId hashId = new ShortUniqueId();
        String hex = "deadbeefcafebabe";

        String encoded = hashId.encodeHex(hex);
        String decoded = hashId.decodeHex(encoded);

        assertFalse(encoded.isBlank());
        assertEquals(hex.toUpperCase(), decoded);
    }

    @Test
    @DisplayName("Given invalid hex when encoding hex then empty hash is returned")
    void givenInvalidHexWhenEncodingHexThenReturnEmptyHash() {
        ShortUniqueId hashId = new ShortUniqueId();

        assertEquals("", hashId.encodeHex(null));
        assertEquals("", hashId.encodeHex(""));
        assertEquals("", hashId.encodeHex("xyz-123"));
    }

    @Test
    @DisplayName("Given min hash length when encoding then resulting hash respects minimum size")
    void givenMinHashLengthWhenEncodingThenHashRespectsMinimumSize() {
        ShortUniqueId hashId = new ShortUniqueId("salt", 16, "abcdefghijklmnopqrstuvwxyzABCDEFG", "cfhistuCFHISTU");

        String encoded = hashId.encode(1);

        assertTrue(encoded.length() >= 16);
    }

    @Test
    @DisplayName("Given invalid constructor arguments when creating then illegal argument is thrown")
    void givenInvalidConstructorArgumentsWhenCreatingThenThrowIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new ShortUniqueId(null, 0, "abcdefghijklmnopqrstuvwxyz", "cfhistu"));
        assertThrows(IllegalArgumentException.class, () -> new ShortUniqueId("", -1, "abcdefghijklmnopqrstuvwxyz", "cfhistu"));
        assertThrows(IllegalArgumentException.class, () -> new ShortUniqueId("", 0, "", "cfhistu"));
        assertThrows(IllegalArgumentException.class, () -> new ShortUniqueId("", 0, "abc", "cfhistu"));
        assertThrows(IllegalArgumentException.class, () -> new ShortUniqueId("", 0, "abcdefghijklmnopqrstuvwxyz", ""));
    }
}

