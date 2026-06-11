package com.euonia.core;

import java.util.Random;

/**
 * Tool to generate random IDs based on a seed value.
 */
final class RandomId {

    private static final String[] CHARS = {
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
        "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };

    private RandomId() {
        // utility class
    }

    private static String[] copyChars() {
        return CHARS.clone();
    }

    private static String generateKey() {
        int seek = (int) System.currentTimeMillis();

        var random = new Random(seek);
        var chars = copyChars();

        for (int i = 0; i < 100000; i++) {
            int number = random.nextInt(1, chars.length);
            var temp = chars[0];
            chars[0] = chars[number - 1];
            chars[number - 1] = temp;
        }

        return String.join("", chars);
    }

    /**
     * Generates a random ID based on the provided seed.
     *
     * @param seed the seed value
     * @return a random ID string
     */
    public static String generate(long seed) {
        var key = generateKey();
        return mixup(key, seed);
    }

    private static String convert(String key, long value) {
        if (value < 62) {
            return key.substring((int) value, (int) value + 1);
        }

        int y = (int) (value % 62);
        long x = value / 62;
        return convert(key, x) + key.charAt(y);
    }

    private static String mixup(String key, long value) {
        var sequence = convert(key, value);
        int salt = 0;
        for (int i = 0; i < sequence.length(); i++) {
            salt += sequence.charAt(i);
        }

        int x = salt % sequence.length();

        return sequence.substring(x) + sequence.substring(0, x);
    }
}
