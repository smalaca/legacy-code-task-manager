package com.smalaca.taskamanager.processor;

import com.smalaca.taskamanager.model.interfaces.ToDoItem;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class ToDoItemProcessorTest {
    private final ToDoItemProcessor processor = new ToDoItemProcessor();

    @Test
    void shouldDoNothing() {
        ToDoItem toDoItem = mock(ToDoItem.class);

        processor.processFor(toDoItem);

        verifyNoInteractions(toDoItem);
    }
}