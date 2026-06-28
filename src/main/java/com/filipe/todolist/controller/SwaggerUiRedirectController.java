package com.filipe.todolist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Keeps backward compatibility for a custom Swagger UI path that some
 * environments may not honor depending on springdoc version/runtime image.
 */
@Controller
public class SwaggerUiRedirectController {

    @GetMapping("/v3/swagger-ui.html")
    public String redirectToSwaggerUiIndex() {
        return "redirect:/swagger-ui/index.html";
    }
}

