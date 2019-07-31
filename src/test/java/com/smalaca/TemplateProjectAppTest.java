package com.smalaca;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TemplateProjectAppTest {
    @Test
    void shouldReturnName() {
        String result = new TemplateProjectApp().name();

        assertThat(result).isEqualTo("Template Project");
    }
}
