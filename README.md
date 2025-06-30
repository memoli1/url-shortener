# URL Shortener – Code Factory Challenge

This is a production-ready MVP for a URL shortener service. 
It provides a simple REST API to shorten and resolve URLs.

---

##  Features

- Shorten any valid URL and retrieve a unique short identifier
- Resolve a short URL back to the original full URL
- Same URL returns the same short version
- Persistence with H2 in-memory DB (dev) and environment-specific DB (prod)
- Configurable short URL length via `application.properties`
- Separation of environments using Spring Profiles (`dev`, `prod`)
- Global exception handling for error responses

---

##  Tech Stack

- Java 21
- Spring Boot 3
- Spring Data JPA
- H2 (dev)
- JUnit 5 + MockMvc (unit & integration testing)
- Apache Commons Lang (for random string generation)

---

##  How to Run

```bash
  # Run locally with default (dev) profile
  ./gradlew bootRun
```

Change environment:

```bash
  SPRING_PROFILES_ACTIVE=prod 
  ./gradlew bootRun

```

---

##  API Documentation

### 1. POST `/api/urls`

**Request:**

```json
{
  "originalUrl": "https://www.google.com"
}
```

**Response:**

```json
{
  "shortUrl": "abc123"
}
```

---

###  2. GET `/api/urls?shortUrl=abc123`

**Response:**

```json
{
  "originalUrl": "https://www.google.com"
}
```
### Postman Collection

You can use the provided Postman collection to test the API:

`postman/url-shortener.postman_collection.json`

---

## Configuration

### `application.properties`

```properties
spring.profiles.active=dev
shorturl.length=6
```

You can override `shorturl.length` per environment (e.g. 8 in production).

---
## Possible Improvements

- **Validate URL format** before shortening

    Ensure that the submitted URL is valid and properly formatted before storing or processing it.
- **Handle duplicates for `shortUrl`** using DB-level unique constraint.

    In this MVP, I did not implement a mechanism to detect whether a generated short URL is already used. 

    The probability of collision is very low with a 6-character random alphanumeric string, but in a production system, I would add a UNIQUE constraint on the database and retry generation in case of conflict. This would ensure uniqueness even under high concurrency.

- **Add rate limiting** on `POST` to avoid abuse

  Prevent abuse by limiting how often a client can submit URLs within a time window.
  
- **Switch to a persistent database**
  
    The current setup uses an in-memory H2 database, which is ideal for development and testing. In production, a persistent and scalable option like PostgreSQL or MySQL should be used.

- **Introduce caching to speed up URL resolution**

    Adding a caching layer (e.g., Redis) for resolving short URLs could reduce database load and improve performance, especially for frequently accessed URLs.

- **Return full shortened URL (`https://shortenerUrl/abc123`) instead of just the short code**

    This can be easily configured via application properties or environment variable in a real deployment with a known domain.

---
##  Alternative Approaches Considered

When designing the URL shortening logic, I evaluated several strategies.

At first, I considered the classic two-step approach: saving the original URL to the database to obtain the auto-generated ID, and then generating the short URL from that ID (using Hashids). While this method works, it requires two database operations: one to insert the record and another to update it with the generated short URL. This introduces unnecessary performance overhead, especially under high load, and I decided to discard it early.

I also looked into deterministic hashing strategies like computing an MD5 or SHA-256 hash from the original URL. This has the advantage of being idempotent — the same URL always results in the same short string. However, this method comes with collision risks, especially when truncating hashes to keep URLs short. It also produces longer, less readable strings and doesn't offer flexibility in short URL length.

Another option was to base64-encode or base62-encode the database ID. While it guarantees uniqueness and shorter URLs compared to UUIDs, it still depends on saving to the database first to retrieve the ID, which brings us back to the double-write issue.

Some alternatives I considered included UUIDs or random-based libraries like nanoid. These ensure uniqueness and remove the need for collision checking, but they generate longer and less user-friendly short URLs, which I believe is not ideal for a user-facing service.

---

### Why I Chose My Solution

I chose to generate a random alphanumeric short string (with a configurable length via `application.properties`) and then check if it already exists in the database before saving. This approach avoids unnecessary database updates and ensures uniqueness with a retry mechanism in case of collisions.

It provides:

- **Single DB write** – high performance for shortening requests
- **Configurable length** – easily adjustable for different environments or use cases
- **Human-friendly URLs** – short, readable, and suitable for end users
- **Low collision risk** – especially with a length of 6+ characters

---

