package edu.nd.oose.springbootdemo.model;

public record Choice(
        Long id,
        String text,
        int votes
) {}
