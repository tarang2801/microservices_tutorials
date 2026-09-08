# Distributed Hotel Rating Platform

A production-oriented microservices application demonstrating
service discovery, centralized configuration, API gateway,
inter-service communication and fault-tolerant distributed systems.

Overview Architecture Diagram

CLIENT
  ↓
API GATEWAY (8085) routes based on path
  ↓
SERVICE REGISTRY (Eureka 8762) resolves service location
  ↓
MICROSERVICE (8080/8082/8083) loads config from Config Server (9999)
  ↓
SERVICE calls CONFIG SERVER → Git Repository
  ↓
DATABASE connection (PostgreSQL/MongoDB via docker-network)
  ↓
RESILIENCE LAYERS applied (if inter-service call):
  • Circuit Breaker checks state
  • Retry logic if failed
  • Rate Limiter throttles traffic
  ↓
RESPONSE propagates back through the same path
  ↓
CLIENT receives final response



Complete Distributed Hotel Rating Service - System Architecture Flow Diagram

╔══════════════════════════════════════════════════════════════════════════════════════════════╗
║                  DISTRIBUTED HOTEL RATING PLATFORM - END-TO-END FLOW                         ║
╚══════════════════════════════════════════════════════════════════════════════════════════════╝


                           ┌─────────────────────────────────┐
                           │      CLIENT REQUEST             │
                           │   (Browser/Mobile/API)          │
                           └────────────┬────────────────────┘
                                        │
                                        ▼
                     ┌──────────────────────────────────────┐
                     │     API GATEWAY (Port 8085)          │
                     │  Spring Cloud Gateway + Webflux      │
                     │  Load Balancing (lb://)              │
                     └────┬───────────────────┬──────────┬──┘
                          │                   │          │
        ┌─────────────────┼───────────────────┼──────────┼──────────────┐
        │                 │                   │          │              │
        ▼                 ▼                   ▼          ▼              ▼
   ┌─────────────┐  ┌──────────────┐  ┌──────────────┐  ▼      ┌─────────────────┐
   │ USER        │  │ HOTEL        │  │ RATING       │       │ SERVICE         │
   │ SERVICE     │  │ SERVICE      │  │ SERVICE      │       │ REGISTRY        │
   │ (8080)      │  │ (8082)       │  │ (8083)       │       │ (Eureka Port    │
   │             │  │              │  │              │       │ 8762)           │
   │ ─────────── │  │ ──────────── │  │ ──────────── │       │                 │
   │ Endpoints:  │  │ Endpoints:   │  │ Endpoints:   │       │ Registers &     │
   │ /user/*     │  │ /hotel/*     │  │ /rating/*    │       │ discovers       │
   │             │  │              │  │              │       │ all services    │
   └──────┬──────┘  └──────┬───────┘  └──────┬───────┘       │                 │
          │                │                  │               └────────────────┘
          │                │                  │                      ▲
          ▼                ▼                  ▼                      │
    ┌──────────────────────────────────────────────────────────────┐│
    │              SERVICE DISCOVERY & COMMUNICATION               ││
    │                                                               ││
    │  Each Service:                                               ││
    │  1. Registers with Eureka on startup                        ││
    │  2. Discovers other services from registry                  ││
    │  3. Uses service name for load-balanced calls               ││
    │     Example: lb://userservice, lb://hotelservice            ││
    └──────────────────────────────────────────────────────────────┘│
          │                │                  │                      │
          │                │                  └──────────────────────┘
          │                │
          │                └──────────────────┐
          │                                   │
          │        INTER-SERVICE CALLS        │
          │        (OpenFeign + Circuit      │
          │         Breaker in User Service)  │
          │                                   │
    ┌─────▼──────────────────────────────────▼─────┐
    │                                               │
    │   USER SERVICE CIRCUIT BREAKER PROTECTION    │
    │                                               │
    │  @CircuitBreaker(name="ratingHotelBreaker")  │
    │  @Retry(max-attempts: 5)                    │
    │  @RateLimiter(limit-for-period: 2)         │
    │                                               │
    │  Calls Hotel & Rating Services               │
    │  Fallback on failure                         │
    │                                               │
    └─────┬──────────────────────────────────┬─────┘
          │                                  │
          │ Resilience4j Config:             │
          │ • Failure Rate: 50%              │
          │ • Window Size: 10                │
          │ • Retry: 5s wait                 │
          │ • Half-Open: 3 permits           │
          │ • Open->Half-Open: 5s            │
          │                                  │
          ▼                                  ▼


╔════════════════════════════════════════════════════════════════════════════════╗
║                        DATABASE LAYER                                          ║
╚════════════════════════════════════════════════════════════════════════════════╝

┌────────────────────────────────────────────────────────────────────────────┐
│                          CONFIGURATION SERVER                              │
│                          (Port 9999)                                        │
│  ┌──────────────────────────────────────────────────────────────────────┐ │
│  │ Spring Cloud Config Server                                           │ │
│  │ Serves centralized configuration to all microservices               │ │
│  │                                                                      │ │
│  │ Git Repository: microserviceconfigserver                            │ │
│  │ Branch: main                                                        │ │
│  │ Search Paths: {application}, common                                │ │
│  │                                                                      │ │
│  │ Provides properties for:                                            │ │
│  │ • Database credentials & URLs                                       │ │
│  │ • Spring Data JPA/Mongo configs                                    │ │
│  │ • Actuator & Health endpoints                                      │ │
│  └──────────────────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────────────┘
                                    ▲
                                    │
                    spring.config.import: 
                 "optional:configserver:http://localhost:9999"
                                    │
                ┌───────────────────┼───────────────────┐
                │                   │                   │
                ▼                   ▼                   ▼
        ┌───────────────┐  ┌──────────────┐  ┌──────────────┐
        │ USER SERVICE  │  │ HOTEL        │  │ RATING       │
        │ Fetches:      │  │ SERVICE      │  │ SERVICE      │
        │ • DB URL      │  │ Fetches:     │  │ Fetches:     │
        │ • Credentials │  │ • DB URL     │  │ • DB URL     │
        │ • Circuit     │  │ • Creds      │  │ • Creds      │
        │   Breaker     │  │ • JPA config │  │ • Mongo cfg  │
        │   settings    │  │              │  │              │
        │ • Retry       │  │              │  │              │
        │   settings    │  │              │  │              │
        │ • Rate limit  │  │              │  │              │
        └───────────────┘  └──────────────┘  └──────────────┘
                │                   │                   │
                ▼                   ▼                   ▼


╔════════════════════════════════════════════════════════════════════════════════╗
║                    DATABASE CONNECTIONS (Docker)                               ║
╚════════════════════════════════════════════════════════════════════════════════╝

       ┌──────────────────────────────────────────────────────────┐
       │         Docker Network: microservice-network             │
       │                                                          │
       │  ┌────────────────────────────────────────────────────┐ │
       │  │  PostgreSQL (Port 5437)                            │ │
       │  │  ─────────────────────────────────────            │ │
       │  │  Container: postgres-microservices                 │ │
       │  │  Database: microservice                            │ │
       │  │  User: microservice_user                           │ │
       │  │  Password: microservice_password                   │ │
       │  │                                                    │ │
       │  │  Used by:                                          │ │
       │  │  • USER SERVICE (Spring Data JPA)                 │ │
       │  │    └─ JPA/Hibernate ORM                           │ │
       │  │    └─ driver-class: org.postgresql.Driver         │ │
       │  │                                                    │ │
       │  │  • HOTEL SERVICE (Spring Data JPA)                │ │
       │  │    └─ JPA/Hibernate ORM                           │ │
       │  │    └─ driver-class: org.postgresql.Driver         │ │
       │  │                                                    │ │
       │  │  Connection URL:                                   │ │
       │  │  jdbc:postgresql://postgres-microservice:5432     │ │
       │  │  /microservice                                     │ │
       │  │                                                    │ │
       │  │  Health Check: pg_isready (every 10s)             │ │
       │  └────────────────────────────────────────────────────┘ │
       │                                                          │
       │  [MongoDB Configuration Commented Out]                   │
       │  • Would be used by RATING SERVICE                       │
       │  • Port would be 27017                                   │
       │  • Currently config moved to Config Server              │
       │                                                          │
       └──────────────────────────────────────────────────────────┘
                    │                        │
         ┌──────────┴────────────┬───────────┴─────────────┐
         │                       │                         │
         ▼                       ▼                         ▼
    ┌─────────────┐      ┌──────────────┐      ┌──────────────┐
    │ USER        │      │ HOTEL        │      │ RATING       │
    │ SERVICE     │      │ SERVICE      │      │ SERVICE      │
    │ (Port 8080) │      │ (Port 8082)  │      │ (Port 8083)  │
    │             │      │              │      │              │
    │ DB Type:    │      │ DB Type:     │      │ DB Type:     │
    │ PostgreSQL  │      │ PostgreSQL   │      │ MongoDB*     │
    │             │      │              │      │              │
    │ ORM:        │      │ ORM:         │      │ Mapping:     │
    │ Hibernate   │      │ Hibernate    │      │ Spring Data  │
    │             │      │              │      │ Mongo        │
    │ Config:     │      │ Config:      │      │              │
    │ • From      │      │ • From       │      │ Config:      │
    │   Config    │      │   Config     │      │ • From Config│
    │   Server    │      │   Server     │      │   Server     │
    └─────────────┘      └──────────────┘      └──────────────┘


╔════════════════════════════════════════════════════════════════════════════════╗
║                  COMPLETE REQUEST-RESPONSE FLOW                                ║
╚════════════════════════════════════════════════════════════════════════════════╝

1. CLIENT REQUEST
   └─> http://localhost:8085/userservice/api/users/123

2. API GATEWAY
   ├─> Receives request on port 8085
   ├─> Matches route predicate: Path=/userservice/**
   ├─> Resolves URI: lb://userservice (load balanced)
   └─> Discovers USER SERVICE from Service Registry (Eureka)

3. SERVICE REGISTRY LOOKUP
   ├─> Service Registry checks registered instances
   ├─> Finds USER SERVICE at http://localhost:8080
   └─> Returns service URL to API Gateway

4. API GATEWAY ROUTES TO USER SERVICE
   └─> Forwards request to http://localhost:8080/userservice/api/users/123

5. USER SERVICE RECEIVES REQUEST
   ├─> Loads configuration from Config Server
   │   └─> http://localhost:9999/userservice/main
   ├─> Gets properties:
   │   ├─> PostgreSQL connection details
   │   ├─> Circuit Breaker settings
   │   ├─> Retry & Rate Limiter config
   │   └─> Spring Data JPA configuration
   └─> Processes request in business logic

6. IF USER SERVICE NEEDS TO CALL OTHER SERVICES
   ├─> OpenFeign Client prepares inter-service call
   │
   ├─> Circuit Breaker Checks State:
   │   ├─ CLOSED: Request proceeds → 
   │   │   ├─ Retry Handler checks (max-attempts: 5)
   │   │   ├─ Rate Limiter: limits to 2 requests/4s
   │   │   ├─ TimeLimiter timeout applied
   │   │   └─ Service Discovery: lb://hotelservice
   │   │       └─ Calls Hotel Service at http://localhost:8082
   │   │
   │   ├─ OPEN: Circuit Breaker TRIPS →
   │   │   └─ Fallback Method Called
   │   │       └─ Returns cached response or default value
   │   │
   │   └─ HALF_OPEN: Test request allowed →
   │       ├─ Limited permits: 3
   │       ├─ Single request attempted
   │       └─ Result determines next state (CLOSED/OPEN)
   │
   └─> Called Service (Hotel/Rating) also:
       ├─ Loads config from Config Server
       ├─ Registers with Service Registry
       ├─ Connects to PostgreSQL/MongoDB database
       └─> Returns response

7. DATABASE OPERATIONS
   ├─ USER SERVICE → PostgreSQL
   │  ├─> Hibernate ORM translates to SQL
   │  ├─> Connection URL: jdbc:postgresql://postgres-microservice:5432/microservice
   │  ├─> User: microservice_user
   │  └─> Executes queries
   │
   ├─ HOTEL SERVICE → PostgreSQL
   │  ├─> Hibernate ORM translates to SQL
   │  └─> Shares same PostgreSQL instance
   │
   └─ RATING SERVICE → MongoDB (if enabled)
      ├─> Spring Data MongoDB
      └─> URI: mongodb://localhost:27017/microservice

8. RESPONSE PROPAGATION
   ├─ Service returns response to API Gateway
   ├─ API Gateway routes response back to client
   └─ Response reaches client


╔════════════════════════════════════════════════════════════════════════════════╗
║                   CONFIGURATION HIERARCHY                                      ║
╚════════════════════════════════════════════════════════════════════════════════╝

                    ┌─────────────────────────┐
                    │   Config Server Git     │
                    │  (microserviceconfigserver)
                    │   Branch: main          │
                    │   Paths: {application}, │
                    │          common         │
                    └────────────┬────────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                  │
              ▼                  ▼                  ▼
        ┌───────────────┐  ┌───────────┐  ┌─────────────────┐
        │ userservice   │  │ hotel     │  │ rating          │
        │ /main folder  │  │ service   │  │ /main folder    │
        │               │  │ /main     │  │                 │
        │ Contains:     │  │ folder    │  │ Contains:       │
        │ • userservice │  │           │  │ • ratingservice │
        │   -dev.yml    │  │ Contains: │  │   -dev.yml      │
        │ • userservice │  │ • hotel   │  │ • ratingservice │
        │   -prod.yml   │  │   service │  │   -prod.yml     │
        │ • common      │  │   -dev    │  │ • common        │
        │   properties  │  │   -prod   │  │   properties    │
        │               │  │ • common  │  │                 │
        └───────────────┘  └───────────┘  └─────────────────┘
              │                  │                  │
              └──────────────────┼──────────────────┘
                                 │
                    ┌────────────┴─────────────┐
                    │                          │
                    ▼                          ▼
        ┌──────────────────────┐  ┌──────────────────────┐
        │ Local application.yml │  │ application.properties
        │ (Service defaults)    │  │ (Empty - for testing)
        │                       │  │
        │ Priority: LOWEST      │  │
        └───────────────────────┘  └──────────────────────┘
        
        CONFIG LOAD ORDER (High to Low Priority):
        1. Environment Variables
        2. Command-line Arguments
        3. application.properties (local)
        4. application.yml (local)
        5. Config Server (from Git)
        6. Defaults in code


╔════════════════════════════════════════════════════════════════════════════════╗
║                    ACTUATOR & MONITORING                                       ║
╚════════════════════════════════════════════════════════════════════════════════╝

USER SERVICE Actuator Endpoints (Port 8080):
├─ /userservice/actuator/health
│  └─ Shows: UP/DOWN + Circuit Breaker status
│
├─ /userservice/actuator/metrics
│  └─ Shows: Application metrics
│
└─ /userservice/actuator/health/circuitbreakers
   └─ Shows: ratingHotelBreaker state (CLOSED/OPEN/HALF_OPEN)

Management Config:
├─ health.circuitbreakers.enabled: true
├─ health.show-details: ALWAYS
└─ endpoints.web.exposure.include: health, metrics, prometheus


╔════════════════════════════════════════════════════════════════════════════════╗
║                    RESILIENCE PATTERN SUMMARY                                 ║
╚════════════════════════════════════════════════════════════════════════════════╝

Circuit Breaker (ratingHotelBreaker):
├─ Failure Rate Threshold: 50%
├─ Minimum Calls: 5 (before deciding)
├─ Window Size: 10 (last 10 calls)
├─ Sliding Window Type: COUNT_BASED
├─ Wait Duration (Open→Half-Open): 5 seconds
├─ Half-Open Permits: 3 calls
├─ Automatic Transition Enabled: true
└─ Health Indicator Registered: true

Retry (ratingHotelService):
├─ Max Attempts: 5
├─ Wait Duration: 5 seconds between retries
└─ Retry on: Exceptions (configured in code)

Rate Limiter (ratingHotelRateLimiter):
├─ Limit for Period: 2 requests
├─ Limit Refresh Period: 4 seconds
├─ Timeout Duration: 0 seconds
└─ Max: 2 requests per 4 seconds

Event Buffer:
└─ Size: 10 (last 10 circuit breaker events captured)


╔════════════════════════════════════════════════════════════════════════════════╗
║                    KEY TECHNOLOGIES STACK                                      ║
╚════════════════════════════════════════════════════════════════════════════════╝

CORE:
├─ Java 17
├─ Spring Boot 4.0.7
└─ Spring Cloud 2025.1.2

SERVICE DISCOVERY & CONFIGURATION:
├─ Netflix Eureka (Service Registry)
├─ Spring Cloud Config Server (Centralized Config)
└─ Git Repository (Config Source)

RESILIENCE & FAULT TOLERANCE:
├─ Resilience4j 2.4.0 (Circuit Breaker, Retry, Rate Limiter)
├─ Spring Boot Starter Actuator (Health/Monitoring)
└─ Spring Boot Starter AspectJ (AOP for annotations)

INTER-SERVICE COMMUNICATION:
├─ Spring Cloud OpenFeign (Declarative HTTP client)
└─ Spring Cloud Gateway with WebFlux (API Gateway)

DATA PERSISTENCE:
├─ PostgreSQL 15 (User & Hotel Services) with Spring Data JPA/Hibernate
├─ MongoDB 6.0 (Rating Service - config ready) with Spring Data MongoDB
└─ MySQL Connector (User Service compatibility)

DEPLOYMENT:
└─ Docker Compose (Container orchestration)

Inter-Service Communication with Circuit Breaker

┌────────────────────────────────────────────────────────────────┐
│           DISTRIBUTED SERVICE COMMUNICATION FLOW               │
└────────────────────────────────────────────────────────────────┘

API GATEWAY
    │
    ├─────────────► USER SERVICE
    │                    │
    │              [Circuit Breaker]
    │                    │
    │    ┌───────────────┼───────────────┐
    │    │               │               │
    │    ▼               ▼               ▼
    │  HOTEL        RATING          DATABASE
    │  SERVICE      SERVICE         (MySQL/PostgreSQL/MongoDB)
    │    │               │               │
    │    └───────────────┼───────────────┘
    │                    │
    │            (Resilience4j
    │             Protected)
    │                    │
    │    ┌───────────────────────────┐
    │    │  FALLBACK STRATEGY         │
    │    │  - Return cached data      │
    │    │  - Return default response │
    │    │  - Log error               │
    │    └───────────────────────────┘
    │
    └──► Response to Client



1. Architecture
2. Microservices
3. Technology Stack
4. Service Communication
5. Resilience Patterns
6. Database Design
7. Docker Setup
8. API Documentation
9. Failure Scenarios
10. How to Run
