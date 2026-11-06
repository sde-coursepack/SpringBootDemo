package edu.nd.oose.springbootdemo.repository;

import edu.nd.oose.springbootdemo.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PollRepository {

    private final JdbcTemplate jdbcTemplate;

    public PollRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Initialize SQLite tables on application startup
    @PostConstruct
    public void initDatabase() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS questions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                text TEXT NOT NULL
            );
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS choices (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                question_id INTEGER NOT NULL,
                text TEXT NOT NULL,
                votes INTEGER DEFAULT 0,
                FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
            );
        """);
    }

    // 1. List all questions with their choices
    public List<Question> findAllQuestions() {
        String sql = "SELECT id, text FROM questions";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            long id = rs.getLong("id");
            String text = rs.getString("text");
            List<Choice> choices = findChoicesByQuestionId(id);
            return new Question(id, text, choices);
        });
    }

    // 2. Create a question and its associated choices
    @Transactional
    public Question createQuestion(String text, List<String> choiceTexts) {
        String sqlQuestion = "INSERT INTO questions (text) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuestion, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, text);
            return ps;
        }, keyHolder);

        long questionId = keyHolder.getKey().longValue();

        List<Choice> choices = new ArrayList<>();
        String sqlChoice = "INSERT INTO choices (question_id, text, votes) VALUES (?, ?, 0)";

        for (String choiceText : choiceTexts) {
            KeyHolder choiceKeyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sqlChoice, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, questionId);
                ps.setString(2, choiceText);
                return ps;
            }, choiceKeyHolder);

            long choiceId = choiceKeyHolder.getKey().longValue();
            choices.add(new Choice(choiceId, choiceText, 0));
        }

        return new Question(questionId, text, choices);
    }

    // 3. Vote on a specific choice for a question
    public boolean vote(Long questionId, Long choiceId) {
        String sql = "UPDATE choices SET votes = votes + 1 WHERE id = ? AND question_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql, choiceId, questionId);
        return rowsUpdated > 0;
    }

    // 4. Get detailed results for a single question
    public Optional<PollResult> getResults(Long questionId) {
        String questionSql = "SELECT text FROM questions WHERE id = ?";
        List<String> questions = jdbcTemplate.query(questionSql, (rs, rowNum) -> rs.getString("text"), questionId);

        if (questions.isEmpty()) {
            return Optional.empty();
        }

        String questionText = questions.get(0);
        List<Choice> choices = findChoicesByQuestionId(questionId);

        int totalVotes = choices.stream().mapToInt(Choice::votes).sum();

        List<PollResult.ChoiceResult> choiceResults = choices.stream().map(c -> {
            double percentage = totalVotes == 0 ? 0.0 : ((double) c.votes() / totalVotes) * 100.0;
            return new PollResult.ChoiceResult(c.id(), c.text(), c.votes(), Math.round(percentage * 100.0) / 100.0);
        }).toList();

        return Optional.of(new PollResult(questionId, questionText, totalVotes, choiceResults));
    }

    private List<Choice> findChoicesByQuestionId(Long questionId) {
        String sql = "SELECT id, text, votes FROM choices WHERE question_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Choice(
                rs.getLong("id"),
                rs.getString("text"),
                rs.getInt("votes")
        ), questionId);
    }
}
