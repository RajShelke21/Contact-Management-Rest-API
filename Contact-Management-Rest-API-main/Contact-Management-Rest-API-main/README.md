I built a comprehensive REST API for contact management where multiple users can securely store and manage their personal contacts. The system uses JWT for stateless authentication and ensures complete data isolation between users, making it perfect for real-world multi-tenant applications.
🚀 What Makes This Special
This isn't just another CRUD application - it's a production-ready system that solves real security and scalability challenges:

🔐 Stateless Authentication: JWT-based auth eliminates server-side session management
🏗️ Layered Architecture: Clean separation of concerns with Controllers, Services, and Repositories
🔒 Data Isolation: Users can only access their own data through secure filtering
📁 File Management: Secure photo uploads with conflict prevention
🛡️ Security First: BCrypt password hashing and secure reset flows
⚡ Performance: Optimized database queries and efficient relationship mapping

🛠️ Technical Highlights
Architecture Decision
I chose a layered architecture with clear separation of concerns:

Controllers handle HTTP requests and responses
Services contain all business logic and validation
Repositories manage data access and database operations
Entities represent our domain model with proper relationships

Security Implementation
I implemented JWT-based authentication because it's stateless and perfect for REST APIs. Each user's data is completely isolated using user ID filtering in database queries, ensuring zero data leakage between users.
Database Design
I used a One-to-Many relationship between Users and Contacts with proper foreign key constraints to ensure data integrity and enable efficient queries. The schema supports:

User management with secure password storage
Contact relationships with cascade operations
Password reset token management
Optimized indexing for search operations

File Handling Strategy
For contact photos, I store files on the file system and save paths in the database. This approach:

Keeps the database lightweight and fast
Allows easy file serving through static resources
Implements unique naming to prevent conflicts
Supports multiple file formats with validation

🏗️ System Architecture
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Controllers   │───▶│    Services      │───▶│  Repositories   │
│                 │    │                  │    │                 │
│ • AuthController│    │ • AuthService    │    │ • UserRepo      │
│ • ContactCtrl   │    │ • ContactService │    │ • ContactRepo   │
└─────────────────┘    │ • JwtService     │    └─────────────────┘
                       └──────────────────┘             │
                                                        ▼
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Security      │    │      DTOs        │    │    Database     │
│                 │    │                  │    │                 │
│ • JWT Filter    │    │ • AuthRequest    │    │ • MySQL         │
│ • Security      │    │ • ContactDto     │    │ • JPA/Hibernate │
│   Config        │    │ • UserDto        │    │ • Relationships │
└─────────────────┘    └──────────────────┘    └─────────────────┘
🚀 Key Features
🔐 Authentication & Security

User Registration & Login with input validation
JWT Token Generation with configurable expiration
Password Encryption using BCrypt hashing
Forgot Password functionality with secure token reset
Stateless Authentication - no server-side sessions

👤 Contact Management

Full CRUD Operations - Create, Read, Update, Delete contacts
Photo Upload Support - Store and serve contact photos
Advanced Search - Search by name, phone, or email
Data Validation - Comprehensive input validation with custom patterns
User Isolation - Complete data separation between users

📊 Additional Features

Contact Statistics - Get total contact count per user
File Management - Automatic file cleanup on contact deletion
Error Handling - Comprehensive exception handling with meaningful messages
API Documentation - RESTful endpoints with clear response formats

🔧 Technologies Used
Backend Stack

Java 17 - Latest LTS version with modern features
Spring Boot 2.7.0 - Enterprise-grade framework
Spring Security - Comprehensive security framework
Spring Data JPA - Data persistence and repository pattern
Hibernate - ORM for database operations

Database & Storage

MySQL 8.0 - Reliable relational database
JDBC - Database connectivity
File System Storage - For photo uploads

Security & Authentication

JSON Web Tokens (JWT) - Stateless authentication
BCrypt - Password hashing algorithm
HTTPS Ready - Secure communication support

Development Tools

