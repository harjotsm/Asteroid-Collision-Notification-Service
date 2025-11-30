package com.pm.asteroidaltering.controller;

import com.pm.asteroidaltering.service.AsteroidAlteringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/asteroid-altering")
public class AsteroidAlteringController {

    private final AsteroidAlteringService asteroidAlteringService;

    @Autowired
    public AsteroidAlteringController(AsteroidAlteringService asteroidAlteringService) {
        this.asteroidAlteringService = asteroidAlteringService;
    }

    @PostMapping("/alert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void alert() {
        asteroidAlteringService.alert();
    }
}
