package com.ideas2it.stringplus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Comprehensive tests for the {@link StringPlus} class.
 *
 * <p>This test suite uses a hacker's mindset to validate every method against
 * a wide range of edge cases, including nulls, empty strings, Unicode, and
 * concurrency scenarios.
 *
 * @author Gokul Annadurai
 * @version 1.0
 * @since 2024-07-25
 */
@DisplayName("StringPlus Comprehensive Tests")
class StringPlusTest {

    @Nested
    @DisplayName("Constructor and Core Methods")
    class CoreTests {
        @Test
        @DisplayName("should handle null input gracefully")
        void testNullConstructor() {
            StringPlus sp = new StringPlus(null);
            assertNull(sp.toString());
            assertEquals(0, sp.length());
        }

        @Test
        @DisplayName("should handle empty string correctly")
        void testEmptyString() {
            StringPlus sp = new StringPlus("");
            assertEquals("", sp.toString());
            assertEquals(0, sp.length());
        }

        @Test
        @DisplayName("toString should return the original string value")
        void testToString() {
            assertEquals("hello", new StringPlus("hello").toString());
            assertEquals("  ", new StringPlus("  ").toString());
        }

        @Test
        @DisplayName("of() factory method should work like constructor")
        void testOfFactory() {
            assertEquals(new StringPlus("test"), StringPlus.of("test"));
            assertEquals(new StringPlus(null), StringPlus.of(null));
        }
    }

    @Nested
    @DisplayName("Case Manipulation")
    class CaseTests {
        @ParameterizedTest
        @CsvSource({
            "hello world, Hello world",
            " aBC, Abc",
            "'', ''",
            "'   ', '   '",
            "1st place, 1st place",
            "$, $"
        })
        @DisplayName("capitalize() should handle various inputs")
        void testCapitalize(String input, String expected) {
            assertEquals(expected, new StringPlus(input).capitalize().toString());
        }
        
        @Test
        @DisplayName("capitalize() on null should return null string")
        void testCapitalizeNull() {
            assertNull(new StringPlus(null).capitalize().toString());
        }

        @ParameterizedTest
        @CsvSource({
            "hello world, Hello World",
            "java is fun, Java Is Fun",
            " aBC  dEF , Abc  Def",
            "'', ''",
            "single, Single",
            "emoji_test 🙂, Emoji_test 🙂"
        })
        @DisplayName("titleCase() should handle various inputs")
        void testTitleCase(String input, String expected) {
            assertEquals(expected, new StringPlus(input).titleCase().toString());
        }

        @Test
        @DisplayName("toLowerCase(Locale) and toUpperCase(Locale) should handle null and locale-specific cases")
        void testLocaleCaseConversion() {
            StringPlus turkish = new StringPlus("Iİıi");
            java.util.Locale tr = new java.util.Locale("tr");
            java.util.Locale en = java.util.Locale.ENGLISH;

            // Turkish locale: 'I'->'ı', 'İ'->'i'
            assertEquals("ıii̇", turkish.toLowerCase(tr).toString());
            // English locale: 'I'->'i', 'İ'->'i', 'ı'->'ı', 'i'->'i'
            assertEquals("iiıi", turkish.toLowerCase(en).toString());

            // Turkish locale: 'i'->'İ', 'ı'->'I'
            StringPlus turkishLower = new StringPlus("iı");
            assertEquals("İI", turkishLower.toUpperCase(tr).toString());
            // English locale: 'i'->'I', 'ı'->'I'
            assertEquals("II", turkishLower.toUpperCase(en).toString());

            // Null locale falls back to default
            assertEquals("abc", new StringPlus("ABC").toLowerCase((java.util.Locale)null).toString());
            assertEquals("ABC", new StringPlus("abc").toUpperCase((java.util.Locale)null).toString());

            // Null string
            assertNull(new StringPlus(null).toLowerCase(tr).toString());
            assertNull(new StringPlus(null).toUpperCase(tr).toString());
        }
    }

    @Nested
    @DisplayName("Trimming")
    class TrimTests {
        @Test
        @DisplayName("trimChars() should remove custom characters")
        void testTrimChars() {
            assertEquals("Hello", new StringPlus("___Hello___").trimChars('_').toString());
            assertEquals("World", new StringPlus("-_-World-_-").trimChars('_', '-').toString());
            assertEquals("NoChange", new StringPlus("NoChange").trimChars('-').toString());
        }

