package com.smalaca.taskamanager.devnull;

import org.junit.jupiter.api.Test;

class DevNullDirectoryImplTest {
    @Test
    void shouldDoNothing() {
        new DevNullDirectoryImpl().forget();
    }
}