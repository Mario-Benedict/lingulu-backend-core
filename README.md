# 🐯 Lingulu — Backend Core Service

This is the Backend Core service for Lingulu, responsible for authentication, course management, learning progress tracking, AI conversation, leaderboard system, and cloud integrations.

Following microservice architecture principles (Controller → Service → Repository → Entity).

---
## 🌍 Lingulu Ecosystem

This repository is part of:
- lingulu-backend-core (Main API)
- lingulu-machine-learning
- lingulu-frontend

---

## 🎯 Backend Responsibilities

The Backend Core is responsible for:
- Handling all REST API requests from frontend
- Authentication (JWT + OAuth2)
- OTP verification system
- Course & learning management
- Learning progress tracking
- Leaderboard calculation
- AI conversation processing (Groq API)
- Audio handling (upload, storage, TTS/STT)
- AWS integrations (S3, Polly, CloudFront)
- Database persistence (JPA/Hibernate)
- Caching (Redis)
- Secure file delivery (signed CDN URLs)

---

## 🏗️ Architecture Flow

### 🔐 Authentication Flow

```pgsql
Frontend
↓
Auth Controller
↓
JWT / OAuth2
↓
Security Filter
↓
Protected API Access
```

### 📚 Learning Flow

```pgsql
Frontend (Start Lesson)
↓
LearningController
↓
LearningService
↓
Repository (JPA)
↓
Database
↓
JSON Response
```

### 🎙️ Pronunciation / Audio Flow

```pgsql
Frontend (record voice)
↓
Backend API (Spring Boot)
↓
File upload to S3
↓
(Optional) Whisper STT processing
↓
Score / Result processing
↓
Database storage
↓
Response to frontend (JSON)
```

### 🤖 AI Conversation Flow

```scss
Frontend (chat message)
↓
ConversationController
↓
GroqService
↓
Groq API
↓
Store Conversation
↓
Response to frontend (JSON)
```

---

## 🛠️ Tech Stack
- Java 17+
- Spring Boot
- Spring Security (JWT + OAuth2)
- Spring Data JPA (Hibernate)
- Redis
- AWS S3
- AWS CloudFront
- AWS Polly
- Groq API (AI conversation)
- PostgreSQL / MySQL
- Maven

---

## 🔐 Security Architecture

The backend uses:

- JWT Authentication
- OAuth2 Login (Google)
- Role-based access control
- Custom AuthenticationEntryPoint
- Password encryption (BCrypt)
- OTP verification system

---
## 🧩 Core Modules
### 👤 Account Module

Handles:
- Registration
- Login (JWT / OAuth)
- OTP Verification
- Profile management
- Password reset

Entities:
- User
- OAuthAccount
- UserProfile
- UserLearningStats

### 📚 Course & Learning Module

Handles:
- Course
- Lesson
- Section
- Section types:
    - Grammar
    - Vocabulary
    - MCQ (Multiple Choice Questions)
        - MCQQuestion
        - MCQOption
    - Speaking

Progress tracking:
- CourseProgress
- LessonProgress
- SectionProgress
- AnsweredQuestion

Leaderboard:
- Leaderboard entity
- LeaderboardService

### 🤖 AI Conversation Module

Handles:
- AI chat conversation
- Message storage
- Groq API integration

Entities:
- Conversation
- ConversationMessage

Service:
- GroqService
- ConversationService

### 🔊 Audio & Cloud Integration

- AWS Polly → Text to Speech
- Whisper → Speech to Text
- AWS S3 → File storage
- CloudFront → CDN signed URLs
- Redis → Caching / OTP storage

Services:
- PollyService
- WhisperService
- S3StorageService
- CloudFrontSigner
- RedisConfig

### 🗄 Database Design

Uses JPA with layered entities:

**Account Domain**
- User
- OAuthAccount
- UserProfile
- UserLearningStats
- Course Domain

**Course**
- Lesson
- Section
- SectionType
- MCQQuestion
- MCQOption
- Speaking
- Vocabulary
- Grammar

**Progress Domain**
- CourseProgress
- LessonProgress
- SectionProgress

---

## ⚙️ Installation

### 📋 Requirements

Make sure you have installed:
- Java 25+
- Maven 3.9+
- PostgreSQL
- Redis
- AWS credentials (S3, CloudFront, Polly)
- Groq API key (for AI conversation)

### 📦 Install Dependencies

```bash
mvn clean install
```

---

## ⚙️ Configuration

```css
src/main/resources/
├── application.properties
├── secret.properties
└── private_lingulu_cdn_key_pkcs8.der
```
You must configure:
- Database connection
- JWT secret
- AWS credentials
- Redis connection
- Groq API key

---
## 🛠 How to Run

### 1️⃣ Clone the repository

```bash
git clone https://github.com/your-username/lingulu-backend-core.git
cd lingulu-backend-core
```

### 2️⃣ Configure environment

Update:

```bash
application.properties
secret.properties
```
### 3️⃣ Run the application

```bash
./mvnw spring-boot:run
```

or

```bash
mvn clean install
mvn spring-boot:run
```

---

## 📡 API Architecture

Follows RESTful design:

```bash
- /api/account
- /api/otp
- /api/dashboard
- /api/leaderboard
- /api/conversation
- /api/learning
- /api/learning/progress
```

All secured endpoints require JWT token in cookie

---

## 🧪 Testing

```bash
src/test/java
```

Run:

```bash
mvn test
```
---
## 📌 Design Principles

- Clean microservice architecture
- Separation of concerns
- SOLID principles
- DTO pattern
- Global exception handling
- Centralized security config
- Modular service structure

---

## 🚀 Future Improvements

### 🧠 Intelligent Scaling
- Auto-scaling via Kubernetes (HPA)
- Resource-based scaling policies
- Circuit breaker pattern (Resilience4j)
- Load balancing optimization

### 🔐 Advanced Security
- Service-to-service authentication

### 📈 Advanced Observability
- Distributed tracing (OpenTelemetry)
- Centralized logging (ELK stack)
- Performance bottleneck tracing
- Alerting system integration

### 🤓 AI Evolution
- Accent-based personalization
- ML-driven recommendation system

---

Made with love ❤️, lack of sleep 🥱 and tears 🥹 by MACAN MULAZ 🐅

