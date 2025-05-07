# Infrastructure Monitoring System - Backend

This is the backend service for the **Infrastructure Monitoring System**, responsible for data management and communication with the frontend.

## Services

The backend consists of the following microservices:

- **Auth Service**: Manages user accounts and authentication.
- **Camera Service**: Manages cameras and camera-related data.
- **Infra Service**: Handles infrastructure objects such as guardrails, signs, and lamp posts.
- **Notification Service**: Sends notifications to users regarding detected faults or missing objects.
- **Report Service**: Generates reports on detected faults and infrastructure status.
- **Detection Service**: Serve models for real-time fault detections.

## Prerequisites

- **Java 21 or later**
- **Maven** for dependency management
- **Docker** for containerization and database setup
