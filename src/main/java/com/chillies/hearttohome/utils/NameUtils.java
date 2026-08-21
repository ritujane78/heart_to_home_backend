package com.chillies.hearttohome.utils;

import java.util.Set;

public final class NameUtils {

    private static final Set<String> TITLES = Set.of(
            "mr", "mrs", "ms", "miss", "dr", "prof"
    );

    private NameUtils() {
    }

    public static String formatFirstName(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }

        String[] parts = name.trim().split("\\s+");

        if (parts.length == 0) {
            return "";
        }

        String first = clean(parts[0]);

        // First word is a title (Dr., Mr., etc.)
        if (TITLES.contains(first.toLowerCase()) && parts.length > 1) {
            return capitalize(first) + ". " + capitalize(clean(parts[1]));
        }

        return capitalize(first);
    }

    private static String clean(String word) {
        return word.replaceAll("^[^\\p{L}]+|[^\\p{L}]+$", "");
    }

    private static String capitalize(String word) {
        if (word == null || word.isBlank()) {
            return "";
        }

        return Character.toUpperCase(word.charAt(0))
                + word.substring(1).toLowerCase();
    }
}