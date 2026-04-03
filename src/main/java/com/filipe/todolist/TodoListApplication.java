package com.filipe.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the todolist Spring Boot application.
 * <p>
 * Run this class to start the embedded server and initialize the Spring context.
 */
@SpringBootApplication
public class TodoListApplication {

    /**
     * Application entry point. Starts the Spring context and embedded server.
     *
     * @param args runtime arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(TodoListApplication.class, args);
    }

}
