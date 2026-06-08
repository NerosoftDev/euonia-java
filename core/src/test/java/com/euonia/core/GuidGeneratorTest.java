package com.euonia.core;

import java.nio.ByteBuffer;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GuidGenerator")
class GuidGeneratorTest {

    private static final long DOTNET_EPOCH_OFFSET_MILLIS = 62_135_596_800_000L;

    @Test
    @DisplayName("Given empty type when generating then all-zero UUID is returned")
    void givenEmptyTypeWhenGeneratingThenReturnEmptyUuid() {
        UUID guid = GuidGenerator.generate(GuidType.EMPTY);

        assertEquals(new UUID(0L, 0L), guid);
    }

    @Test
    @DisplayName("Given simple type when generating then non-empty UUID is returned")
    void givenSimpleTypeWhenGeneratingThenReturnNonEmptyUuid() {
        UUID guid = GuidGenerator.generate(GuidType.SIMPLE);

        assertNotEquals(new UUID(0L, 0L), guid);
    }

    @Test
    @DisplayName("Given sequential-as-string type when generating then timestamp is encoded at canonical start")
    void givenSequentialAsStringTypeWhenGeneratingThenTimestampIsAtCanonicalStart() {
        long before = dotNetNowMillis();
        UUID guid = GuidGenerator.generate(GuidType.SEQUENTIAL_AS_STRING);
        long after = dotNetNowMillis();

        long embeddedTimestamp = decode48BitTimestamp(uuidBytes(guid), 0);

        assertTrue(embeddedTimestamp >= before);
        assertTrue(embeddedTimestamp <= after);
    }

    @Test
    @DisplayName("Given sequential-as-binary type when generating then timestamp is encoded at dotnet byte-array start")
    void givenSequentialAsBinaryTypeWhenGeneratingThenTimestampIsAtDotNetByteArrayStart() {
        long before = dotNetNowMillis();
        UUID guid = GuidGenerator.generate(GuidType.SEQUENTIAL_AS_BINARY);
        long after = dotNetNowMillis();

        long embeddedTimestamp = decode48BitTimestamp(toDotNetBytes(guid), 0);

        assertTrue(embeddedTimestamp >= before);
        assertTrue(embeddedTimestamp <= after);
    }

    @Test
    @DisplayName("Given sequential-at-end type when generating then timestamp is encoded at dotnet byte-array end")
    void givenSequentialAtEndTypeWhenGeneratingThenTimestampIsAtDotNetByteArrayEnd() {
        long before = dotNetNowMillis();
        UUID guid = GuidGenerator.generate(GuidType.SEQUENTIAL_AT_END);
        long after = dotNetNowMillis();

        long embeddedTimestamp = decode48BitTimestamp(toDotNetBytes(guid), 10);

        assertTrue(embeddedTimestamp >= before);
        assertTrue(embeddedTimestamp <= after);
    }

    private static long dotNetNowMillis() {
        return System.currentTimeMillis() + DOTNET_EPOCH_OFFSET_MILLIS;
    }

    private static byte[] uuidBytes(UUID value) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(value.getMostSignificantBits());
        buffer.putLong(value.getLeastSignificantBits());
        return buffer.array();
    }

    private static byte[] toDotNetBytes(UUID value) {
        byte[] bytes = uuidBytes(value);
        reverse(bytes, 0, 4);
        reverse(bytes, 4, 2);
        reverse(bytes, 6, 2);
        return bytes;
    }

    private static long decode48BitTimestamp(byte[] bytes, int offset) {
        long value = 0L;
        for (int index = offset; index < offset + 6; index++) {
            value = (value << 8) | (bytes[index] & 0xFFL);
        }
        return value;
    }

    private static void reverse(byte[] bytes, int offset, int length) {
        for (int left = offset, right = offset + length - 1; left < right; left++, right--) {
            byte current = bytes[left];
            bytes[left] = bytes[right];
            bytes[right] = current;
        }
    }
}