        @Test
        @DisplayName("trimChars() should handle special regex characters safely")
        void testTrimCharsWithRegexChars() {
            assertEquals("Safe", new StringPlus(".*Safe*.").trimChars('.', '*').toString());
        }

        @Test
        @DisplayName("trimChars() on null should not throw error")
        void testTrimCharsNull() {
            assertNull(new StringPlus(null).trimChars('a').toString());
        }
    }
    
    @Nested
    @DisplayName("URL and Data Formatting")
    class FormattingTests {
        @ParameterizedTest
        @CsvSource({
            "Hello World, hello-world",
            "Java Is Grêat!, java-is-great",
            " (extra spaces) , extra-spaces",
            "---test---, test",
            "emoji 🙂 test, emoji-test"
        })
        @DisplayName("slugify() should create URL-friendly slugs")
        void testSlugify(String input, String expected) {
             assertEquals(expected, new StringPlus(input).slugify().toString());
        }

        @Test
        @DisplayName("splitAndTrim() should handle various delimiter cases")
        void testSplitAndTrim() {
            List<StringPlus> result = new StringPlus("java , spring,  react  ").splitAndTrim(",");
            assertEquals(3, result.size());
            assertEquals("java", result.get(0).toString());
            assertEquals("spring", result.get(1).toString());
            assertEquals("react", result.get(2).toString());
        }

        @Test
        @DisplayName("splitAndTrim() on empty string should return empty list")
        void testSplitAndTrimEmpty() {
             assertTrue(new StringPlus("").splitAndTrim(",").isEmpty());
        }
    }

    @Nested
    @DisplayName("Fuzzy Matching and Regex")
    class MatchingTests {
        @ParameterizedTest
        @CsvSource({"kitten, sitting, 3", "java, jvaa, 1", "test, test, 0"})
        @DisplayName("levenshteinDistance() should calculate edits correctly")
        void testLevenshteinDistance(String s1, String s2, int dist) {
            assertEquals(dist, new StringPlus(s1).levenshteinDistance(s2));
        }

        @Test
        @DisplayName("levenshteinDistance() with Unicode should be consistent")
        void testLevenshteinUnicode() {
            // Note: This simple algorithm works on chars, so a multi-char emoji is > 1 distance.
            assertEquals(1, new StringPlus("🙂").levenshteinDistance("😐"));
        }

        @Test
        @DisplayName("extractMatches() should find all regex matches")
        void testExtractMatches() {
            List<String> matches = new StringPlus("Emails: t@a.com, x@b.com").extractMatches("\\w+@\\w+\\.com");
            assertEquals(2, matches.size());
            assertEquals("t@a.com", matches.get(0));
        }
        
        @Test
        @DisplayName("extractMatches() with invalid regex should throw exception")
        void testExtractMatchesInvalidRegex() {
            // This behavior comes from Java's Pattern.compile, which is correct
            assertThrows(PatternSyntaxException.class, () -> {
                new StringPlus("test").extractMatches("[[invalid");
            });
        }
    }

    @Nested
    @DisplayName("Security and Masking")
    class SecurityTests {
        @ParameterizedTest
        @CsvSource({
            "test.email@example.com, t***********@example.com",
            "a@b.c, a@b.c",
            "long.name@domain.com, l**********@domain.com"
        })
        @DisplayName("maskEmail() should mask emails correctly")
        void testMaskEmail(String input, String expected) {
            assertEquals(expected, new StringPlus(input).maskEmail().toString());
        }

        @ParameterizedTest
        @CsvSource({
            "1234567890, ******7890",
            "1234, 1234", // Too short
            "555-555-5555, ***-***-5555"
        })
        @DisplayName("maskPhone() should mask phone numbers correctly")
        void testMaskPhone(String input, String expected) {
            assertEquals(expected, new StringPlus(input).maskPhone().toString());
        }
    }
    
