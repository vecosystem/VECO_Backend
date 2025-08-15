package com.example.Veco.global.healthcheck;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/healthcheck")
    @Hidden
    public String healthcheck() {
        return "OK";
    }
}
