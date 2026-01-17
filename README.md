# Confiance user-service

## Overview
Part of the Confiance Financial Platform microservices architecture.

## Port
- **Default Port**: 8082

## Technology Stack
- Java 17
- Spring Boot 3.2.0
- Maven

## Building

```bash
mvn clean package
```

## Running Locally

```bash
mvn spring-boot:run
```

## Docker

```bash
docker build -t confiance/user-service:latest .
docker run -p 8082:8082 confiance/user-service:latest
```

## Environment Variables

See `application.yml` for required configuration.

## Documentation

- [Main Documentation](https://github.com/confiance-platform/confiance-platform)
- [API Documentation](../docs/API.md)

## License
Proprietary - Confiance Financial Platform
