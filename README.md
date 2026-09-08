# Distributed Hotel Rating Platform

A production-oriented microservices application demonstrating
service discovery, centralized configuration, API gateway,
inter-service communication and fault-tolerant distributed systems.

                    API Gateway
                         |
       +-----------------+-----------------+
       |                 |                 |
       v                 v                 v
   User Service     Hotel Service    Rating Service
       |                 |                 |
   PostgreSQL         MongoDB          MongoDB
       
              Service Registry
                    |
              Config Server


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