Maven - Dependency management and build automation
Postman - API testing and documentation
Git - Version control

📦 Installation & Setup
Prerequisites
bash# Required software
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6+
- Git
1. Clone the Repository
bashgit clone https://github.com/yourusername/contact-management-api.git
cd contact-management-api
2. Database Setup
sql-- Create database
CREATE DATABASE contact_db;

-- Create user (optional)
CREATE USER 'contact_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON contact_db.* TO 'contact_user'@'localhost';
FLUSH PRIVILEGES;
3. Configure Application
properties# src/main/resources/application.properties

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/contact_db
spring.datasource.username=root
spring.datasource.password=yourpassword

# JWT Configuration (Generate your own secret)
jwt.secret=myVerySecretKeyForJWTTokenGenerationAndValidation
jwt.expiration=86400000

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
4. Build and Run
bash# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run

# Or build JAR and run
mvn clean package
java -jar target/contact-management-api-1.0.0.jar
The API will be available at http://localhost:8080
🗄️ Database Schema
Users Table
sqlCREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    reset_token VARCHAR(255),
    reset_token_expiry DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
Contacts Table
sqlCREATE TABLE contacts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    phone_no VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    gender ENUM('Male', 'Female', 'Other'),
    photo VARCHAR(255),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
🔌 API Endpoints
Authentication Endpoints
Register New User
httpPOST /api/auth/signup
Content-Type: application/json

{
    "username": "john_doe",
    "password": "password123",
    "email": "john@example.com"
}
User Login
httpPOST /api/auth/signin
Content-Type: application/json

{
    "username": "john_doe",
    "password": "password123"
}

Response:
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "john_doe",
    "email": "john@example.com"
}
Forgot Password
httpPOST /api/auth/forgot-password
Content-Type: application/json

{
    "email": "john@example.com"
}
Reset Password
httpPOST /api/auth/reset-password
Content-Type: application/json

{
    "token": "reset-token-uuid",
    "newPassword": "newpassword123"
}
Contact Management Endpoints
Get All Contacts
httpGET /api/contacts
Authorization: Bearer {jwt_token}

Response: Array of contact objects
Create New Contact
httpPOST /api/contacts
Authorization: Bearer {jwt_token}
Content-Type: multipart/form-data

Form Data:
- name: "Alice Smith"
- phoneNo: "+1234567890"
- email: "alice@example.com"
- gender: "Female"
- photo: [image file]
Update Contact
httpPUT /api/contacts/{id}
Authorization: Bearer {jwt_token}
Content-Type: multipart/form-data

Form Data:
- name: "Alice Johnson"
- phoneNo: "+1234567890"
- email: "alice.johnson@example.com"
- gender: "Female"
- photo: [optional image file]
Delete Contact
httpDELETE /api/contacts/{id}
Authorization: Bearer {jwt_token}
Search Contacts
httpGET /api/contacts/search?query=alice
Authorization: Bearer {jwt_token}
Get Contact Count
httpGET /api/contacts/count
Authorization: Bearer {jwt_token}

