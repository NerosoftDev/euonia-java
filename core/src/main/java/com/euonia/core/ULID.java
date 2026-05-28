package com.euonia.core;

import java.security.SecureRandom;
import java.time.Instant;

/**
 * ULID (Universally Unique Lexicographically Sortable Identifier) generator.
 * <p>
 * ULID is a 128-bit universally unique identifier that is lexicographically
 * sortable and URL-safe.
 * </p>
 */
@SuppressWarnings("unused")
public final class ULID {

    /**
     * ULID uses a specific 32-character encoding known as Crockford's Base32,
     * which includes digits and upper-case letters but excludes letters like "I",
     * "L", "O"
     * to avoid confusion with digits.
     */
    private static final String CROCKFORD_BASE32 = "0123456789ABCDEFGHJKMNPQRSTVWXYZ";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private ULID() {
        // utility class, prevent instantiation
    }

    /**
     * Generates a new ULID string.
     *
     * @return a 32-character ULID string
     */
    public static String generate() {
        byte[] timestamp = getTimestamp();
        byte[] randomBytes = getRandomBytes();
        return encode(timestamp, randomBytes);
    }

    /**
     * Gets the current UTC timestamp as a 48-bit (6 bytes) big-endian value.
     *
     * @return 6 bytes representing the timestamp in milliseconds since Unix epoch
     */
    private static byte[] getTimestamp() {
        long timestamp = Instant.now().toEpochMilli();
        // Convert to 8 bytes in big-endian order
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (timestamp >> 56);
        bytes[1] = (byte) (timestamp >> 48);
        bytes[2] = (byte) (timestamp >> 40);
        bytes[3] = (byte) (timestamp >> 32);
        bytes[4] = (byte) (timestamp >> 24);
        bytes[5] = (byte) (timestamp >> 16);
        bytes[6] = (byte) (timestamp >> 8);
        bytes[7] = (byte) timestamp;
        // Extract the last 6 bytes (lower 48 bits)
        byte[] result = new byte[6];
        System.arraycopy(bytes, 2, result, 0, 6);
        return result;
    }

    /**
     * Generates 10 cryptographically secure random bytes (80 bits).
     *
     * @return 10 random bytes
     */
    private static byte[] getRandomBytes() {
        byte[] randomBytes = new byte[10];
        SECURE_RANDOM.nextBytes(randomBytes);
        return randomBytes;
    }

    /**
     * Encodes the timestamp and random bytes into a Crockford's Base32 encoded ULID
     * string.
     *
     * @param timestamp   6 bytes of timestamp data
     * @param randomBytes 10 bytes of random data
     * @return a Base32-encoded ULID string
     */
    private static String encode(byte[] timestamp, byte[] randomBytes) {
        byte[] ulidBytes = new byte[16];
        System.arraycopy(timestamp, 0, ulidBytes, 0, 6);
        System.arraycopy(randomBytes, 0, ulidBytes, 6, 10);

        StringBuilder ulid = new StringBuilder(32);
        for (byte b : ulidBytes) {
            int value = b & 0xFF;
            ulid.append(CROCKFORD_BASE32.charAt((value >> 3) & 0x1F));
            ulid.append(CROCKFORD_BASE32.charAt(value & 0x1F));
        }
        return ulid.toString();
    }
}
