package com.filipe.todolist.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SwaggerUiRedirectControllerTest {

    @Test
    void redirectsLegacySwaggerUiPathToIndex() {
        SwaggerUiRedirectController controller = new SwaggerUiRedirectController();

        String view = controller.redirectToSwaggerUiIndex();

        assertEquals("redirect:/swagger-ui/index.html", view);
    }
}