Response:
{
    "count": 25
}
🧩 Project Structure
src/main/java/com/contactapp/
├── config/
│   ├── SecurityConfig.java           # Security configuration
│   ├── JwtAuthenticationFilter.java  # JWT request filter
│   └── FileUploadConfig.java         # File upload settings
├── controller/
│   ├── AuthController.java           # Authentication endpoints
│   └── ContactController.java        # Contact CRUD endpoints
├── dto/
│   ├── AuthRequest.java              # Login/signup request
│   ├── ContactDto.java               # Contact data transfer
│   ├── UserDto.java                  # User data transfer
│   ├── ForgotPasswordRequest.java    # Password reset request
│   └── ResetPasswordRequest.java     # Password reset confirmation
├── entity/
│   ├── User.java                     # User entity with relationships
│   └── Contact.java                  # Contact entity
├── repository/
│   ├── UserRepository.java           # User data access
│   └── ContactRepository.java        # Contact data access with search
├── service/
│   ├── AuthService.java              # Authentication business logic
│   ├── ContactService.java           # Contact business logic
│   └── JwtService.java               # JWT token operations
├── exception/
│   └── GlobalExceptionHandler.java   # Centralized error handling
└── ContactManagementApplication.java # Main application class
💡 Challenges Solved
1. User Data Isolation
Challenge: Ensuring users can only access their own contacts
Solution: Implemented user ID validation in all database queries and repository methods. Every contact operation includes user ID filtering to prevent unauthorized access.
java// Example: Repository method with user isolation
List<Contact> findByUserId(Long userId);
Optional<Contact> findByIdAndUserId(Long id, Long userId);
2. Stateless Authentication
Challenge: JWT tokens eliminate server-side session management
Solution: Implemented JWT token generation, validation, and extraction with proper security filters. Tokens contain user information and expire automatically.
3. File Management
Challenge: Implemented secure file upload with unique naming to prevent conflicts
Solution: UUID-based file naming system with proper directory management and cleanup on deletion.
java// Unique file naming strategy
String filename = UUID.randomUUID().toString() + extension;
Path filePath = uploadPath.resolve(filename);
4. Password Security
Challenge: Used BCrypt for password hashing and implemented secure reset flow
Solution: BCrypt with configurable strength and time-limited reset tokens with proper validation.
🧪 Testing
Using Postman

Import the API Collection (if available)
Set Environment Variables:

baseUrl: http://localhost:8080
authToken: Bearer {your_jwt_token}


Test Flow:

Register a new user
Login to get JWT token
Create contacts with the token
Test all CRUD operations
Verify data isolation with multiple users



Manual Testing Script
bash# 1. Register user
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123","email":"test@example.com"}'

# 2. Login and get token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}' | jq -r '.token')

# 3. Create contact
curl -X POST http://localhost:8080/api/contacts \
  -H "Authorization: Bearer $TOKEN" \
  -F "name=John Doe" \
  -F "phoneNo=+1234567890" \
  -F "email=john@example.com" \
  -F "gender=Male"

# 4. Get all contacts
curl -X GET http://localhost:8080/api/contacts \
  -H "Authorization: Bearer $TOKEN"
🚀 Performance Optimizations

Database Indexing: Optimized queries with proper indexes on user_id and search fields
Lazy Loading: JPA relationships configured for efficient data loading
Connection Pooling: Database connection management for concurrent users
File System Storage: Efficient file handling without database bloat
JWT Stateless: No server-side session storage reduces memory usage

🔒 Security Features
Authentication Security

JWT tokens with configurable expiration
BCrypt password hashing with salt
Secure password reset with time-limited tokens
Input validation and sanitization

Authorization Security

User-specific data access control
JWT token validation on every request
Proper HTTP status codes for security events
CORS configuration for cross-origin requests

Data Security

SQL injection prevention through parameterized queries
File upload validation and size limits
Secure file storage with unique naming
Comprehensive error handling without information leakage

🔄 Future Enhancements

 Email Integration - Send reset tokens via email
 Contact Sharing - Share contacts between users
 Bulk Operations - Import/export contacts
 Advanced Search - Full-text search with filters
 Rate Limiting - API request throttling
 Caching - Redis integration for performance
 Monitoring - Application metrics and logging
 Mobile App - React Native frontend

📊 API Response Examples
Successful Contact Creation
json{
    "message": "Contact created successfully",
    "contact": {
        "id": 1,
        "name": "Alice Smith",
        "phoneNo": "+1234567890",
        "email": "alice@example.com",
        "gender": "Female",
        "photo": "/uploads/abc123-def456.jpg"
    }
}
Error Response
json{
    "error": "Contact not found",
    "timestamp": "2024-01-15T10:30:00.000Z",
    "status": 404,
    "path": "/api/contacts/999"
}
Validation Error
json{
    "phoneNo": "Invalid phone number",
    "email": "Invalid email format",
    "name": "Name is required"
}
