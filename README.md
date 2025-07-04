
# Spring Boot Rate Limiter (Microservices with Redis & Gateway)

A complete Spring Boot microservices project with:

- API Gateway
- Redis Rate Limiting per IP + API
- Hello Service (sample microservice)

## 🧱 Modules

1. `api-gateway` — Spring Cloud Gateway with Redis-based rate limiter
2. `service-hello` — Simple REST service to return a response

## 🚀 Setup

### Prerequisites

- Java 17+
- Maven
- Redis on localhost:6379

### Running the app

In separate terminals:

```
cd service-hello
mvn spring-boot:run
```

```
cd api-gateway
mvn spring-boot:run
```

### Test Rate Limiting

```
for i in {1..60}; do curl -s -o /dev/null -w "%{http_code}
" http://localhost:8080/api/hello; done
```

→ First 50 requests = `200 OK`, after that = `429 Too Many Requests`.

## 💡 Why Redis Rate Limiting?

To:
- Prevent abuse and overload
- Enforce fair use
- Improve performance with reactive Redis support

---
Developed with Spring Boot 3.2.4 and Spring Cloud 2023.0.1
