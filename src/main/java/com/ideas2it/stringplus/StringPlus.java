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

    /**
     * Efficiently joins a list of String instances with a delimiter.
     * Null values are treated as empty strings. Returns an empty StringPlus if the list is null or empty.
     *
     * @param parts The list of String instances to join.
     * @param delimiter The delimiter to use between parts.
     * @return A new StringPlus containing the joined string.
     */
    public static StringPlus join(List<StringPlus> parts, String delimiter) {
        if (parts == null || parts.isEmpty()) {
            return new StringPlus("");
        }
        String safeDelimiter = delimiter == null ? "" : delimiter;
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (StringPlus part : parts) {
            if (!first) {
                sb.append(safeDelimiter);
            }
            sb.append(part == null ? "" : part);
            first = false;
        }
        return new StringPlus(sb.toString());
    }

    /**
     * Efficiently concatenates a list of StringPlus instances with no delimiter.
     * Null values are treated as empty strings. Returns empty if list is null or empty.
     * @param parts the list of StringPlus to concatenate
     * @return a new StringPlus with all values concatenated
     */
    public static StringPlus concat(List<StringPlus> parts) {
        if (parts == null || parts.isEmpty()) {
            return new StringPlus("");
        }
        StringBuilder sb = new StringBuilder();
        for (StringPlus part : parts) {
            sb.append(part == null ? "" : part.toString());
        }
        return new StringPlus(sb.toString());
    }

    /**
     * Checks if a String is null or empty.
     * @param s the String to check
     * @return true if null or empty
     */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * Checks if a StringPlus is null or empty.
     * @param sp the StringPlus to check
     * @return true if null or empty
     */
    public static boolean isNullOrEmpty(StringPlus sp) {
        return sp == null || sp.toString() == null || sp.toString().isEmpty();
    }

    /**
     * Returns defaultValue if s is null, else s.
     * @param s the String to check
     * @param defaultValue the default value to return if s is null
     * @return s if not null, else defaultValue
     */
    public static String defaultIfNull(String s, String defaultValue) {
        return s == null ? defaultValue : s;
    }

    /**
     * Returns defaultValue if sp is null, else sp.
     * @param sp the StringPlus to check
     * @param defaultValue the default value to return if sp is null
     * @return sp if not null, else defaultValue
     */
    public static StringPlus defaultIfNull(StringPlus sp, StringPlus defaultValue) {
        return sp == null ? defaultValue : sp;
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

    /**
     * Returns the number of Unicode code points in the string.
     * @return the code point count, or 0 if value is null
     */
    public int codePointCount() {
        if (value == null) return 0;
        return value.codePointCount(0, value.length());
    }

    /**
     * Returns the code point at the specified code point index.
     * @param codePointIndex the index in code points
     * @return the Unicode code point as an int
     * @throws IndexOutOfBoundsException if index is out of range or value is null
     */
    public int codePointAt(int codePointIndex) {
        if (value == null) throw new IndexOutOfBoundsException("String is null");
        int charIndex = value.offsetByCodePoints(0, codePointIndex);
        return value.codePointAt(charIndex);
    }

    /**
     * Returns a substring based on code point indices.
     * @param beginCodePointIndex the start code point index (inclusive)
     * @param endCodePointIndex the end code point index (exclusive)
     * @return a new StringPlus with the substring
     * @throws IndexOutOfBoundsException if indices are out of range or value is null
     */
    public StringPlus codePointSubstring(int beginCodePointIndex, int endCodePointIndex) {
        if (value == null) throw new IndexOutOfBoundsException("String is null");
        int beginCharIndex = value.offsetByCodePoints(0, beginCodePointIndex);
        int endCharIndex = value.offsetByCodePoints(0, endCodePointIndex);
        return new StringPlus(value.substring(beginCharIndex, endCharIndex));
    }

    /**
     * Returns a substring, clamping indices to valid range. Never throws IndexOutOfBounds.
     * If beginIndex >= endIndex, returns empty string. Null-safe.
     * @param beginIndex the beginning index, inclusive
     * @param endIndex the ending index, exclusive
     * @return a new StringPlus with the substring, or empty if indices invalid or value is null
     */
    public StringPlus safeSubstring(int beginIndex, int endIndex) {
        if (value == null || value.isEmpty()) return new StringPlus("");
        int len = value.length();
        int start = Math.max(0, Math.min(beginIndex, len));
        int end = Math.max(0, Math.min(endIndex, len));
        if (start >= end) return new StringPlus("");
        return new StringPlus(value.substring(start, end));
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
     * Converts the string to lower case using the specified locale.
     * @param locale the locale to use
     * @return a new StringPlus converted to lower case using the locale
     */
    public StringPlus toLowerCase(java.util.Locale locale) {
        if (value == null) return this;
        if (locale == null) return toLowerCase();
        return new StringPlus(value.toLowerCase(locale));
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
     * Converts the string to upper case using the specified locale.
     * @param locale the locale to use
     * @return a new StringPlus converted to upper case using the locale
     */
    public StringPlus toUpperCase(java.util.Locale locale) {
        if (value == null) return this;
        if (locale == null) return toUpperCase();
        return new StringPlus(value.toUpperCase(locale));
    }
    
    /**
     * See {@link String#replace(CharSequence, CharSequence)}.
     * @return a new StringPlus with replacements made.
     */
    public StringPlus replace(CharSequence target, CharSequence replacement) {
        if (value == null) return this;
        return new StringPlus(value.replace(target, replacement));
    }

    /**
     * Normalizes whitespace: replaces all sequences of whitespace (spaces, tabs, newlines, etc.)
     * with a single space and trims the result. Null-safe.
     * @return a new StringPlus with normalized whitespace
     */
    public StringPlus normalizeWhitespace() {
        if (value == null) return this;
        return new StringPlus(value.replaceAll("\\s+", " ").trim());
    }

    /**
     * Returns a new StringPlus with the interned string value (using String.intern()).
     * Use with caution: excessive interning can cause memory leaks in the JVM string pool.
     * @return a new StringPlus with interned value, or this if value is null
     */
    public StringPlus intern() {
        if (value == null) return this;
        return new StringPlus(value.intern());
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
     * Checks if the string matches a given precompiled Pattern.
     * @param pattern The precompiled Pattern.
     * @return true if it matches, false otherwise.
     */
    public boolean matchesRegex(Pattern pattern) {
        if (value == null || pattern == null) return false;
        return pattern.matcher(value).matches();
    }

    /**
     * Extracts all substrings that match a given precompiled Pattern.
     * @param pattern The precompiled Pattern.
     * @return A list of matching strings.
     */
    public List<String> extractMatches(Pattern pattern) {
        if (value == null || pattern == null) return Collections.emptyList();
        List<String> matches = new ArrayList<>();
        Matcher matcher = pattern.matcher(value);
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
    
    /**
     * Returns the bytes of the string using the specified Charset. Null-safe.
     * @param charset the Charset to use (required)
     * @return the byte array, or empty array if value is null
     * @throws NullPointerException if charset is null
     */
    public byte[] getBytes(java.nio.charset.Charset charset) {
        if (charset == null) throw new NullPointerException("Charset must not be null");
        if (value == null) return new byte[0];
        return value.getBytes(charset);
    }

    /**
     * Creates a StringPlus from a byte array and Charset. Null-safe.
     * @param bytes the byte array
     * @param charset the Charset to use (required)
     * @return a new StringPlus, or empty if bytes is null
     * @throws NullPointerException if charset is null
     */
    public static StringPlus fromBytes(byte[] bytes, java.nio.charset.Charset charset) {
        if (charset == null) throw new NullPointerException("Charset must not be null");
        if (bytes == null) return new StringPlus("");
        return new StringPlus(new String(bytes, charset));
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