package com.euonia.core;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ObjectId")
class ObjectIdTest {

    @Test
    @DisplayName("Given primitive and common object values when constructing ObjectId then value is preserved")
    void givenSupportedValueTypesWhenConstructingThenValueIsPreserved() {
        ObjectId fromLong = new ObjectId(42L);
        ObjectId fromString = new ObjectId("abc");
        UUID uuid = UUID.randomUUID();
        ObjectId fromUuid = new ObjectId(uuid);
        ObjectId fromInteger = new ObjectId(Integer.valueOf(7));

        assertEquals(42L, fromLong.getValue());
        assertEquals("abc", fromString.getValue());
        assertEquals(uuid, fromUuid.getValue());
        assertEquals(7, fromInteger.getValue());
    }

    @Test
    @DisplayName("Given int literal when constructing ObjectId then long constructor is selected")
    void givenIntLiteralWhenConstructingThenLongConstructorIsSelected() {
        ObjectId objectId = new ObjectId(7);

        assertInstanceOf(Long.class, objectId.getValue());
        assertEquals(7L, objectId.getValue());
    }

    @Test
    @DisplayName("Given generated ids when calling static factory methods then generated value types are correct")
    void givenFactoriesWhenGeneratingThenReturnExpectedUnderlyingTypes() {
        ObjectId snowflake = ObjectId.snowflake();
        ObjectId guid = ObjectId.guid();
        ObjectId random = ObjectId.random();
        ObjectId ulid = ObjectId.ulid();

        assertInstanceOf(Long.class, snowflake.getValue());
        assertInstanceOf(UUID.class, guid.getValue());
        assertInstanceOf(UUID.class, random.getValue());
        assertInstanceOf(String.class, ulid.getValue());
        assertEquals(32, ulid.toString().length());
    }

    @Test
    @DisplayName("Given type token when value matches then getValue(Class) returns cast value")
    void givenMatchingTypeWhenGetValueByTypeThenReturnsCastedValue() {
        UUID uuid = UUID.randomUUID();
        ObjectId objectId = new ObjectId(uuid);

        UUID casted = objectId.getValue(UUID.class);

        assertEquals(uuid, casted);
    }

    @Test
    @DisplayName("Given type token when value does not match then getValue(Class) returns null")
    void givenMismatchedTypeWhenGetValueByTypeThenReturnsNull() {
        ObjectId objectId = new ObjectId("value");

        Integer casted = objectId.getValue(Integer.class);

        assertNull(casted);
    }

    @Test
    @DisplayName("Given identical underlying values when comparing ObjectId then equals and hashCode are consistent")
    void givenSameUnderlyingValueWhenComparingThenEqualsAndHashCodeMatch() {
        ObjectId left = new ObjectId("same");
        ObjectId right = new ObjectId("same");

        assertEquals(left, right);
        assertEquals(left.hashCode(), right.hashCode());
    }

    @Test
    @DisplayName("Given different values or types when comparing ObjectId then equals returns false")
    void givenDifferentValuesWhenComparingThenNotEqual() {
        ObjectId left = new ObjectId("left");
        ObjectId right = new ObjectId("right");
        ObjectId longNumeric = new ObjectId(7L);
        ObjectId integerNumeric = new ObjectId(Integer.valueOf(7));

        assertEquals(left, left);
        assertNotEquals(left, right);
        assertNotEquals(left, null);
        assertNotEquals(left, "left");
        assertNotEquals(longNumeric, integerNumeric);
    }

    @Test
    @DisplayName("Given supported underlying value types when converting to string then text representation is returned")
    void givenSupportedValueTypesWhenToStringThenReturnExpectedText() {
        UUID uuid = UUID.randomUUID();
        ObjectId longId = new ObjectId(123L);
        ObjectId integerId = new ObjectId(456);
        ObjectId stringId = new ObjectId("hello");
        ObjectId uuidId = new ObjectId(uuid);

        assertEquals("123", longId.toString());
        assertEquals("456", integerId.toString());
        assertEquals("hello", stringId.toString());
        assertEquals(uuid.toString(), uuidId.toString());
    }

    @Test
    @DisplayName("Given different UUID-based factories when generating then returned values are valid UUID instances")
    void givenGuidAndRandomFactoriesWhenGeneratingThenReturnUuidValues() {
        ObjectId guid = ObjectId.guid();
        ObjectId random = ObjectId.random();

        assertDoesNotThrow(() -> UUID.fromString(guid.toString()));
        assertDoesNotThrow(() -> UUID.fromString(random.toString()));
    }

    @Test
    @DisplayName("Given GUID type when generating then typed GUID strategy is supported")
    void givenGuidTypeWhenGeneratingThenTypedGuidStrategyIsSupported() {
        ObjectId guid = ObjectId.guid(GuidType.SEQUENTIAL_AS_STRING);

        assertInstanceOf(UUID.class, guid.getValue());
        assertDoesNotThrow(() -> UUID.fromString(guid.toString()));
    }
}

