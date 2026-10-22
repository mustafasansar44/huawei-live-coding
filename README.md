### Quick Start

* **Docker Build:** `docker build -t olympic-ranking .`
* **Docker Run:** `docker run -p 8080:8080 olympic-ranking`
* **Swagger URL:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)



# 2024 Paris Olympics Medal Ranking API — Live Coding Challenge

## Duration: 90 minutes

## Tech Stack

| Item | Detail |
|------|--------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 4.1.1 |
| **Build Tool** | Maven |
| **Database** | H2 (in-memory) |

---

## Project Setup

Start your project at https://start.spring.io/. Use the following settings:

| Field | Value |
|-------|-------|
| **Project** | Maven |
| **Language** | Java |
| **Spring Boot** | 4.1.1 |
| **Group** | `com.huawei` |
| **Artifact** | `olympic-ranking` |
| **Name** | `olympic-ranking` |
| **Package name** | `com.huawei.olympicranking` |
| **Packaging** | Jar |
| **Java** | 17 |

### Dependencies (Add on Spring Initializr)

- Spring Web
- Spring Data JPA
- H2 Database
- Validation

### Maven Configuration

| Config | Value |
|--------|-------|
| **User Settings File** | `D:\maven\dany-user-settings-file.xml` |
| **Local Repository** | `D:\maven\olympic-ranking` |

### Build Command

```bash
mvn install -Dmaven.test.skip=true \
  -gs /usr1/maven/settings/cbu-maven-user-settings.xml \
  -s /usr1/maven/settings/cbu-maven-user-settings.xml
```

---

## Problem Definition

You are asked to develop a **medal tracking REST API** for the 2024 Paris Olympics. The system will record medals won by athletes and provide rankings by country, athlete, and sport.

---

## Domain Model

Your system needs to store the following information:

- **Countries:** Country code (e.g., TUR, USA) and country name
- **Sports:** Sport name (e.g., Archery, Swimming)
- **Athletes:** Athlete name and which country they represent
- **Medal Records:** Which athlete won which medal type, in which sport, representing which country
- **Medal Types:** Gold (GOLD), Silver (SILVER), Bronze (BRONZE)

> Design the entities, their relationships, and table structure yourself. Decide which entities have Many-to-One, One-to-Many, or Many-to-Many relationships on your own.

---

## REST API Endpoints

You need to design the following endpoints:

### 1. Data Entry (CRUD)

- Create a country (POST)
- Create a sport (POST)
- Create an athlete (POST)



- Record a medal (POST)

### 2. Ranking Endpoints

- Athlete ranking (GET)
- Sport ranking (GET)
- Country ranking (GET)

> URL paths, request/response body structures, and HTTP status codes are up to your design.

---

## Medal Ranking Rules

### Country Ranking (Official Olympic Rules)

**Priority is on Gold Medals:**

1. **Gold Medal Priority** — The country with the most gold medals ranks highest, regardless of total medal count.
2. **Silver Medal Breaks the Tie** — If two or more countries have equal gold medals, the one with more silver medals ranks higher.
3. **Bronze Medal is the Final Decider** — If gold and silver medal counts are also equal, ranking is determined by bronze medal count.
4. **Total Medal Equality** — If all medal counts (gold, silver, bronze) are equal, countries are ranked alphabetically by name.

> **Example:**
>
> | Rank | Country | Gold | Silver | Bronze |
> |------|---------|------|--------|--------|
> | 1 | USA | 40 | 44 | 41 |
> | 2 | CHN | 40 | 27 | 24 |
> | 3 | JPN | 20 | 12 | 13 |
>
> USA and CHN have equal gold (40), but USA has more silver (44 > 27) → USA ranks first.

### Athlete Ranking

1. **Total medal count** (descending)
2. **Gold medal count** (descending)
3. **Silver medal count** (descending)
4. **Bronze medal count** (descending)
5. **Athlete name** (alphabetical)

> **Example:**
>
> | Rank | Athlete | Total | Gold | Silver | Bronze |
> |------|---------|-------|------|--------|--------|
> | 1 | Athlete A | 5 | 2 | 2 | 1 |
> | 2 | Athlete B | 5 | 2 | 1 | 2 |
> | 3 | Athlete C | 4 | 3 | 1 | 0 |
>
> A and B have equal totals (5), equal gold (2) → silver: A (2) > B (1) → A ranks first.
> C has fewer total medals (4), so ranks below A and B despite having more gold.

### Sport Ranking

1. **Total medal count** (descending) — all medals awarded in that sport
2. **Sport name** (alphabetical)

