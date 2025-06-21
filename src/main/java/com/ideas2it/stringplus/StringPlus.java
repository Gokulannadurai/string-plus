package com.ideas2it.stringplus;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A powerful, immutable, and feature-rich string manipulation utility class.
 * StringPlus encapsulates a java.lang.String, extends it with a fluent API,
 * and adds utilities for modern backend development. It is designed to be
 * secure, reliable, and efficient.
 *
 * This class implements CharSequence, allowing it to be used with many
 * Java APIs that operate on character sequences.
 *
 * @author Gokul Annadurai
 * @version 1.0
 * @since 2024-07-25
 */
public final class StringPlus implements CharSequence, Serializable, Comparable<StringPlus> {

    private static final long serialVersionUID = 1L;
    private final String value;

    /**
     * Constructs a new StringPlus.
     *
     * @param value The string value to encapsulate. Can be null.
     */
    public StringPlus(String value) {
        this.value = value;
    }

    /**
     * Static factory method to create a StringPlus instance.
     *
     * @param value The string value.
     * @return a new StringPlus instance.
     */
    public static StringPlus of(String value) {
        return new StringPlus(value);
    }

    // --- CharSequence Methods ---

    @Override
    public int length() {
        return value == null ? 0 : value.length();
    }

    @Override
    public char charAt(int index) {
        if (value == null) {
            throw new IndexOutOfBoundsException("String is null");
        }
        return value.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        if (value == null) {
            throw new IndexOutOfBoundsException("String is null");
        }
        return new StringPlus(value.subSequence(start, end).toString());
    }

    // --- Standard String-like Methods (Fluent) ---

    /**
     * See {@link String#trim()}.
     * @return a new StringPlus with leading/trailing whitespace removed.
     */
    public StringPlus trim() {
        if (value == null) return this;
        return new StringPlus(value.trim());
    }

    /**
     * See {@link String#toLowerCase()}.
     * @return a new StringPlus converted to lower case.
     */
    public StringPlus toLowerCase() {
        if (value == null) return this;
        return new StringPlus(value.toLowerCase());
    }

    /**
     * See {@link String#toUpperCase()}.
     * @return a new StringPlus converted to upper case.
     */
    public StringPlus toUpperCase() {
        if (value == null) return this;
        return new StringPlus(value.toUpperCase());
    }
    
    /**
     * See {@link String#replace(CharSequence, CharSequence)}.
     * @return a new StringPlus with replacements made.
     */
    public StringPlus replace(CharSequence target, CharSequence replacement) {
        if (value == null) return this;
        return new StringPlus(value.replace(target, replacement));
    }

    // --- Custom Features from Requirements ---

    /**
     * Converts the first character to upper case and the rest to lower case.
     *
     * @return A new, capitalized StringPlus instance.
     */
    public StringPlus capitalize() {
        if (value == null || value.isEmpty()) return this;
        return new StringPlus(Character.toUpperCase(value.charAt(0)) + value.substring(1).toLowerCase());
    }

