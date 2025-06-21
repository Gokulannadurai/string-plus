# Custom String (`MyString`) Class Requirements

## 1. Introduction

This document outlines the requirements for a custom `StringPlus` class in Java. The goal is to create a powerful, immutable, and feature-rich string manipulation library that extends the standard `java.lang.String` capabilities with a modern, fluent API and additional utilities.

## 2. Core Design Principles

*   **Immutability**: Every method that transforms the string will return a new `StringPlus` instance, leaving the original unchanged.
*   **Fluent API**: Methods should be chainable to allow for expressive and readable code.
*   **Compatibility**: The class should provide easy conversion to and from the standard `java.lang.String`.

## 3. Core Features

The `MyString` class will encapsulate a `String` value and provide methods for manipulation and analysis.

```java
public class MyString {
    private final String value;

    public MyString(String value) {
        this.value = value;
    }

    // ... methods ...

    @Override
    public String toString() {
        return value;
    }
}
```

## 4. Feature Specifications

### 4.1. Chained Fluent API

All manipulation methods must return a `new MyString` instance to allow for method chaining.

**Example:**

```java
MyString result = new MyString("  Hello World!  ")
    .trim()
    .toLowerCase()
    .replace("world", "java")
    .capitalize(); // Expected: "Hello java"
```

### 4.2. Case Manipulation

#### 4.2.1. `capitalize()`

Converts the first character of the string to upper case and the rest of the string to lower case.

**Implementation Example:**

```java
public MyString capitalize() {
    if (value == null || value.isEmpty()) {
        return this;
    }
    return new MyString(Character.toUpperCase(value.charAt(0)) + value.substring(1).toLowerCase());
}
```

#### 4.2.2. `titleCase()`

Converts the string to title case, where the first letter of each word is capitalized.

**Implementation Example:**

```java
public MyString titleCase() {
    if (value == null || value.isEmpty()) {
        return this;
    }
    String[] words = value.toLowerCase().split("\\s+");
    StringBuilder sb = new StringBuilder();
    for (String word : words) {
        if (!word.isEmpty()) {
            sb.append(Character.toUpperCase(word.charAt(0)))
              .append(word.substring(1))
              .append(" ");
        }
    }
    return new MyString(sb.toString().trim());
}
```

### 4.3. Advanced Trimming

#### 4.3.1. `trimChars(char... chars)`

Trims specified characters from the beginning and end of the string.

**Implementation Example:**

```java
public MyString trimChars(char... chars) {
    if (value == null) return this;
    String result = value;
    for (char c : chars) {
        String escapedChar = java.util.regex.Pattern.quote(String.valueOf(c));
        result = result.replaceAll("^" + escapedChar + "+|" + escapedChar + "+$", "");
    }
    return new MyString(result);
}
```

### 4.4. URL-Friendly Strings

#### 4.4.1. `slugify()`

Converts the string into a URL-friendly "slug".

**Implementation Example:**

```java
public MyString slugify() {
    if (value == null) return new MyString("");
    // Normalizer.normalize to handle accented characters
    String normalized = java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD); 
    return new MyString(normalized.toLowerCase()
        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
        .replaceAll("[^a-z0-9]+", "-")
        .replaceAll("^-|-$", ""));
}
```
**Example:** `"Java Is Great!"` → `"java-is-great"`

### 4.5. Smart Splitting

#### 4.5.1. `splitAndTrim(String delimiter)`

Splits the string by a given delimiter and trims whitespace from each resulting element.

**Implementation Example:**

```java
public java.util.List<MyString> splitAndTrim(String delimiter) {
    if (value == null) return java.util.Collections.emptyList();
    return java.util.Arrays.stream(value.split(java.util.regex.Pattern.quote(delimiter)))
                 .map(String::trim)
                 .map(MyString::new)
                 .collect(java.util.stream.Collectors.toList());
}
```

### 4.6. Fuzzy String Matching

#### 4.6.1. `levenshteinDistance(String other)`

Calculates the Levenshtein distance between the `MyString` instance's value and another string.

**Implementation Example:**

```java
public int levenshteinDistance(String other) {
    if (value == null || other == null) return -1;
    int[][] dp = new int[value.length() + 1][other.length() + 1];
    for (int i = 0; i <= value.length(); i++) {
        for (int j = 0; j <= other.length(); j++) {
            if (i == 0) {
                dp[i][j] = j;
            } else if (j == 0) {
                dp[i][j] = i;
            } else if (value.charAt(i - 1) == other.charAt(j - 1)) {
                dp[i][j] = dp[i - 1][j - 1];
            } else {
                dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
            }
        }
    }
    return dp[value.length()][other.length()];
}
```

### 4.7. Regular Expression Helpers

#### 4.7.1. `matchesRegex(String regex)`

Checks if the string matches a given regular expression.

**Implementation Example:**

```java
public boolean matchesRegex(String regex) {
    if (value == null) return false;
    return java.util.regex.Pattern.matches(regex, value);
}
```

#### 4.7.2. `extractMatches(String regex)`

Extracts all substrings that match a given regular expression.

**Implementation Example:**

```java
public java.util.List<String> extractMatches(String regex) {
    if (value == null) return java.util.Collections.emptyList();
    java.util.List<String> matches = new java.util.ArrayList<>();
    java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(regex).matcher(value);
    while (matcher.find()) {
        matches.add(matcher.group());
    }
    return matches;
}
```

### 4.8. Internationalization (i18n) Support

#### 4.8.1. `removeAccents()`

Removes diacritical marks (accents) from the string.

**Implementation Example:**

```java
public MyString removeAccents() {
    if (value == null) return this;
    String normalized = java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD);
    return new MyString(normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", ""));
}
```

#### 4.8.2. `detectDirection()`
*(Future implementation)* Detects if the text direction is Right-to-Left (RTL) or Left-to-Right (LTR).

#### 4.8.3. `graphemeCount()`
*(Future implementation)* Counts the number of user-perceived characters (graphemes), including complex emojis.

### 4.9. Sensitive Data Masking

#### 4.9.1. `maskEmail()`

Masks an email address.

**Implementation Example:**

```java
public MyString maskEmail() {
    if (value == null) return this;
    return new MyString(value.replaceAll("(?<=.).(?=[^@]*?@)", "*"));
}
```

#### 4.9.2. `maskPhone()`

Masks a phone number, showing only the last 4 digits.

**Implementation Example:**

```java
public MyString maskPhone() {
    if (value == null) return this;
    return new MyString(value.replaceAll("\\d(?=\\d{4})", "*"));
}
```

### 4.10. Text Analysis

#### 4.10.1. `wordCount()`

Counts the number of words in the string.

**Implementation Example:**

```java
public int wordCount() {
    if (value == null || value.trim().isEmpty()) {
        return 0;
    }
    return value.trim().split("\\s+").length;
}
```

#### 4.10.2. `tokenize()`

Splits the string into a list of tokens (words).

**Implementation Example:**

```java
public java.util.List<String> tokenize() {
    if (value == null) return java.util.Collections.emptyList();
    return java.util.Arrays.asList(value.split("\\W+"));
}
```

#### 4.10.3. `detectLanguage()`
*(Future implementation)* Attempts to detect the language of the text. This would likely require an external library. 