---

## Persistence

- Use **H2 in-memory database**
- Create entities and repositories using **Spring Data JPA**
- Flyway/Liquibase is not required — `spring.jpa.hibernate.ddl-auto=update` is sufficient

### application.properties

```properties
spring.application.name=olympic-ranking
spring.datasource.url=jdbc:h2:mem:olympics
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

---

## Validation

- Use Bean Validation for all required fields (`@NotBlank`, `@NotNull`, `@Positive`)
- Validate request bodies with `@Valid` annotation on `POST` endpoints
- Return appropriate HTTP status codes for invalid input (`400 Bad Request`)

---

## Exception Handling

Create a global exception handler (`@RestControllerAdvice`):

| Exception | HTTP Status | Description |
|-----------|-------------|-------------|
| `ResourceNotFoundException` | 404 | Non-existent resource (athlete, country, sport) |
| `ValidationException` | 400 | Invalid input (`@Valid` failure) |
| `DuplicateMedalException` | 409 | Same athlete + same sport + same medal type already exists |
| `Exception` (fallback) | 500 | Unexpected errors |

### Error Response Format

```json
{
  "timestamp": "2024-08-15T10:30:00",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Athlete not found with id: 99",
  "path": "/api/medals"
}
```

---

## Testing

Write at least **3 test cases** of the following types:

### Unit Test (Service Layer — Mockito)

```java
@ExtendWith(MockitoExtension.class)
class MedalServiceTest {
    @Mock
    private MedalRepository medalRepository;
    @InjectMocks
    private MedalService medalService;

    @Test
    void shouldSaveMedal_WhenValidInput() { ... }

    @Test
    void shouldThrowException_WhenDuplicateMedal() { ... }
}
```

### Integration Test (Spring Boot + H2)

```java
@SpringBootTest
@AutoConfigureTestDatabase
class CountryRankingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnCountriesRankedByGoldFirst() throws Exception {
        // Given: USA (2G, 1S), TUR (1G, 3S)
        // When: GET /api/rankings/countries
        // Then: USA is #1 (more gold), TUR is #2
    }
}
```

---

## Sample Data (For Testing)

The following data can be used for testing:

```
Countries:
  TUR - Türkiye
  USA - United States
  JPN - Japan

Sports:
  Archery
  Swimming
  Judo

Athletes:
  1 - Mete Gazoz (TUR)
  2 - Noah Lyles (USA)
  3 - Abby Steuter (USA)
  4 - Hifumi Abe (JPN)

Medals:
  Mete Gazoz    - Archery  - GOLD   (TUR)
  Mete Gazoz    - Archery  - SILVER (TUR)
  Noah Lyles    - Swimming - GOLD   (USA)
  Noah Lyles    - Swimming - BRONZE (USA)
  Hifumi Abe    - Judo     - GOLD   (JPN)
```

### Expected Country Ranking

```
1. TUR - Gold: 1, Silver: 1, Bronze: 0
2. USA - Gold: 1, Silver: 0, Bronze: 1
3. JPN - Gold: 1, Silver: 0, Bronze: 0
```

> All three countries have equal gold (1). TUR has silver (1), others don't → TUR ranks first.
> USA and JPN have equal silver (0). USA has bronze (1), JPN doesn't (0) → USA 2nd, JPN 3rd.

---

## Bonus Tasks (For Candidates Who Finish Early)

If you complete the core requirements, you may work on the following bonus tasks:

### Bonus 1: Weighted Scoring Strategy

Add a **weighted scoring** system as an alternative to the official Olympic rules:

```
Gold = 3 points
Silver = 2 points
Bronze = 1 point
```

New endpoint:
```
GET /api/rankings/countries?strategy=WEIGHTED
GET /api/rankings/countries?strategy=OFFICIAL (default)
```

### Bonus 2: Top-N Query

```
GET /api/rankings/athletes?top=10
```

Return the top 10 athletes by medal count. Using `PriorityQueue` (min-heap) for O(n log k) solution is recommended.

### Bonus 3: Real-time Rank Update

Instead of full re-sort when a medal is added, implement **incremental update** of the ranking.

### Bonus 4: Redis Caching

Cache ranking results with Redis (`@Cacheable`, `@CacheEvict`).

### Bonus 5: API Documentation

Add Swagger/OpenAPI documentation (`springdoc-openapi-starter-webmvc-ui`).

---

> **Note:** While reading the requirements, feel free to ask questions if anything is unclear or ambiguous. Prioritize your work and manage the 90-minute time wisely.
