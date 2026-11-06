# Spring Boot Demo

Project: this project demonstrates using Spring Boot to build a simple RESTful API.

## Requirements

* **Java 17** or higher

## Setup and running

To build and start the server, use:

```bash
./gradlew build
```

Start the Spring Boot server on `http://localhost:8080` using Gradle:

```bash
./gradlew run
```

Or using the Spring Boot plugin task:

```bash
./gradlew bootRun
```

The SQLite database file (`poll.db`) will automatically be created in the root directory if it does not already exist.

## Using the server

### 1. Create a New Poll Question

Create a new question along with its multiple-choice options.

* **Endpoint:** `POST /api/polls`
* **Headers:** `Content-Type: application/json`

#### Request Body:
```json
{
  "text": "What is your favorite programming language?",
  "choices": ["Java", "Kotlin", "Python", "Go"]
}
```

#### Bash Command:
```bash
curl -X POST http://localhost:8080/api/polls \
  -H "Content-Type: application/json" \
  -d '{
    "text": "What is your favorite programming language?",
    "choices": ["Java", "Kotlin", "Python", "Go"]
  }'
```

#### Example Response (`201 Created`):
```json
{
  "id": 1,
  "text": "What is your favorite programming language?",
  "choices": [
    { "id": 1, "text": "Java", "votes": 0 },
    { "id": 2, "text": "Kotlin", "votes": 0 },
    { "id": 3, "text": "Python", "votes": 0 },
    { "id": 4, "text": "Go", "votes": 0 }
  ]
}
```

---

### 2. List All Poll Questions

Retrieve a list of all existing poll questions and their respective choices.

* **Endpoint:** `GET /api/polls`

#### Bash Command:
```bash
curl -X GET http://localhost:8080/api/polls
```

#### Example Response (`200 OK`):
```json
[
  {
    "id": 1,
    "text": "What is your favorite programming language?",
    "choices": [
      {"id": 1, "text": "Java", "votes": 1},
      {"id": 2, "text": "Kotlin", "votes": 2},
      {"id": 3, "text": "Python", "votes": 0},
      {"id": 4, "text": "Go", "votes": 0}
    ]
  },
  {
    "id": 2,
    "text": "Pick a starter PokÃ©mon:",
    "choices": [
      {"id": 5, "text": "Charmander", "votes": 0},
      {"id": 6, "text": "Squirtle", "votes": 0},
      {"id": 7, "text": "Bulbasaur", "votes": 0},
      {"id": 8, "text": "Pikachu", "votes": 0},
      {"id": 9, "text": "Eevee", "votes": 0}
    ]
  }
]

```

---

### 3. Vote on a Choice

Cast a vote for a specific choice associated with a question.

* **Endpoint:** `POST /api/polls/{questionId}/vote/{choiceId}`

#### Bash Command:
```bash
# Vote for Choice ID 2 (Kotlin) on Question ID 1
curl -X POST http://localhost:8080/api/polls/1/vote/2
```

#### Example Response (`200 OK`):
```json
{
  "success": true,
  "message": "Vote recorded successfully.",
  "questionId": 1,
  "choiceId": 2
}
```

#### Failure Response (`404 Not Found`):
```json
{
  "success": false,
  "error": "Invalid question ID or choice ID."
}
```

---

### 4. Get Detailed Poll Results

Fetch the detailed results of a specific poll question, including individual vote counts and total percentages.

* **Endpoint:** `GET /api/polls/{questionId}/results`

#### Bash Command:
```bash
curl -X GET http://localhost:8080/api/polls/1/results
```

#### Example Response (`200 OK`):
```json
{
  "questionId": 1,
  "questionText": "What is your favorite programming language?",
  "totalVotes": 4,
  "results": [
    { "choiceId": 1, "choiceText": "Java", "votes": 1, "percentage": 25.0 },
    { "choiceId": 2, "choiceText": "Kotlin", "votes": 2, "percentage": 50.0 },
    { "choiceId": 3, "choiceText": "Python", "votes": 1, "percentage": 25.0 },
    { "choiceId": 4, "choiceText": "Go", "votes": 0, "percentage": 0.0 }
  ]
}
```

#### Failure Response (`404 Not Found`):
```json
{
  "error": "Question not found with ID: 999"
}
```