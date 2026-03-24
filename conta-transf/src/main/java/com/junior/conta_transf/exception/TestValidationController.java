package com.junior.conta_transf.exception;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
public class TestValidationController {

    @PostMapping("/test/validation")
    void create(@Valid @RequestBody ValidationRequest body) {}

    @PostMapping("/test/validation/multi")
    void createMulti(@Valid @RequestBody MultiValidationRequest body) {}

    public record ValidationRequest(@NotBlank String name) {}
    public record MultiValidationRequest(@NotBlank String name, @NotBlank String doc) {}
}