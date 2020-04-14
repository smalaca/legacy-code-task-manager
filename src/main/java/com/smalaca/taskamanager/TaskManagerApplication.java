package com.smalaca.taskamanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskManagerApplication {
    private TaskManagerApplication() {}

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }
}