    /**
     * Converts the string to title case, capitalizing the first letter of each word.
     *
     * @return A new, title-cased StringPlus instance.
     */
    public StringPlus titleCase() {
        if (value == null || value.isEmpty()) return this;
        String[] words = value.toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1))
                  .append(" ");
            }
        }
        return new StringPlus(sb.toString().trim());
    }

    /**
     * Trims a custom set of characters from the beginning and end of the string.
     *
     * @param chars The characters to trim.
     * @return A new, trimmed StringPlus instance.
     */
    public StringPlus trimChars(char... chars) {
        if (value == null || chars.length == 0) return this;
        
        StringBuilder patternBuilder = new StringBuilder("[");
        for (char c : chars) {
            patternBuilder.append(Pattern.quote(String.valueOf(c)));
        }
        patternBuilder.append("]");
        
        String pattern = "^" + patternBuilder.toString() + "+|" + patternBuilder.toString() + "+$";
        return new StringPlus(value.replaceAll(pattern, ""));
    }

    /**
     * Converts the string into a URL-friendly "slug".
     * Replaces accented characters, converts to lower case, and replaces non-alphanumeric
     * character sequences with a hyphen.
     *
     * @return A new, slugified StringPlus instance.
     */
    public StringPlus slugify() {
        if (value == null || value.trim().isEmpty()) return new StringPlus("");
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        return new StringPlus(normalized.toLowerCase()
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-|-$", ""));
    }

    /**
     * Splits the string by a delimiter and trims each resulting element.
     *
     * @param delimiter The delimiter to split by.
     * @return A list of new StringPlus instances.
     */
    public List<StringPlus> splitAndTrim(String delimiter) {
        if (value == null || value.isEmpty()) return Collections.emptyList();
        return Arrays.stream(value.split(Pattern.quote(delimiter)))
                     .map(String::trim)
                     .map(StringPlus::new)
                     .collect(Collectors.toList());
    }

    /**
     * Calculates the Levenshtein distance to another string.
     * This distance is the number of edits (insertions, deletions, or substitutions)
     * needed to change one word into the other.
     *
     * @param other The other string to compare against.
     * @return The Levenshtein distance.
     */
    public int levenshteinDistance(String other) {
        if (value == null || other == null) return -1;
        int[][] dp = new int[value.length() + 1][other.length() + 1];
        for (int i = 0; i <= value.length(); i++) {
            for (int j = 0; j <= other.length(); j++) {
                if (i == 0) dp[i][j] = j;
                else if (j == 0) dp[i][j] = i;
                else if (value.charAt(i - 1) == other.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[value.length()][other.length()];
    }

    /**
     * Checks if the string matches a given regular expression.
     *
     * @param regex The regular expression.
     * @return true if it matches, false otherwise.
     */
    public boolean matchesRegex(String regex) {
        if (value == null) return false;
        return Pattern.matches(regex, value);
    }

    /**
     * Extracts all substrings that match a given regular expression.
     *
     * @param regex The regular expression.
     * @return A list of matching strings.
     */
    public List<String> extractMatches(String regex) {
        if (value == null) return Collections.emptyList();
        List<String> matches = new ArrayList<>();
        Matcher matcher = Pattern.compile(regex).matcher(value);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }

    /**
     * Removes diacritical marks (accents) from the string.
     *
     * @return A new StringPlus instance with accents removed.
     */
    public StringPlus removeAccents() {
        if (value == null) return this;
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        return new StringPlus(normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", ""));
    }

    /**
     * Masks an email address, showing only the first character of the username.
     * Example: "test.email@example.com" -> "t********@example.com"
     *
     * @return A new, masked StringPlus instance.
     */
    public StringPlus maskEmail() {
        if (value == null) return this;
        return new StringPlus(value.replaceAll("(?<=.).(?=[^@]*?@)", "*"));
    }

    /**
     * Masks a phone number, showing only the last 4 digits.
     *
     * @return A new, masked StringPlus instance.
     */
    public StringPlus maskPhone() {
        if (value == null) return this;
        return new StringPlus(value.replaceAll("\\d(?=\\d{4})", "*"));
    }

    /**
     * Counts the number of words in the string.
     *
     * @return The word count.
     */
    public int wordCount() {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        return value.trim().split("\\s+").length;
    }

    /**
     * Splits the string into a list of tokens (words).
     *
     * @return A list of tokens.
     */
    public List<String> tokenize() {
        if (value == null || value.trim().isEmpty()) return Collections.emptyList();
        return Arrays.asList(value.trim().split("\\W+"));
    }
    
    // --- Object Methods ---
    
    @Override
    public String toString() {
        return value;
    }
    
    @Override
    public int hashCode() {
        return value == null ? 0 : value.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        
        if (obj instanceof StringPlus) {
            StringPlus other = (StringPlus) obj;
            if (value == null) {
                return other.value == null;
            }
            return value.equals(other.value);
        }
        
        if (obj instanceof String) {
            if (value == null) return false;
            return value.equals(obj);
        }
        
        return false;
    }

    @Override
    public int compareTo(StringPlus other) {
        if (this.value == null && other.value == null) {
            return 0;
        }
        if (this.value == null) {
            return -1; // nulls first
        }
        if (other.value == null) {
            return 1;
        }
        return this.value.compareTo(other.value);
    }
}