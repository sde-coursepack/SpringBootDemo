package edu.nd.oose.springbootdemo.controller;

import edu.nd.oose.springbootdemo.model.*;
import edu.nd.oose.springbootdemo.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/polls")
public class PollController {

    private final PollRepository pollRepository;

    public PollController(PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }

    // DTO for incoming poll creation request
    public record CreateQuestionRequest(String text, List<String> choices) {}

    // 1. GET /api/polls - List all questions
    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(pollRepository.findAllQuestions());
    }

    // 2. POST /api/polls - Submit a new question with choices
    @PostMapping
    public ResponseEntity<?> createQuestion(@RequestBody CreateQuestionRequest request) {
        if (request.text() == null || request.text().isBlank() || request.choices() == null || request.choices().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Question text and at least one choice are required."));
        }
        Question created = pollRepository.createQuestion(request.text(), request.choices());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 3. POST /api/polls/{questionId}/vote/{choiceId} - Vote on an existing question
    @PostMapping("/{questionId}/vote/{choiceId}")
    public ResponseEntity<?> vote(@PathVariable Long questionId, @PathVariable Long choiceId) {
        boolean success = pollRepository.vote(questionId, choiceId);
        if (success) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Vote recorded successfully.",
                    "questionId", questionId,
                    "choiceId", choiceId
            ));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "error", "Invalid question ID or choice ID."
            ));
        }
    }

    // 4. GET /api/polls/{questionId}/results - See detailed results of a single question
    @GetMapping("/{questionId}/results")
    public ResponseEntity<?> getResults(@PathVariable Long questionId) {
        return pollRepository.getResults(questionId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Question not found with ID: " + questionId)));
    }
}
