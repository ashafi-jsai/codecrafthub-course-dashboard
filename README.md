# CodeCraftHub - Course Tracking Service

A simple REST API built with Java and Spring Boot that lets developers track courses they want to learn. Course data is stored in a local `courses.json` file — no database required.

---

## Features

- Add, view, update, and delete courses
- Track status: `Not Started`, `In Progress`, or `Completed`
- Set a target completion date for each course
- View statistics: total courses and count per status
- Data persisted automatically in `src/main/resources/courses.json`

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 21+ |
| Maven | 3.8+ |

---

## Installation

```bash
# Clone or download the project, then navigate to it
cd Course-Service

# Download dependencies and compile
mvn clean install
```

---

## Running the Application

```bash
mvn spring-boot:run
```

The server starts at **http://localhost:8080**.  
Data is stored in `src/main/resources/courses.json` (created automatically if empty).

---

## API Endpoints

### Base URL: `http://localhost:8080/api/courses`

---

### 1. Get All Courses

```
GET /api/courses
```

**Response `200 OK`**
```json
[
  {
    "id": 1,
    "name": "Spring Boot Basics",
    "description": "Learn Spring Boot from scratch",
    "target_date": "2025-12-31",
    "status": "In Progress",
    "created_at": "2025-06-09T10:00:00"
  }
]
```

---

### 2. Get a Specific Course

```
GET /api/courses/{id}
```

**Response `200 OK`**
```json
{
  "id": 1,
  "name": "Spring Boot Basics",
  "description": "Learn Spring Boot from scratch",
  "target_date": "2025-12-31",
  "status": "In Progress",
  "created_at": "2025-06-09T10:00:00"
}
```

**Response `404 Not Found`**
```json
{ "error": "Course not found with id: 1" }
```

---

### 3. Add a New Course

```
POST /api/courses
Content-Type: application/json
```

**Request Body**
```json
{
  "name": "Docker for Developers",
  "description": "Containerize applications using Docker",
  "target_date": "2025-09-01",
  "status": "Not Started"
}
```

**Response `201 Created`**
```json
{
  "id": 2,
  "name": "Docker for Developers",
  "description": "Containerize applications using Docker",
  "target_date": "2025-09-01",
  "status": "Not Started",
  "created_at": "2025-06-09T11:30:00"
}
```

**Response `400 Bad Request`** (missing or invalid field)
```json
{ "error": "Invalid status 'Pending'. Must be one of: [Not Started, In Progress, Completed]" }
```

---

### 4. Update a Course

```
PUT /api/courses/{id}
Content-Type: application/json
```

**Request Body** (all fields required)
```json
{
  "name": "Docker for Developers",
  "description": "Containerize applications using Docker",
  "target_date": "2025-10-01",
  "status": "In Progress"
}
```

**Response `200 OK`** — returns the updated course  
**Response `404 Not Found`** — if the ID doesn't exist

---

### 5. Get Course Statistics

```
GET /api/courses/stats
```

**Response `200 OK`**
```json
{
  "total": 3,
  "by_status": {
    "Not Started": 1,
    "In Progress": 1,
    "Completed": 1
  }
}
```

---

### 7. Delete a Course

```
DELETE /api/courses/{id}
```

**Response `200 OK`**
```json
{ "message": "Course with id 2 deleted successfully." }
```

**Response `404 Not Found`**
```json
{ "error": "Course not found with id: 2" }
```

---

## Valid Status Values

| Value | Meaning |
|-------|---------|
| `Not Started` | Course hasn't been started yet |
| `In Progress` | Currently working through the course |
| `Completed` | Finished the course |

---

## Troubleshooting

**Port already in use**
```
Web server failed to start. Port 8080 was already in use.
```
Change the port in `src/main/resources/application.properties`:
```properties
server.port=9090
```

**`courses.json` is corrupted / parse error**
Delete or fix the file manually. The app will recreate an empty file on the next write:
```bash
rm courses.json
```

**`mvn` command not found**
Install Maven from https://maven.apache.org/download.cgi and ensure it is on your `PATH`.

**Java version mismatch**
Verify you are using Java 21+:
```bash
java -version
```

---

## Project Structure

```
Course-Service/
├── src/
│   └── main/
│       ├── java/net/courses/
│       │   ├── CodeCraftHubApplication.java   # Spring Boot entry point
│       │   ├── controller/
│       │   │   └── CourseController.java       # HTTP request handlers
│       │   ├── model/
│       │   │   └── Course.java                 # Course data model
│       │   └── service/
│       │       └── CourseService.java          # Business logic + file I/O
│       └── resources/
│           └── application.properties          # App configuration
├── courses.json                                # Auto-created data file
├── pom.xml                                     # Maven dependencies
└── README.md
```
