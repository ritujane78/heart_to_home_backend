package com.chillies.hearttohome.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NameUtilsTest {

    @Test
    void formatFirstNameReturnsEmptyForBlankInput() {
        assertThat(NameUtils.formatFirstName(null)).isEmpty();
        assertThat(NameUtils.formatFirstName("   ")).isEmpty();
    }

    @Test
    void formatFirstNameCapitalizesFirstWord() {
        assertThat(NameUtils.formatFirstName("ritu shrestha")).isEqualTo("Ritu");
    }

    @Test
    void formatFirstNameKeepsRecognizedTitles() {
        assertThat(NameUtils.formatFirstName("dr. maya gurung")).isEqualTo("Dr. Maya");
    }
}
