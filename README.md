# StringPlus - The Modern Java String Utility

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/ideas2it/stringplus)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/java-21+-blue.svg)](https://www.oracle.com/java/technologies/downloads/)

**StringPlus** is a powerful, immutable, and feature-rich string manipulation library for Java. It encapsulates the standard `java.lang.String` and extends it with a modern, fluent API and dozens of utilities designed to solve real-world backend development challenges.

It provides a clean, readable, and efficient way to handle everything from simple case changes to complex data masking and URL slugification.

## StringPlus vs Java String: Feature Comparison

StringPlus extends the standard Java String with dozens of modern, backend-focused features for security, productivity, and maintainability. Here's how it compares:

| Feature                        | StringPlus | Java String |
|--------------------------------|:----------:|:-----------:|
| Immutability                   |     ✔      |      ✔      |
| CharSequence/Serializable      |     ✔      |      ✔      |
| Fluent API                     |     ✔      |      ✔      |
| Null Safety                    |   Better   |   Weaker    |
| Custom Trimming                |     ✔      |      ✗      |
| Slugification                  |     ✔      |      ✗      |
| Title/Capitalize               |     ✔      |      ✗      |
| Levenshtein Distance           |     ✔      |      ✗      |
| Regex (Pattern Overload)       |     ✔      |      ✗      |
| Unicode Code Point Methods     |     ✔      |   Partial   |
| Safe Substring                 |     ✔      |      ✗      |
| Null/Empty Helpers             |     ✔      |      ✗      |
| Security Masking (Email/Phone) |     ✔      |      ✗      |
| Batch Join/Concat              |     ✔      |   Partial   |
| String Interning               |     ✔      |      ✔      |
| Encoding/Decoding (Charset)    |     ✔      |      ✔      |
| Accent Removal                 |     ✔      |      ✗      |
| Word Count/Tokenize            |     ✔      |      ✗      |

## Key Features

*   **Fluent API**: Chain methods together for expressive and readable code.
*   **Immutable by Design**: Every operation returns a new `StringPlus` instance, ensuring thread safety.
*   **Rich Utility Set**: Over 10 powerful utilities not found in `java.lang.String`.
*   **Secure**: Built-in methods for masking sensitive data like emails and phone numbers.
*   **Reliable**: Rigorously tested against a wide range of edge cases.
*   **Easy to Use**: Simple to integrate into any Maven project.

## Installation

To add StringPlus to your project, add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.ideas2it</groupId>
    <artifactId>string-plus</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Start

See how easy it is to chain multiple operations together with the fluent API:

```java
import com.ideas2it.stringplus.StringPlus;

public class Main {
    public static void main(String[] args) {
        StringPlus text = new StringPlus("  java is AWESOME!  ");

        StringPlus result = text.trim()             // "java is AWESOME!"
                                .titleCase()        // "Java Is Awesome!"
                                .replace("Awesome", "Powerful") // "Java Is Powerful!"
                                .slugify();         // "java-is-powerful"

        System.out.println(result); // Outputs: "java-is-powerful"
    }
}
```

---

## Advanced Utility Methods

Here is a detailed look at the powerful utilities that `StringPlus` provides.

### 1. `capitalize()`

Converts the first character of a string to uppercase and the rest to lowercase. Ideal for formatting names or sentence beginnings.

**Example:**
```java
StringPlus text = new StringPlus("jAVa iS fUn.");
StringPlus capitalized = text.capitalize();

System.out.println(capitalized); // "Java is fun."
```

### 2. `titleCase()`

Converts a string to "title case," where the first letter of every word is capitalized. Perfect for headlines or proper nouns.

**Example:**
```java
StringPlus heading = new StringPlus("the quick brown fox");
StringPlus title = heading.titleCase();

System.out.println(title); // "The Quick Brown Fox"
```

### 3. `trimChars(char... chars)`

Removes a custom set of characters from both the beginning and end of a string. More powerful than the standard `trim()`.

**Example:**
```java
StringPlus messyData = new StringPlus("___-Hello World!-_--");
StringPlus cleaned = messyData.trimChars('_', '-');

System.out.println(cleaned); // "Hello World!"
```

### 4. `slugify()`

Transforms a string into a URL-friendly "slug." It converts to lowercase, removes accents, and replaces non-alphanumeric characters with a hyphen.

**Example:**
```java
StringPlus blogTitle = new StringPlus("Java Is Grêat! (and powerful)");
StringPlus urlSlug = blogTitle.slugify();

System.out.println(urlSlug); // "java-is-great-and-powerful"
```

### 5. `splitAndTrim(String delimiter)`

Splits a string by a delimiter and automatically trims whitespace from each resulting substring. Perfect for parsing CSV or other delimited data.

**Example:**
```java
StringPlus tags = new StringPlus("java,   spring boot,  microservices  ");
java.util.List<StringPlus> tagList = tags.splitAndTrim(",");

// tagList will contain ["java", "spring boot", "microservices"]
tagList.forEach(System.out::println);
```

### 6. `levenshteinDistance(String other)`

Calculates the Levenshtein distance between two strings—the minimum number of edits required to change one string into the other. Invaluable for "Did you mean...?" features or fuzzy search.

**Example:**
```java
StringPlus correct = new StringPlus("java");
int distance = correct.levenshteinDistance("jvaa"); // 1 substitution ('v' for 'a')

System.out.println("Distance: " + distance); // "Distance: 1"
```

### 7. `extractMatches(String regex)`

Finds all substrings that match a regular expression and returns them as a list. Easily extract emails, phone numbers, or hashtags from text.

**Example:**
```java
StringPlus content = new StringPlus("Contact us at support@example.com or sales@example.com.");
java.util.List<String> emails = content.extractMatches("[\\w.-]+@[\\w.-]+");

// emails will contain ["support@example.com", "sales@example.com"]
System.out.println(emails);
```

### 8. `removeAccents()`

Normalizes a string by removing diacritical marks (accents). Crucial for ensuring "crème" and "creme" are treated as equivalent in searches.

**Example:**
```java
StringPlus accented = new StringPlus("Crème brûlée");
StringPlus normalized = accented.removeAccents();

System.out.println(normalized); // "Creme brulee"
```

### 9. `maskEmail()`

Masks an email address for security, revealing only the first character of the username. Standard practice for protecting user data.

**Example:**
```java
StringPlus email = new StringPlus("my.secret.email@example.com");
StringPlus masked = email.maskEmail();

System.out.println(masked); // "m**************@example.com"
```

### 10. `maskPhone()`

Masks a phone number, hiding all digits except for the last four. Essential for protecting sensitive information in logs and UIs.

**Example:**
```java
StringPlus phone = new StringPlus("1-800-555-1234");
StringPlus maskedPhone = phone.maskPhone();

System.out.println(maskedPhone); // "1-***-***-1234"
```

### 11. `join(List<StringPlus> parts, String delimiter)` and `join(List<String> parts, String delimiter)`

Efficiently joins a list of strings (or StringPlus instances) with a delimiter. Nulls are treated as empty strings. Safer and faster than manual concatenation in loops.

**Example:**
```java
import java.util.List;

// Joining StringPlus instances
List<StringPlus> words = List.of(new StringPlus("java"), new StringPlus("spring"), new StringPlus("react"));
StringPlus joined = StringPlus.join(words, ", ");
System.out.println(joined); // "java, spring, react"

// Joining plain strings
List<String> tags = List.of("api", "backend", "2024");
StringPlus tagString = StringPlus.join(tags, "|");
System.out.println(tagString); // "api|backend|2024"
```

### 12. `toLowerCase(Locale locale)` and `toUpperCase(Locale locale)`

Locale-sensitive case conversion, crucial for languages like Turkish where 'I' and 'i' behave differently. Falls back to default if locale is null.

**Example:**
```java
import java.util.Locale;
StringPlus turkish = new StringPlus("Iİıi");
System.out.println(turkish.toLowerCase(new Locale("tr"))); // "ıii̇"
System.out.println(turkish.toLowerCase(Locale.ENGLISH));    // "iiıi"

StringPlus lower = new StringPlus("iı");
System.out.println(lower.toUpperCase(new Locale("tr")));   // "İI"
System.out.println(lower.toUpperCase(Locale.ENGLISH));      // "II"
```

### 13. `codePointCount()`, `codePointAt(int codePointIndex)`, and `codePointSubstring(int, int)`

Unicode-safe operations for counting, accessing, and slicing by code points (not just chars). Essential for emoji and multilingual text.

**Example:**
```java
StringPlus emoji = new StringPlus("a🙂b");
System.out.println(emoji.codePointCount()); // 3
System.out.println(emoji.codePointAt(1));   // 128578 (U+1F642, the code point for 🙂)
System.out.println(emoji.codePointSubstring(1, 3)); // "🙂b"
```

### 14. `safeSubstring(int beginIndex, int endIndex)`

Returns a substring, but never throws IndexOutOfBounds—clamps indices to valid range. Returns empty if indices are invalid or reversed. Null-safe.

**Example:**
```java
StringPlus sp = new StringPlus("abcdef");
System.out.println(sp.safeSubstring(1, 4));    // "bcd"
System.out.println(sp.safeSubstring(-5, 99));  // "abcdef"
System.out.println(sp.safeSubstring(4, 2));    // ""
System.out.println(new StringPlus(null).safeSubstring(0, 2)); // ""
```

### 15. `isNullOrEmpty(...)` and `defaultIfNull(...)`

Helpers for null/empty checks and defaulting, for both String and StringPlus types. Makes code safer and more readable.

**Example:**
```java
System.out.println(StringPlus.isNullOrEmpty((String)null)); // true
System.out.println(StringPlus.isNullOrEmpty(""));          // true
System.out.println(StringPlus.isNullOrEmpty("abc"));       // false

System.out.println(StringPlus.isNullOrEmpty(new StringPlus(null))); // true
System.out.println(StringPlus.isNullOrEmpty(new StringPlus("")));  // true
System.out.println(StringPlus.isNullOrEmpty(new StringPlus("abc")));// false

System.out.println(StringPlus.defaultIfNull(null, "default")); // "default"
System.out.println(StringPlus.defaultIfNull("abc", "default")); // "abc"

StringPlus def = new StringPlus("default");
System.out.println(StringPlus.defaultIfNull(null, def)); // "default"
System.out.println(StringPlus.defaultIfNull(new StringPlus("abc"), def)); // "abc"
```

### 16. `normalizeWhitespace()`

Replaces all sequences of whitespace (spaces, tabs, newlines, etc.) with a single space and trims the result. Null-safe.

**Example:**
```java
StringPlus messy = new StringPlus("  a   b\t\tc\n  ");
System.out.println(messy.normalizeWhitespace()); // "a b c"
System.out.println(new StringPlus("").normalizeWhitespace()); // ""
System.out.println(new StringPlus(null).normalizeWhitespace()); // null
```

### 17. `intern()`

Returns a new StringPlus with the interned string value (using String.intern()). Use with caution: excessive interning can cause memory leaks in the JVM string pool.

**Example:**
```java
StringPlus sp1 = new StringPlus("abc");
StringPlus sp2 = new StringPlus("abc");
StringPlus interned1 = sp1.intern();
StringPlus interned2 = sp2.intern();
System.out.println(interned1 == interned2); // false (different objects)
System.out.println(interned1.toString() == interned2.toString()); // true (same interned string reference)
```

### 18. `concat(List<StringPlus> parts)`

Efficiently concatenates a list of StringPlus values with no delimiter. Nulls are treated as empty. Use for batch operations to avoid performance pitfalls of repeated string concatenation.

**Example:**
```java
List<StringPlus> parts = List.of(new StringPlus("a"), new StringPlus("b"), new StringPlus("c"));
StringPlus result = StringPlus.concat(parts);
System.out.println(result); // "abc"

System.out.println(StringPlus.concat(null)); // ""
System.out.println(StringPlus.concat(List.of())); // ""
```

### 19. `matchesRegex(Pattern)` and `extractMatches(Pattern)`

Overloads that accept precompiled Pattern objects for better performance in repeated regex operations. Null-safe.

**Example:**
```java
import java.util.regex.Pattern;
Pattern digits = Pattern.compile("\\d+");
StringPlus sp = new StringPlus("a1b22c333");
System.out.println(sp.matchesRegex(Pattern.compile("a\\d+b\\d+"))); // false
System.out.println(sp.extractMatches(digits)); // [1, 22, 333]
```

### 20. `getBytes(Charset)` and `fromBytes(byte[], Charset)`

Encode a StringPlus to a byte array or decode from a byte array, using an explicit Charset (e.g., UTF-8, UTF-16). Null-safe, explicit charset required.

**Example:**
```java
import java.nio.charset.StandardCharsets;
StringPlus sp = new StringPlus("héllo🙂");
byte[] bytes = sp.getBytes(StandardCharsets.UTF_8);
StringPlus decoded = StringPlus.fromBytes(bytes, StandardCharsets.UTF_8);
System.out.println(decoded); // "héllo🙂"
```

---

## How to Contribute

Contributions are welcome! If you would like to contribute, please follow these steps:

1.  Fork the repository.
2.  Create a new branch (`git checkout -b feature/your-feature`).
3.  Make your changes and add tests.
4.  Commit your changes (`git commit -m 'Add some feature'`).
5.  Push to the branch (`git push origin feature/your-feature`).
6.  Open a new Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details. 