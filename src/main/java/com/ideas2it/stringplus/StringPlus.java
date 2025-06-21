package com.ideas2it.stringplus;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        final StringBuilder result = new StringBuilder(value.length());
        final StringBuilder currentWord = new StringBuilder();

        for (char c : value.toCharArray()) {
            if (Character.isWhitespace(c)) {
                if (currentWord.length() > 0) {
                    currentWord.setCharAt(0, Character.toTitleCase(currentWord.charAt(0)));
                    result.append(currentWord);
                    currentWord.setLength(0);
                }
                result.append(c); 
            } else {
                currentWord.append(Character.toLowerCase(c));
            }
        }

        if (currentWord.length() > 0) {
            currentWord.setCharAt(0, Character.toTitleCase(currentWord.charAt(0)));
            result.append(currentWord);
        }

        return new StringPlus(result.toString());
    }

    /**
     * Trims a custom set of characters from the beginning and end of the string.
     *
     * @param chars The characters to trim.
     * @return A new, trimmed StringPlus instance.
     */
    public StringPlus trimChars(char... chars) {
        if (value == null || chars.length == 0) return this;
        
        java.util.Set<Character> trimSet = new java.util.HashSet<>();
        for (char c : chars) {
            trimSet.add(c);
        }

        int start = 0;
        while (start < value.length() && trimSet.contains(value.charAt(start))) {
            start++;
        }

        int end = value.length();
        while (end > start && trimSet.contains(value.charAt(end - 1))) {
            end--;
        }

        return new StringPlus(value.substring(start, end));
    }

    /**
     * Converts the string into a URL-friendly "slug".
     * Replaces accented characters, converts to lower case, and replaces non-alphanumeric
     * character sequences with a hyphen.
     *
     * @return A new, slugified StringPlus instance.
     */
    public StringPlus slugify() {
        if (value == null || value.trim().isEmpty()) {
            return new StringPlus("");
        }

        String text = this.toLowerCase().removeAccents().toString();
        StringBuilder slug = new StringBuilder();
        boolean lastCharWasHyphen = false;

        for (char c : text.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                slug.append(c);
                lastCharWasHyphen = false;
            } else if (!lastCharWasHyphen && slug.length() > 0) {
                slug.append('-');
                lastCharWasHyphen = true;
            }
        }

        int start = 0;
        int end = slug.length();

        while (start < end && slug.charAt(start) == '-') start++;
        while (end > start && slug.charAt(end - 1) == '-') end--;

        return new StringPlus(slug.substring(start, end));
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
                .filter(s -> !s.isEmpty()) // filter out empty parts
                .map(StringPlus::new)
                .toList(); // returns unmodifiable list
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
        
        int atIndex = value.indexOf('@');
        if (atIndex <= 1) { // No '@' found, or username is only one character
            return this;
        }

        char[] masked = value.toCharArray();
        for (int i = 1; i < atIndex; i++) {
            masked[i] = '*';
        }
        return new StringPlus(new String(masked));
    }

    /**
     * Masks a phone number, showing only the last 4 digits.
     *
     * @return A new, masked StringPlus instance.
     */
    public StringPlus maskPhone() {
        if (value == null) return this;
        
        char[] chars = value.toCharArray();
        List<Integer> digitIndices = new ArrayList<>();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isDigit(chars[i])) {
                digitIndices.add(i);
            }
        }

        if (digitIndices.size() > 4) {
            for (int i = 0; i < digitIndices.size() - 4; i++) {
                chars[digitIndices.get(i)] = '*';
            }
        }
        
        return new StringPlus(new String(chars));
    }

    /**
     * Counts the number of words in the string.
     *
     * @return The word count.
     */
    public int wordCount() {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        int count = 0;
        boolean inWord = false;
        for (int i = 0; i < value.length(); i++) {
            if (Character.isWhitespace(value.charAt(i))) {
                inWord = false;
            } else {
                if (!inWord) {
                    count++;
                }
                inWord = true;
            }
        }
        return count;
    }

    /**
     * Splits the string into a list of tokens (words).
     *
     * @return A list of tokens.
     */
    public List<String> tokenize() {
        if (value == null || value.trim().isEmpty()) return Collections.emptyList();
        
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        
        for (char c : value.trim().toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                currentToken.append(c);
            } else {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
            }
        }
        
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }
        
        return tokens;
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
        
        if (obj instanceof StringPlus stringPlus) {
            if (value == null) {
                return stringPlus.value == null;
            }
            return value.equals(stringPlus.value);
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