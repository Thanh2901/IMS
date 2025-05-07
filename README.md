# Infrastructure Monitoring System

Welcome to the **Infrastructure Monitoring System**! This project is tailored for managing cameras and infrastructure objects on highways. It empowers administrators to monitor the condition of infrastructure, detect faults or lost objects, and notify users in real-time. The system also provides tools for generating detailed reports to ensure timely maintenance and issue resolution.

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Technology Stack](#technology-stack)
4. [Backend Documentation](#backend-documentation)
5. [Frontend Documentation](#frontend-documentation)

## Overview

The **Infrastructure Monitoring System** is a comprehensive solution designed to enhance highway management by integrating real-time monitoring and fault detection capabilities. It leverages advanced technologies to monitor cameras and infrastructure objects, ensuring the early detection of issues such as damaged guardrails, missing signs, or debris on the road. 

Key objectives include:
- Detecting faults or lost objects efficiently.
- Notifying users promptly about critical incidents.
- Providing a robust reporting system for maintenance and analysis.

The system is equipped with a scalable architecture, user-friendly interface, and secure communication channels to ensure reliable operation and seamless user experience.

## Features

- Real-time monitoring of infrastructure components.
- User authentication and role-based access control (RBAC).
- Service discovery and API gateway for efficient microservices communication.
- Centralized state management and responsive UI design.
- Dockerized setup for easy development.

## Technology Stack

- **Spring Boot**: Core framework for the backend services.
- **Spring Cloud Gateway**: API Gateway for routing and load balancing.
- **Spring Security + JWT**: Authentication and authorization.
- **Spring Data JPA**: ORM for database interactions.
- **PostgreSQL**: Relational database for data storage.
- **Eureka**: Service discovery for microservices architecture.
- **React**: JavaScript library for building user interfaces.
- **Redux**: State management for consistent data flow.
- **Chakra UI**: Component library for responsive and accessible UI design.
- **TypeScript**: Strongly-typed programming language for enhanced code quality and maintainability.
- **Docker**: for database setup and containerization.

## Backend Documentation

Detailed instructions for backend setup and configuration are available [here](backend/README.md).

## Frontend Documentation

Detailed instructions for backend setup and configuration are available [here](frontend/README.md).

## License
The Infrastructure Mornitoring System is licensed under the terms of the MIT license.
