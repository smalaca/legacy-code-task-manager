package com.smalaca;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RentalAppTest {
    @Test
    void shouldReturnName() {
        String result = new RentalApp().name();

        assertThat(result).isEqualTo("Domain Driven Design Rental App");
    }
}
