package com.euonia.reflection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TypeHelper")
class TypeHelperTest {

    private enum SampleEnum {
        FIRST,
        SECOND
    }

    @Test
    @DisplayName("Given null desired type when coercing then illegal argument is thrown")
    void givenNullDesiredTypeWhenCoercingThenThrowIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> TypeHelper.coerceValue(null, String.class, "x"));
    }

    @Test
    @DisplayName("Given null value and primitive target when coercing then primitive default is returned")
    void givenNullValueAndPrimitiveTargetWhenCoercingThenReturnPrimitiveDefault() {
        assertEquals(0, TypeHelper.coerceValue(int.class, null));
        assertEquals(false, TypeHelper.coerceValue(boolean.class, null));
        assertEquals('\0', TypeHelper.coerceValue(char.class, null));
    }

    @Test
    @DisplayName("Given enum target when coercing from name and ordinal then enum values are returned")
    void givenEnumTargetWhenCoercingThenReturnEnumValues() {
        assertEquals(SampleEnum.SECOND, TypeHelper.coerceValue(SampleEnum.class, "SECOND"));
        assertEquals(SampleEnum.FIRST, TypeHelper.coerceValue(SampleEnum.class, 0));
        assertThrows(IllegalArgumentException.class, () -> TypeHelper.coerceValue(SampleEnum.class, 5));
    }

    @Test
    @DisplayName("Given boolean target when coercing from number and text then boolean values are returned")
    void givenBooleanTargetWhenCoercingThenReturnBooleanValues() {
        assertEquals(true, TypeHelper.coerceValue(Boolean.class, 1));
        assertEquals(false, TypeHelper.coerceValue(Boolean.class, 0));
        assertEquals(true, TypeHelper.coerceValue(Boolean.class, "yes"));
        assertEquals(false, TypeHelper.coerceValue(Boolean.class, "no"));
        assertThrows(IllegalArgumentException.class, () -> TypeHelper.coerceValue(Boolean.class, "maybe"));
    }

    @Test
    @DisplayName("Given numeric target when coercing from text then numeric value is returned")
    void givenNumericTargetWhenCoercingFromTextThenReturnNumericValue() {
        assertEquals(42, TypeHelper.coerceValue(Integer.class, "42"));
        assertEquals(0L, TypeHelper.coerceValue(Long.class, ""));
        assertEquals(3.5d, TypeHelper.coerceValue(Double.class, "3.5"));
    }

    @Test
    @DisplayName("Given UUID and Character targets when coercing then values are converted")
    void givenUuidAndCharacterTargetsWhenCoercingThenValuesAreConverted() {
        UUID uuid = UUID.randomUUID();

        assertEquals(uuid, TypeHelper.coerceValue(UUID.class, uuid.toString()));
        assertNull(TypeHelper.coerceValue(UUID.class, ""));
        assertEquals('A', TypeHelper.coerceValue(Character.class, "ABC"));
        assertEquals('\0', TypeHelper.coerceValue(Character.class, ""));
    }

    @Test
    @DisplayName("Given date time targets when coercing then temporal values are converted")
    void givenDateTimeTargetsWhenCoercingThenTemporalValuesAreConverted() {
        Instant now = Instant.now();

        Date date = TypeHelper.coerceValue(Date.class, now.toEpochMilli());
        Instant parsedInstant = TypeHelper.coerceValue(Instant.class, now.toString());
        LocalDate parsedDate = TypeHelper.coerceValue(LocalDate.class, "2026-06-07");

        assertEquals(now.toEpochMilli(), date.getTime());
        assertEquals(now, parsedInstant);
        assertEquals(LocalDate.of(2026, 6, 7), parsedDate);
    }

    @Test
    @DisplayName("Given array and collection targets when coercing iterable then converted collections are returned")
    void givenArrayAndCollectionTargetsWhenCoercingIterableThenReturnConvertedCollections() {
        List<String> values = Arrays.asList("1", "2", "3");

        int[] ints = TypeHelper.coerceValue(int[].class, values);
        Object list = TypeHelper.coerceValue(List.class, new String[]{"a", "b"});
        Object concreteList = TypeHelper.coerceValue(ArrayList.class, new String[]{"x", "y"});

        assertArrayEquals(new int[]{1, 2, 3}, ints);
        assertInstanceOf(List.class, list);
        assertEquals(List.of("a", "b"), list);
        assertInstanceOf(ArrayList.class, concreteList);
        assertEquals(List.of("x", "y"), concreteList);
    }

    @Test
    @DisplayName("Given incompatible conversion when coercing then illegal argument is thrown")
    void givenIncompatibleConversionWhenCoercingThenThrowIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> TypeHelper.coerceValue(Integer.class, new Object()));
    }
}