    @Nested
    @DisplayName("i18n and Unicode")
    class I18nTests {
        @Test
        @DisplayName("removeAccents() should strip diacritical marks")
        void testRemoveAccents() {
            assertEquals("Creme brulee", new StringPlus("Crème brûlée").removeAccents().toString());
            assertEquals("aeiou", new StringPlus("áéíóú").removeAccents().toString());
        }
    }

    @Nested
    @DisplayName("Text Analysis")
    class AnalysisTests {
        @ParameterizedTest
        @CsvSource({"Hello world, 2", "'  leading and trailing  ', 3", "oneword, 1", "'', 0"})
        @DisplayName("wordCount() should count words accurately")
        void testWordCount(String input, int count) {
            assertEquals(count, new StringPlus(input).wordCount());
        }
    }

    @Nested
    @DisplayName("Object Contracts and Concurrency")
    class ContractTests {
        @Test
        @DisplayName("equals() and hashCode() should be consistent")
        void testEqualsAndHashCode() {
            StringPlus s1 = new StringPlus("test");
            StringPlus s2 = new StringPlus("test");
            StringPlus s3 = new StringPlus("different");

            assertTrue(s1.equals(s2) && s2.equals(s1));
            assertEquals(s1.hashCode(), s2.hashCode());
            assertNotEquals(s1, s3);
            
            // Interaction with String
            assertTrue(s1.equals("test"));
            assertFalse(s1.equals("different"));
        }

        @Test
        @DisplayName("compareTo() should provide correct ordering")
        void testCompareTo() {
            StringPlus s1 = new StringPlus("a");
            StringPlus s2 = new StringPlus("b");
            StringPlus s3 = new StringPlus("b");

            assertTrue(s1.compareTo(s2) < 0);
            assertTrue(s2.compareTo(s1) > 0);
            assertEquals(0, s2.compareTo(s3));
        }

