package edu.nd.oose.springbootdemo.model;

import java.util.List;

public record Question(
        Long id,
        String text,
        List<Choice> choices
) {}
