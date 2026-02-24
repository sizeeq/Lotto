<div align="center">

# 🎱 LottoApp

**Online Lotto game with automated Saturday draws**

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-6DB33F?style=flat-square&logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?style=flat-square&logo=springsecurity)
![MongoDB](https://img.shields.io/badge/MongoDB-4.2-47A248?style=flat-square&logo=mongodb)
![Redis](https://img.shields.io/badge/Redis-Cache-DC382D?style=flat-square&logo=redis)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)
![Tests](https://img.shields.io/badge/Tests-Unit%20%2B%20Integration-22c55e?style=flat-square)

A backend REST API for an online Lotto game. Players submit 6 numbers, receive a unique ticket ID, and check their results after the Saturday 12:00 draw. Winning numbers are fetched from an external HTTP service.

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Game Flow](#-game-flow)
- [API Endpoints](#-api-endpoints)
- [Getting Started](#-getting-started)
- [Configuration](#️-configuration)
- [Testing](#-testing)
- [Project Structure](#-project-structure)

---

## 🔍 Overview

LottoApp simulates a classic Polish Lotto game:

- **Player submits 6 numbers** (range 1–99, no duplicates) → receives a ticket with a unique ID and next draw date
- **Every Saturday at 12:00** — winning numbers are fetched from an external HTTP API and saved
- **At 11:55** — ResultChecker calculates winners by comparing all submitted tickets against winning numbers
- **After 12:00** — ResultAnnouncer makes results available via `GET /results/{ticketId}`
- **Redis caches** drawn result announcements to avoid recalculation

---

## 🏗 Architecture

The application is split into **4 independent domain modules**, each with its own Facade as the public API:

```
┌─────────────────────────────────────────────────────────────────┐
│                         Infrastructure                           │
│                                                                  │
│  POST /inputNumbers    GET /results/{id}    Schedulers (cron)   │
│         │                    │              │            │       │
│         ▼                    ▼              ▼            ▼       │
├─────────┼────────────────────┼─────────────┼────────────┼───────┤
│                           Domain                                 │
│                                                                  │
│  ┌──────────────┐   ┌────────────────┐   ┌──────────────────┐   │
│  │  Number      │   │  WinningNumbers│   │  ResultChecker   │   │
│  │  Receiver    │──▶│  Generator     │──▶│  Facade          │   │
│  │  Facade      │   │  Facade        │   │                  │   │
│  └──────────────┘   └────────────────┘   └────────┬─────────┘   │
│         │                    │                    │              │
│         │     (tickets)      │  (winning numbers) │  (results)   │
│         ▼                    ▼                    ▼              │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │              ResultAnnouncer Facade                      │    │
│  │         (checks timing, caches announcements)           │    │
│  └──────────────────────────────────────────────────────────┘    │
├──────────────────────────────────────────────────────────────────┤
│                    Data / External                               │
│                                                                  │
│   MongoDB (tickets, results)    Redis (announcements cache)     │
│   External HTTP API (winning numbers generator)                 │
└──────────────────────────────────────────────────────────────────┘
```

**Key design decisions:**
- Each domain knows only its own repository and exposes only a Facade — no cross-domain direct access
- `AdjustableClock` wraps `java.time.Clock` and allows time manipulation in integration tests
- `DrawDateProvider` interface is injected into `NumberReceiverFacade` — draw date logic is swappable
- `WinningNumbersGenerator` is an interface — the external HTTP implementation lives in infrastructure

### Draw Timeline (every week)

```
Friday / Mon–Fri         Saturday 12:00      Saturday 11:55
      │                       │                    │
      ▼                       ▼                    ▼
[Player submits      [WinningNumbers      [ResultChecker
 numbers →            Scheduler fires:     Scheduler fires:
 gets ticket ID]      fetch & save         calculate winners
                      winning numbers]     for all tickets]

                           │
                           ▼
                    Saturday 12:01+
                    [Results available
                     via GET /results/{id}]
```

---

## 🛠 Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.5 |
| Security | Spring Security + JWT (auth0 java-jwt 4.5.0) |
| Database | MongoDB |
| Cache | Redis |
| HTTP Client | RestClient (Spring 6.1) |
| Validation | Jakarta Bean Validation |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Testing | JUnit 5, Mockito, AssertJ, Testcontainers, WireMock, Awaitility |
| Build | Maven |
| Infrastructure | Docker Compose |

---

## 🎮 Game Flow

```
Player            API              External API         Scheduler
  │                │                    │                   │
  │ POST /register │                    │                   │
  │───────────────▶│                    │                   │
  │ 201 Created    │                    │                   │
  │◀───────────────│                    │                   │
  │                │                    │                   │
  │ POST /token    │                    │                   │
  │───────────────▶│                    │                   │
  │ 200 + JWT      │                    │                   │
  │◀───────────────│                    │    [Saturday 12:00]
  │                │                    │◀──────────────────│
  │                │                    │  GET /random?...  │
  │                │                    │──────────────────▶│
  │                │       Save winning numbers             │
  │                │                                        │
  │ POST /inputNumbers                  │  [Saturday 11:55] │
  │───────────────▶│                    │◀──────────────────│
  │ 200 + ticketId │                    │  calculateWinners │
  │◀───────────────│                    │                   │
  │                │                    │                   │
  │ GET /results/{ticketId}             │                   │
  │───────────────▶│                    │                   │
  │ 200 + result   │                    │                   │
  │◀───────────────│                    │                   │
```

---

## 📡 API Endpoints

### 🔓 Public

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/register` | Register a new player |
| `POST` | `/token` | Authenticate and receive JWT token |

### 🔐 Protected (requires `Authorization: Bearer <token>`)

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| `POST` | `/inputNumbers` | Submit 6 numbers for the next draw | `200 OK` / `400 BAD_REQUEST` |
| `GET` | `/results/{ticketId}` | Check result for a given ticket ID | `200 OK` / `404 NOT_FOUND` |

### 📄 Request / Response Examples

<details>
<summary><b>POST /register</b></summary>

```json
// Request
{
  "username": "jakub",
  "password": "securePassword123"
}

// Response 201 Created
{
  "id": "64a1b2c3d4e5f6a7b8c9d0e1",
  "username": "jakub",
  "isCreated": true
}
```
</details>

<details>
<summary><b>POST /inputNumbers</b></summary>

```json
// Request (Authorization: Bearer <token>)
{
  "inputNumbers": [7, 14, 21, 33, 45, 62]
}

// Response 200 OK — valid input
{
  "success": true,
  "ticket": {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "numbers": [7, 14, 21, 33, 45, 62],
    "drawDate": "2025-10-11T12:00:00"
  },
  "errors": []
}

// Response 400 BAD_REQUEST — validation failed
{
  "success": false,
  "ticket": null,
  "errors": ["Numbers must be in range 1-99"]
}
```
</details>

<details>
<summary><b>GET /results/{ticketId}</b></summary>

```json
// Response 200 OK — after draw, player won
{
  "resultDetailsDto": {
    "ticketId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "drawDate": "2025-10-11T12:00:00",
    "matchedNumbers": 4,
    "status": "WIN"
  },
  "message": "Congratulations, you've won and hit 4 numbers!"
}

// Response 200 OK — results still being calculated
{
  "resultDetailsDto": { ... },
  "message": "Results are being calculated, please come back later"
}

// Response 404 NOT_FOUND
{
  "message": "Result for id: abc123 was not found",
  "httpStatus": "NOT_FOUND"
}
```
</details>

### Validation Rules for `/inputNumbers`

| Rule | Detail |
|------|--------|
| Exactly 6 numbers | `NOT_ENOUGH_NUMBERS` / `TOO_MANY_NUMBERS` |
| Range 1–99 | `OUT_OF_RANGE` |
| No duplicates | Enforced by `Set<Integer>` conversion |
| Not null / not empty | Bean validation |

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker + Docker Compose

### 1. Clone the repository

```bash
git clone https://github.com/sizeeq/Lotto.git
cd Lotto
```

### 2. Start infrastructure (MongoDB + Redis)

```bash
docker-compose up -d
```

This starts:
- **MongoDB** on `localhost:27017`
- **Redis** on `localhost:6379`

### 3. Set environment variables

```bash
export JWT_SECRET=your-secret-key-minimum-32-characters
```

### 4. Run the application

```bash
mvn spring-boot:run
```

### 5. Explore the API

Open Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## ⚙️ Configuration

`src/main/resources/application.yml`:

```yaml
lotto:
  result-checker:
    config:
      scheduler-cron: "0 55 11 * * 6"       # Every Saturday at 11:55

  winning-numbers-generator:
    config:
      lowerBound: 1
      upperBound: 99
      requiredNumbers: 6
      generator-cron: "0 0 12 * * 6"        # Every Saturday at 12:00

  http:
    client:
      config:
        uri: http://external-numbers-api.com
        port: 9090
        connectionTimeout: 5000
        readTimeout: 5000
        random-number-service-path: /api/v1.0/random

  jwt:
    config:
      secret: ${JWT_SECRET}                  # set via environment variable
      issuer: Lotto Service
      expirationDays: 30
```

---

## 🧪 Testing

Three levels of tests, totalling 30+ test cases:

```
src/
├── test/                            # Unit tests (no Spring context)
│   └── domain/
│       ├── numberreceiver/
│       │   └── NumberReceiverFacadeTest.java     # 12 tests
│       ├── resultchecker/
│       │   └── ResultCheckerFacadeTest.java       # 6 tests
│       ├── resultannouncer/
│       │   └── ResultAnnouncerEntityFacadeTest.java  # 7 tests
│       └── winningnumbersgenerator/
│           └── WinningNumbersGeneratorFacadeTest.java
│
└── integration/                     # Integration tests (full context + containers)
    ├── feature/
    │   ├── UserPlayedAndWonIntegrationTest.java   # Full win scenario
    │   └── UserPlayedAndLostIntegrationTest.java  # Full lose scenario
    ├── apivalidationerror/
    │   └── ApiValidationFailedIntegrationTest.java
    └── http/winningnumbergenerator/
        └── ExternalWinningNumbersGeneratorErrorIntegrationTest.java
```

### Run all tests

```bash
mvn test
```

### Unit tests — AdjustableClock for time travel

Domain tests use `AdjustableClock` — a custom `Clock` implementation that allows advancing time in tests without `Thread.sleep()`:

```java
// Advance to Saturday 11:55 (5 minutes before draw)
clock.plusDaysAndMinutes(1, 115);

// Advance to Saturday 12:01 (1 minute after draw — results available)
clock.plusDaysAndMinutes(0, 6);
```

### Integration tests — Testcontainers + WireMock + Awaitility

The integration tests spin up a real MongoDB via Testcontainers and mock the external numbers API with WireMock. Awaitility handles asynchronous scheduler execution:

```java
// Wait up to 20 seconds for the scheduler to fetch winning numbers
await().atMost(Duration.ofSeconds(20))
       .until(() -> {
           try {
               WinningNumbersDto dto = winningNumbersGeneratorFacade.findWinningNumbersByDrawDate(drawDate);
               return dto.numbers().size() == 6;
           } catch (WinningNumbersNotFoundException e) {
               return false;
           }
       });
```

---

## 📁 Project Structure

```
src/
├── main/java/pl/lotto/
│   ├── domain/
│   │   ├── numberreceiver/
│   │   │   ├── NumberReceiverFacade.java     # submit numbers, find tickets
│   │   │   ├── NumberValidator.java          # range & count validation
│   │   │   ├── Ticket.java                   # domain entity
│   │   │   ├── DrawDateProvider.java         # port (interface)
│   │   │   ├── SaturdayDrawDateProvider.java # next Saturday 12:00
│   │   │   ├── TicketIdGenerator.java        # port (interface)
│   │   │   └── UUIDTicketIdGenerator.java
│   │   │
│   │   ├── winningnumbersgenerator/
│   │   │   ├── WinningNumbersGeneratorFacade.java
│   │   │   ├── WinningNumbers.java
│   │   │   ├── WinningNumbersGenerator.java  # port (interface)
│   │   │   └── WinningNumbersRepository.java # port (interface)
│   │   │
│   │   ├── resultchecker/
│   │   │   ├── ResultCheckerFacade.java      # compare tickets vs winning numbers
│   │   │   ├── ResultChecker.java            # core matching logic
│   │   │   ├── Result.java
│   │   │   └── ResultStatus.java             # WIN / LOSE
│   │   │
│   │   ├── resultannouncer/
│   │   │   ├── ResultAnnouncerFacade.java    # timing check + caching
│   │   │   ├── ResultAnnouncerEntity.java
│   │   │   └── AnnouncementMessage.java
│   │   │
│   │   └── user/
│   │       ├── UserFacade.java
│   │       ├── User.java
│   │       └── UserRole.java                 # USER / ADMIN
│   │
│   └── infrastructure/
│       ├── numberreceiver/controller/        # POST /inputNumbers
│       ├── resultannouncer/controller/       # GET /results/{id}
│       ├── winningnumbersgenerator/
│       │   ├── client/                       # ExternalWinningNumbersGenerator
│       │   └── scheduler/                    # Saturday 12:00 cron
│       ├── resultchecker/scheduler/          # Saturday 11:55 cron
│       ├── security/                         # JWT filter, authenticator
│       ├── user/controller/                  # POST /register
│       └── apivalidation/
│
└── test/ + integration/
    ├── AdjustableClock.java                  # time-travel helper
    ├── domain/numberreceiver/                # 12 unit tests
    ├── domain/resultchecker/                 # 6 unit tests
    ├── domain/resultannouncer/               # 7 unit tests
    └── feature/                              # end-to-end scenarios
```

---

## 📄 License

This project is for educational and portfolio purposes.

---

<div align="center">
  <sub>Built with ☕ by <a href="https://github.com/sizeeq">Jakub Makuch</a></sub>
</div>
