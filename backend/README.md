# AI Prompt Management Platform - Backend

## Description
The backend for the **AI Prompt Management Platform** is a robust RESTful API built with Java and Spring Boot. It serves as the core engine for managing AI prompts, facilitating AI executions via the Groq API, and handling data persistence with PostgreSQL. This module manages prompt lifecycles, versions, executions, test results, and user feedback securely.

## Technologies Used
* **Java** (17 or later)
* **Spring Boot** (Core framework)
* **Spring Data JPA** (ORM and database interactions)
* **PostgreSQL** (Relational database)
* **Maven** (Dependency management and build tool)
* **Spring AI / Groq API** (AI service integration)

## Features
* **Prompt creation with metadata**: Store prompts with context, tags, and specific parameters.
* **Prompt versioning**: Keep track of historical changes and iterate on prompts safely.
* **Prompt execution using AI**: Dynamically test prompts using integrated Groq AI models.
* **Test result storage**: Persist AI responses and evaluations for future reference.
* **Feedback tracking**: Log user feedback to continuously improve prompt quality.
* **Search with pagination**: Efficiently retrieve prompts using keyword search and page-based loading.

## Project Structure
The backend follows a standard layered architecture:

* `controller/` - REST API endpoints handling incoming HTTP requests.
* `service/` - Core business logic, integrating AI responses and managing entities.
* `repository/` - Spring Data JPA interfaces for database operations.
* `model/` - JPA entity classes representing database tables.
* `dto/` - Data Transfer Objects (DTOs) for structured request/response payload handling.
* `config/` - Configuration classes for security, CORS, AI API keys, etc.
* `exception/` - Centralized error handling and custom exceptions for clean API responses.

## How to Run the Backend

### Prerequisites
* Java 17+
* Maven 3.8+
* PostgreSQL running locally or remotely

### Setup
1. **Configure Database & AI Key**: Open `src/main/resources/application.yml` and provide your PostgreSQL credentials and Groq API key:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/your_db_name
       username: your_db_username
       password: your_db_password
   
   spring.ai.groq:
     api-key: your_groq_api_key

2. Build the Project: Run the following Maven command to resolve dependencies and build the application:
##### mvn clean install

3. Run the Application: Start the Spring Boot server
###### mvn spring-boot:run


# API Endpoints Overview
GET /api/prompts - Retrieve a paginated list of prompts (with optional search)
POST /api/prompts - Create a new prompt
GET /api/prompts/{id} - Fetch details of a specific prompt
POST /api/prompts/{id}/execute - Execute the prompt against the AI model
GET /api/prompts/{id}/versions - Get version history for a prompt
POST /api/prompts/{id}/feedback - Submit feedback for a prompt execution