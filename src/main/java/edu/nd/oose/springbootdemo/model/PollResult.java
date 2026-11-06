package edu.nd.oose.springbootdemo.model;

import java.util.List;

public record PollResult(
        Long questionId,
        String questionText,
        int totalVotes,
        List<ChoiceResult> results
) {
    public record ChoiceResult(
            Long choiceId,
            String choiceText,
            int votes,
            double percentage
    ) {}
}
