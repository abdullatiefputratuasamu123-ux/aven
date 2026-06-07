---
name: spring-boot-h2-local-fallback
description: How to configure a Spring Boot project to run locally using an in-memory H2 database when the primary database is not available.
source: auto-skill
extracted_at: '2026-06-07T04:31:58.324Z'
---

# Spring Boot H2 Local Fallback

When you need to run a Spring Boot application locally but the primary database (e.g., PostgreSQL, MySQL) is not installed or available on the host machine, you can quickly fall back to an in-memory H2 database to proceed with testing or development.

## Steps to Implement

### 1. Add H2 Dependency
Add the H2 database dependency to your `pom.xml` (or `build.gradle`) if it isn't already present.
```xml
<!-- H2 for local development -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 2. Create a Local Properties File
Create an `application-local.properties` (or `application-local.yml`) file in `src/main/resources` to override the database settings. 

Key configurations to include:
- Set the H2 URL. If emulating another DB, use the mode parameter (e.g., `MODE=PostgreSQL` or `MODE=MySQL`).
- Enable the H2 console for easy debugging.
- Use `create-drop` or `update` for `spring.jpa.hibernate.ddl-auto` so Hibernate generates the schema automatically.
- **Important:** Disable database migration tools like Flyway or Liquibase (`spring.flyway.enabled=false`), as their migration scripts are often written in database-specific SQL that H2 might not understand.

Example `application-local.properties`:
```properties
# H2 In-Memory Database for Local Development
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA/Hibernate Config - H2
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Disable Flyway for local H2 development to avoid dialect mismatch errors
spring.flyway.enabled=false
```

### 3. Build and Run
Build the application, bypassing tests if they depend on the primary database:
```bash
mvn clean package -DskipTests
```

Run the application JAR, specifying the local configuration file:
```bash
java -jar target/app-1.0.0.jar --spring.config.location=classpath:/application-local.properties
```

Alternatively, if using profiles, you can set the active profile:
```bash
java -jar target/app-1.0.0.jar --spring.profiles.active=local
```

### Notes
- Make sure to check if there is any application logic or `@Query` annotations using database-specific functions that H2's compatibility mode might not fully support.
- If the application relies on seed data, ensure that mechanisms like `DataInitializer` (CommandLineRunner) or `data.sql` are compatible and will execute to populate the in-memory database on startup.