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
import java.util.regex.PatternSyntaxException;

import static org.junit.jupiter.api.Assertions.*;

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
} 