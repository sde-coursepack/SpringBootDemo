package edu.nd.oose.springbootdemo.controller;

import edu.nd.oose.springbootdemo.model.Question;
import edu.nd.oose.springbootdemo.repository.PollRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
public class ViewController {

    private final PollRepository pollRepository;

    public ViewController(PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }

    // 1. List all questions
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("questions", pollRepository.findAllQuestions());
        return "index";
    }

    // 2. Display create question form
    @GetMapping("/create")
    public String showCreateForm() {
        return "create";
    }

    // Handle create question form submission
    @PostMapping("/create")
    public String createQuestion(@RequestParam String text, @RequestParam String choicesRaw) {
        List<String> choices = Arrays.stream(choicesRaw.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        pollRepository.createQuestion(text, choices);
        return "redirect:/";
    }

    // 3. Display vote form for a question
    @GetMapping("/vote/{questionId}")
    public String showVoteForm(@PathVariable Long questionId, Model model) {
        Question question = pollRepository.findAllQuestions().stream()
                .filter(q -> q.id().equals(questionId))
                .findFirst()
                .orElse(null);

        if (question == null) return "redirect:/";

        model.addAttribute("question", question);
        return "vote";
    }

    // Handle vote submission
    @PostMapping("/vote/{questionId}")
    public String submitVote(@PathVariable Long questionId, @RequestParam Long choiceId) {
        pollRepository.vote(questionId, choiceId);
        return "redirect:/results/" + questionId;
    }

    // 4. Display results for a question
    @GetMapping("/results/{questionId}")
    public String showResults(@PathVariable Long questionId, Model model) {
        pollRepository.getResults(questionId).ifPresent(results -> model.addAttribute("results", results));
        return "results";
    }
}
