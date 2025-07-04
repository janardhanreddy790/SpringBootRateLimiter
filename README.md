# 🚀 Spring Boot Rate Limiter (Microservices with Redis)

A complete Spring Boot microservices project using **Spring Cloud Gateway** and **Redis** for API rate limiting. Designed to throttle requests per IP and path, this project showcases how to implement a scalable and resilient gateway with a custom KeyResolver.

---

## 🧱 Project Structure

```
SpringBootRateLimiter/
├── pom.xml                  # Parent POM
├── api-gateway/             # Spring Cloud Gateway (Reactive)
│   └── pom.xml
├── service-hello/           # Microservice for /api/hello
│   └── pom.xml
└── README.md
```

---

## ⚙️ Technologies Used

- Java 17
- Spring Boot 3.2.4
- Spring Cloud 2023.0.1
- Spring Cloud Gateway
- Redis (Reactive)
- Maven

---

## 🛠️ How to Set Up & Run

### 1. Start Redis Server

Install Redis locally or run it via Docker:

```bash
redis-server
```
Or with Docker:

```bash
docker run --name redis -p 6379:6379 redis
```

### 2. Run the Services

```bash
cd service-hello
mvn spring-boot:run
# in new terminal
cd ../api-gateway
mvn spring-boot:run
```

---

## 📦 API Gateway Configuration (`application.yml`)

```yaml
spring:
  application:
    name: api-gateway
  redis:
    host: localhost
    port: 6379
  cloud:
    gateway:
      default-filters:
        - name: RequestRateLimiter
          args:
            redis-rate-limiter.replenishRate: 2
            redis-rate-limiter.burstCapacity: 3
            key-resolver: "#{@customKeyResolver}"
      routes:
        - id: service-hello
          uri: http://localhost:8081
          predicates:
            - Path=/api/hello
  main:
    allow-bean-definition-overriding: true

logging:
  level:
    org.springframework.cloud.gateway.filter.ratelimit: DEBUG
```

---

## 🧠 What Each Rate Limiter Setting Means

| Property                      | Description                                              |
|------------------------------|----------------------------------------------------------|
| `replenishRate`              | Number of tokens added per second                        |
| `burstCapacity`              | Maximum tokens a client can use in one burst             |
| `key-resolver`               | Spring bean used to generate unique key for each client  |
| `#{@customKeyResolver}`      | Resolves the key as `IP:Path` using custom logic         |

---

## 🔑 Custom KeyResolver (`CustomKeyResolver.java`)

```java
@Bean
public KeyResolver customKeyResolver() {
    return exchange -> {
        String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        String path = exchange.getRequest().getPath().toString();
        return Mono.just(ip + ":" + path);
    };
}
```

This ensures rate limiting is enforced **per IP and per endpoint**.

---

## ✅ How to Test

```bash
for i in {1..10}; do
  curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/hello
done
```

### Expected Output:
- First 2–3 requests: `200`
- Remaining: `429 Too Many Requests`

---

## 🧪 Redis Monitoring (Optional)

Run this in another terminal to monitor Redis:

```bash
redis-cli monitor
```

Look for:
```
"INCR" "request_rate_limiter.{ip:path}.tokens"
```

---

## 🧰 Required Dependencies (api-gateway `pom.xml`)

```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
  </dependency>
</dependencies>
```

## 📌 Parent POM Versions (`parent/pom.xml`)

```xml
<properties>
  <java.version>17</java.version>
  <spring.boot.version>3.2.4</spring.boot.version>
  <spring.cloud.version>2023.0.1</spring.cloud.version>
</properties>

<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-dependencies</artifactId>
      <version>${spring.boot.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-dependencies</artifactId>
      <version>${spring.cloud.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

---

## 🧭 Why Use a Rate Limiter?

- 🔐 **Prevent Abuse** – block IPs from spamming endpoints
- ⚙️ **Protect Microservices** – control load at gateway level
- 💰 **Reduce Cost** – avoid scaling unnecessarily under fake traffic
- 📊 **Monitor Usage** – collect analytics via Redis and Prometheus

---

## 📩 Questions or Improvements?

Feel free to extend this project to support:
- API Key based rate limiting
- User-based plans (free, premium)
- Dashboards with metrics

---

Enjoy building resilient APIs 🚀