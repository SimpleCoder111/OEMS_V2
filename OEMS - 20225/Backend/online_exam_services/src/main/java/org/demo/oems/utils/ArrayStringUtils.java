package org.demo.oems.utils;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for converting between String and Integer arrays/lists
 * (comma-separated format, e.g., "1,2,3,45" ↔ [1,2,3,45])
 */
@Service
public final class ArrayStringUtils {

    private ArrayStringUtils() {
    }

    /**
     * Convert comma-separated string to List<Integer>
     * Handles null/empty/blank input gracefully
     * Supports formats: "1,2,3", "1, 2, 3", "[1,2,3]", "1 2 3"
     */
    public static List<Integer> stringToIntegerList(String input) {
        if (input == null || input.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            // Clean: remove brackets, extra spaces, etc.
            String cleaned = input.trim()
                    .replace("[", "")
                    .replace("]", "")
                    .replaceAll("\\s+", "");

            if (cleaned.isEmpty()) {
                return Collections.emptyList();
            }

            return Arrays.stream(cleaned.split(","))
                    .filter(s -> !s.isBlank())
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

        } catch (NumberFormatException e) {
            // Log warning if needed
            System.err.println("Invalid integer format in string: " + input);
            return Collections.emptyList();
        }
    }

    /**
     * Convert List<Integer> to comma-separated string
     * Returns empty string if list is null or empty
     */
    public static String integerListToString(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return list.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    /**
     * Convert int[] to comma-separated string
     */
    public static String intArrayToString(int[] array) {
        if (array == null || array.length == 0) {
            return "";
        }
        return Arrays.stream(array)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));
    }

    /**
     * Convert comma-separated string to int[]
     * Returns empty array if invalid
     */
    public static int[] stringToIntArray(String input) {
        List<Integer> list = stringToIntegerList(input);
        return list.stream().mapToInt(i -> i).toArray();
    }

    public String escapeForPrompt(String input) {
        if (input == null) return "";
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }



}
