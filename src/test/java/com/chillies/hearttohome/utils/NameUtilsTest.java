package com.chillies.hearttohome.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NameUtilsTest {
    @Test void formatsFirstNameAndWhitespace() {
        assertEquals("Ritu", NameUtils.formatFirstName("  RITU   Sharma "));
    }

    @Test void retainsAndFormatsTitles() {
        assertEquals("Dr. Ritu", NameUtils.formatFirstName("dr. RITU Sharma"));
        assertEquals("Ms. Jane", NameUtils.formatFirstName("Ms Jane Doe"));
    }

    @Test void handlesBlankAndPunctuation() {
        assertEquals("", NameUtils.formatFirstName(null));
        assertEquals("", NameUtils.formatFirstName("  "));
        assertEquals("Ana", NameUtils.formatFirstName("...ANA!!! Smith"));
    }
}
