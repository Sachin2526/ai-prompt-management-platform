 
# AI Prompt Management Platform - Frontend

## Description
The frontend for the **AI Prompt Management Platform** is a modern, responsive Single Page Application (SPA) built to efficiently manage, execute, and analyze AI prompts. Designed for ease of use, it interfaces seamlessly with the Spring Boot backend to deliver real-time data to developers and prompt engineers.

## Technologies Used
* **Angular** (Component-based UI framework)
* **TypeScript** (Strongly typed programming language)
* **Tailwind CSS** (Utility-first framework for rapid and modern UI styling)

## Features
* **Prompt Dashboard**: View, organize, and manage all your stored AI prompts.
* **Search and Filter**: Instantly find specific prompts based on keywords or metadata.
* **Version History**: Browse the evolution of any prompt to see what changed.
* **Version Comparison**: Compare different iterations of a prompt side by side.
* **Playground**: An interactive UI to execute prompts instantly and evaluate AI responses.
* **Feedback and Analytics UI**: Log performance metrics and review feedback tracking visually.

## Project Structure
The codebase follows standard Angular application architecture:

* `src/app/components/` & `src/app/pages/` - Reusable UI components and routed view components (Dashboard, Playground, etc.).
* `src/app/services/` - Angular services responsible for state management and executing HTTP calls to the backend.
* `src/app/app.routes.ts` (or `app-routing.module.ts`) - Defines the client-side routing and navigation guards.

## API Integration Note
This frontend application relies on the Spring Boot backend to function. Ensure that the backend server is running (default is typically `http://localhost:8080`) before starting the frontend platform. API base URLs are mapped within the Angular environment configuration (`src/environments/environment.ts`).

## How to Run the Frontend

### Prerequisites
* Node.js (v18 or newer recommended)
* Angular CLI (`npm install -g @angular/cli`)

### Setup
1. **Install Dependencies**: 
Navigate to the frontend directory and run:
###### npm install

Start the Development Server: Serve the application locally
###### ng serve

Access the App: Once compiled successfully, open your browser and navigate to:

###### http://localhost:4200
