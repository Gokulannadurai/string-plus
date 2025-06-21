# StringPlus - The Modern Java String Utility

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/ideas2it/stringplus)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/java-21+-blue.svg)](https://www.oracle.com/java/technologies/downloads/)

**StringPlus** is a powerful, immutable, and feature-rich string manipulation library for Java. It encapsulates the standard `java.lang.String` and extends it with a modern, fluent API and dozens of utilities designed to solve real-world backend development challenges.

It provides a clean, readable, and efficient way to handle everything from simple case changes to complex data masking and URL slugification.

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