        @Test
        @DisplayName("should be thread-safe due to immutability")
        void testThreadSafety() throws InterruptedException {
            final StringPlus sharedInstance = new StringPlus("initial-value-slug");
            final ExecutorService service = Executors.newFixedThreadPool(10);

            for (int i = 0; i < 1000; i++) {
                service.submit(() -> {
                    // Each thread performs operations but doesn't change the shared instance
                    StringPlus result = sharedInstance.toUpperCase().replace("S", "Z").slugify();
                    assertEquals("INITIAL-VALUE-ZLUG", result.toString());
                    // Verify the original instance remains untouched
                    assertEquals("initial-value-slug", sharedInstance.toString());
                });
            }
            service.shutdown();
            assertTrue(service.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    @Nested
    @DisplayName("Joining Strings")
    class JoinTests {
        @Test
        @DisplayName("join(List<StringPlus>, delimiter) - normal case")
        void testJoinStringPlusNormal() {
            List<StringPlus> parts = List.of(new StringPlus("a"), new StringPlus("b"), new StringPlus("c"));
            assertEquals("a,b,c", StringPlus.join(parts, ",").toString());
        }

        @Test
        @DisplayName("join(List<StringPlus>, delimiter) - null delimiter")
        void testJoinStringPlusNullDelimiter() {
            List<StringPlus> parts = List.of(new StringPlus("a"), new StringPlus("b"));
            assertEquals("ab", StringPlus.join(parts, null).toString());
        }

        @Test
        @DisplayName("join(List<StringPlus>, delimiter) - null and empty elements")
        void testJoinStringPlusNullAndEmptyElements() {
            List<StringPlus> parts = List.of(null, new StringPlus("a"), null, new StringPlus(""), new StringPlus("b"));
            assertEquals("a,,b", StringPlus.join(parts, ",").toString());
        }

        @Test
        @DisplayName("join(List<StringPlus>, delimiter) - empty list")
        void testJoinStringPlusEmptyList() {
            assertEquals("", StringPlus.join(List.of(), ",").toString());
        }

        @Test
        @DisplayName("join(List<StringPlus>, delimiter) - null list")
        void testJoinStringPlusNullList() {
            assertEquals("", StringPlus.join((List<StringPlus>)null, ",").toString());
        }

        @Test
        @DisplayName("join(List<String>, delimiter) - normal case")
        void testJoinStringNormal() {
            List<StringPlus> parts = List.of(new StringPlus("a"),
                    new StringPlus("b"), new StringPlus("c"));
            assertEquals("a|b|c", StringPlus.join(parts, "|").toString());
        }

        @Test
        @DisplayName("join(List<String>, delimiter) - null delimiter")
        void testJoinStringNullDelimiter() {
            List<StringPlus> parts = List.of(new StringPlus("a"), new StringPlus("b"));
            assertEquals("ab", StringPlus.join(parts, null).toString());
        }

        @Test
        @DisplayName("join(List<String>, delimiter) - null and empty elements")
        void testJoinStringNullAndEmptyElements() {
            List<StringPlus> parts = List.of(null, new StringPlus("a"), null,
                    new StringPlus(""), new StringPlus("b"));
            assertEquals("a,,b", StringPlus.join(parts, ",").toString());
        }

        @Test
        @DisplayName("join(List<String>, delimiter) - empty list")
        void testJoinStringEmptyList() {
            assertEquals("", StringPlus.join(List.of(), ",").toString());
        }

        @Test
        @DisplayName("join(List<String>, delimiter) - null list")
        void testJoinStringNullList() {
            assertEquals("", StringPlus.join((List<StringPlus>)null, ",").toString());
        }

        @Test
        @DisplayName("join(List<StringPlus>, delimiter) - single element")
        void testJoinStringPlusSingleElement() {
            List<StringPlus> parts = List.of(new StringPlus("only"));
            assertEquals("only", StringPlus.join(parts, ",").toString());
        }

        @Test
        @DisplayName("join(List<String>, delimiter) - single element")
        void testJoinStringSingleElement() {
            List<StringPlus> parts = List.of(new StringPlus("only"));
            assertEquals("only", StringPlus.join(parts, ",").toString());
        }
    }

    @Nested
    @DisplayName("Unicode and Code Point Methods")
    class UnicodeTests {
        @Test
        @DisplayName("codePointCount should count Unicode code points correctly")
        void testCodePointCount() {
            assertEquals(3, new StringPlus("abc").codePointCount());
            assertEquals(2, new StringPlus("a🙂").codePointCount()); // emoji is surrogate pair
            assertEquals(0, new StringPlus("").codePointCount());
            assertEquals(0, new StringPlus(null).codePointCount());
        }

        @Test
        @DisplayName("codePointAt should return correct code point for BMP and emoji")
        void testCodePointAt() {
            StringPlus sp = new StringPlus("a🙂b");
            assertEquals('a', sp.codePointAt(0));
            assertEquals(0x1F642, sp.codePointAt(1)); // 🙂
            assertEquals('b', sp.codePointAt(2));
        }

        @Test
        @DisplayName("codePointAt should throw for out-of-bounds or null")
        void testCodePointAtOutOfBounds() {
            assertThrows(IndexOutOfBoundsException.class, () -> new StringPlus(null).codePointAt(0));
            assertThrows(IndexOutOfBoundsException.class, () -> new StringPlus("").codePointAt(0));
            assertThrows(IndexOutOfBoundsException.class, () -> new StringPlus("a").codePointAt(2));
        }

        @Test
        @DisplayName("codePointSubstring should extract correct substring for code points")
        void testCodePointSubstring() {
            StringPlus sp = new StringPlus("a🙂bc");
            assertEquals("🙂b", sp.codePointSubstring(1, 3).toString());
            assertEquals("a🙂bc", sp.codePointSubstring(0, 4).toString());
            assertEquals("", sp.codePointSubstring(2, 2).toString());
        }

        @Test
        @DisplayName("codePointSubstring should throw for out-of-bounds or null")
        void testCodePointSubstringOutOfBounds() {
            assertThrows(IndexOutOfBoundsException.class, () -> new StringPlus(null).codePointSubstring(0, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> new StringPlus("").codePointSubstring(0, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> new StringPlus("a").codePointSubstring(0, 2));
        }
    }

    @Nested
    @DisplayName("Safe Substring")
    class SafeSubstringTests {
        @Test
        @DisplayName("safeSubstring should return correct substring for valid indices")
        void testSafeSubstringValid() {
            StringPlus sp = new StringPlus("abcdef");
            assertEquals("bcd", sp.safeSubstring(1, 4).toString());
        }

        @Test
        @DisplayName("safeSubstring should clamp out-of-bounds indices")
        void testSafeSubstringClamp() {
            StringPlus sp = new StringPlus("abcdef");
            assertEquals("abcdef", sp.safeSubstring(-5, 99).toString());
            assertEquals("", sp.safeSubstring(10, 20).toString());
        }

        @Test
        @DisplayName("safeSubstring should return empty for begin >= end")
        void testSafeSubstringEmpty() {
            StringPlus sp = new StringPlus("abcdef");
            assertEquals("", sp.safeSubstring(3, 3).toString());
            assertEquals("", sp.safeSubstring(4, 2).toString());
        }

        @Test
        @DisplayName("safeSubstring should handle null and empty string")
        void testSafeSubstringNullAndEmpty() {
            assertEquals("", new StringPlus(null).safeSubstring(0, 2).toString());
            assertEquals("", new StringPlus("").safeSubstring(0, 2).toString());
        }
    }

    @Nested
    @DisplayName("Null/Empty Helpers")
    class NullEmptyHelpersTests {
        @Test
        @DisplayName("isNullOrEmpty(String) should detect null and empty strings")
        void testIsNullOrEmptyString() {
            assertTrue(StringPlus.isNullOrEmpty((String)null));
            assertTrue(StringPlus.isNullOrEmpty(""));
            assertFalse(StringPlus.isNullOrEmpty("abc"));
        }

        @Test
        @DisplayName("isNullOrEmpty(StringPlus) should detect null and empty StringPlus")
        void testIsNullOrEmptyStringPlus() {
            assertTrue(StringPlus.isNullOrEmpty((StringPlus)null));
            assertTrue(StringPlus.isNullOrEmpty(new StringPlus(null)));
            assertTrue(StringPlus.isNullOrEmpty(new StringPlus("")));
            assertFalse(StringPlus.isNullOrEmpty(new StringPlus("abc")));
        }

        @Test
        @DisplayName("defaultIfNull(String, String) should return default for null and value for non-null")
        void testDefaultIfNullString() {
            assertEquals("default", StringPlus.defaultIfNull(null, "default"));
            assertEquals("abc", StringPlus.defaultIfNull("abc", "default"));
        }

        @Test
        @DisplayName("defaultIfNull(StringPlus, StringPlus) should return default for null and value for non-null")
        void testDefaultIfNullStringPlus() {
            StringPlus def = new StringPlus("default");
            assertEquals(def, StringPlus.defaultIfNull(null, def));
            StringPlus val = new StringPlus("abc");
            assertEquals(val, StringPlus.defaultIfNull(val, def));
        }
    }

    @Nested
    @DisplayName("Whitespace Normalization")
    class WhitespaceTests {
        @Test
        @DisplayName("normalizeWhitespace should replace multiple spaces/tabs/newlines with single space and trim")
        void testNormalizeWhitespace() {
            assertEquals("a b c", new StringPlus("a   b\t\tc\n").normalizeWhitespace().toString());
            assertEquals("a b", new StringPlus("  a   b  ").normalizeWhitespace().toString());
            assertEquals("a b", new StringPlus("a\n\tb").normalizeWhitespace().toString());
        }

        @Test
        @DisplayName("normalizeWhitespace should handle empty and null")
        void testNormalizeWhitespaceEmptyNull() {
            assertEquals("", new StringPlus("").normalizeWhitespace().toString());
            assertNull(new StringPlus(null).normalizeWhitespace().toString());
        }
    }

    @Nested
    @DisplayName("String Interning")
    class InternTests {
        @Test
        @DisplayName("intern should return interned string value")
        void testIntern() {
            StringPlus sp1 = new StringPlus("abc");
            StringPlus sp2 = new StringPlus("abc");
            StringPlus interned1 = sp1.intern();
            StringPlus interned2 = sp2.intern();
            // Interned strings should have the same reference
            assertSame(interned1.toString(), interned2.toString());
            assertEquals("abc", interned1.toString());
        }

        @Test
        @DisplayName("intern should handle null value")
        void testInternNull() {
            StringPlus sp = new StringPlus(null);
            assertSame(sp, sp.intern());
        }
    }

    @Nested
    @DisplayName("Batch Concatenation")
    class ConcatTests {
        @Test
        @DisplayName("concat should concatenate multiple StringPlus values")
        void testConcatMultiple() {
            List<StringPlus> parts = List.of(new StringPlus("a"), new StringPlus("b"), new StringPlus("c"));
            assertEquals("abc", StringPlus.concat(parts).toString());
        }

        @Test
        @DisplayName("concat should handle null and empty lists")
        void testConcatNullEmpty() {
            assertEquals("", StringPlus.concat(null).toString());
            assertEquals("", StringPlus.concat(List.of()).toString());
        }

        @Test
        @DisplayName("concat should handle single and mixed null elements")
        void testConcatSingleAndNulls() {
            assertEquals("x", StringPlus.concat(List.of(new StringPlus("x"))).toString());
            List<StringPlus> parts = List.of(null, new StringPlus("a"), null, new StringPlus("b"));
            assertEquals("ab", StringPlus.concat(parts).toString());
        }
    }

    @Nested
    @DisplayName("Pattern Regex Overloads")
    class PatternRegexTests {
        @Test
        @DisplayName("matchesRegex(Pattern) should match using precompiled pattern")
        void testMatchesRegexPattern() {
            Pattern p = Pattern.compile("[a-z]+\\d+");
            assertTrue(new StringPlus("abc123").matchesRegex(p));
            assertFalse(new StringPlus("abc").matchesRegex(p));
        }

        @Test
        @DisplayName("matchesRegex(Pattern) should handle nulls")
        void testMatchesRegexPatternNulls() {
            Pattern p = Pattern.compile("abc");
            assertFalse(new StringPlus(null).matchesRegex(p));
            assertFalse(new StringPlus("abc").matchesRegex((Pattern)null));
        }

        @Test
        @DisplayName("extractMatches(Pattern) should extract all matches")
        void testExtractMatchesPattern() {
            Pattern p = Pattern.compile("\\d+");
            List<String> matches = new StringPlus("a1b22c333").extractMatches(p);
            assertEquals(List.of("1", "22", "333"), matches);
        }

        @Test
        @DisplayName("extractMatches(Pattern) should handle nulls and no matches")
        void testExtractMatchesPatternNulls() {
            Pattern p = Pattern.compile("x");
            assertEquals(List.of(), new StringPlus(null).extractMatches(p));
            assertEquals(List.of(), new StringPlus("abc").extractMatches((Pattern)null));
            assertEquals(List.of(), new StringPlus("abc").extractMatches(Pattern.compile("\\d+")));
        }
    }

    @Nested
    @DisplayName("Encoding/Decoding")
    class EncodingTests {
        @Test
        @DisplayName("getBytes(Charset) and fromBytes should round-trip with UTF-8 and UTF-16")
        void testEncodingRoundTrip() {
            StringPlus sp = new StringPlus("héllo🙂");
            java.nio.charset.Charset utf8 = java.nio.charset.StandardCharsets.UTF_8;
            java.nio.charset.Charset utf16 = java.nio.charset.StandardCharsets.UTF_16;
            byte[] utf8Bytes = sp.getBytes(utf8);
            byte[] utf16Bytes = sp.getBytes(utf16);
            assertEquals(sp.toString(), StringPlus.fromBytes(utf8Bytes, utf8).toString());
            assertEquals(sp.toString(), StringPlus.fromBytes(utf16Bytes, utf16).toString());
        }

        @Test
        @DisplayName("getBytes(Charset) should handle null value and throw for null charset")
        void testGetBytesNulls() {
            java.nio.charset.Charset utf8 = java.nio.charset.StandardCharsets.UTF_8;
            assertArrayEquals(new byte[0], new StringPlus(null).getBytes(utf8));
            assertThrows(NullPointerException.class, () -> new StringPlus("abc").getBytes(null));
        }

        @Test
        @DisplayName("fromBytes should handle null bytes and throw for null charset")
        void testFromBytesNulls() {
            java.nio.charset.Charset utf8 = java.nio.charset.StandardCharsets.UTF_8;
            assertEquals("", StringPlus.fromBytes(null, utf8).toString());
            assertThrows(NullPointerException.class, () -> StringPlus.fromBytes(new byte[]{65}, null));
        }
    }
} 