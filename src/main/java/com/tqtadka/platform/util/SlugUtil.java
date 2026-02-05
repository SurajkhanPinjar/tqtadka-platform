package com.tqtadka.platform.util;

import java.text.Normalizer;
import java.util.Locale;

public class SlugUtil {

    public static String toSlug(String input) {
        return input.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    public static String slugify(String input) {

        if (input == null) return null;

        String slug = input
                .trim()
                .toLowerCase(Locale.ENGLISH);

        // Normalize accented characters (é → e, ñ → n)
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Replace non-alphanumeric with hyphen
        slug = slug.replaceAll("[^a-z0-9]+", "-");

        // Remove leading/trailing hyphens
        slug = slug.replaceAll("^-+|-+$", "");

        // Safety fallback
        return slug.isBlank() ? "post" : slug;
    }
}