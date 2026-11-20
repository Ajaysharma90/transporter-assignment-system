# FreightFox – Transporter Assignment on Lanes

A Spring Boot REST API that assigns transporters to multiple logistics lanes while minimizing total transportation cost, maximizing transporter utilization within a specified limit, and ensuring complete lane coverage.

## Features

- **Cost Minimization:** Finds the minimum total transportation cost.
- **Maximum Transporter Utilization:** Uses the maximum feasible number of distinct transporters up to the user-defined limit.
- **Full Lane Coverage:** Ensures every lane receives exactly one transporter assignment.
- **REST APIs:** Provides endpoints for submitting lane and transporter quote data and generating optimized assignments.
- **PostgreSQL Integration:** Stores lanes, transporters, and their quotes in a relational database.
- **Input Validation:** Validates request data and handles invalid inputs.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 4.0.9 | Backend framework |
| Spring Data JPA | Database operations |
| PostgreSQL | Relational database |
| Maven | Build management |
| Hibernate | ORM |
| Postman | API testing |

## Project Structure

```text
transporter-assignment/
├── src/
│   ├── main/
│   │   ├── java/com/ajay/transporter/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   └── config/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Database Configuration

Create a PostgreSQL database:

```sql
CREATE DATABASE freightfox_db_v2;
```

Configure the database in `src/main/resources/application.properties`:

```properties
spring.application.name=transporter-assignment

spring.datasource.url=jdbc:postgresql://localhost:5432/freightfox_db_v2
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8080
```

Set the database password as an environment variable. Do not commit database credentials to GitHub.

## Running the Application

### Prerequisites

- Java 21 or later
- PostgreSQL
- Maven (or the included Maven Wrapper)

### 1. Clone the repository

```bash
git clone https://github.com/Ajaysharma90/freightfox-transporter-assignment.git
cd freightfox-transporter-assignment
```

### 2. Set the database password

On Windows PowerShell:

```powershell
$env:DB_PASSWORD = "YOUR_POSTGRESQL_PASSWORD"
```

Replace the placeholder with your local PostgreSQL password. Never publish it in source code.

### 3. Build the application

```powershell
.\mvnw.cmd clean package
```

### 4. Run the application

```powershell
java -jar .\target\transporter-assignment-0.0.1-SNAPSHOT.jar
```

The application runs at:

`http://localhost:8080`

## API Documentation

### 1. Save Input Data

**Endpoint**

```http
POST /api/v1/transporters/input
```

**Request Body**

```json
{
  "lanes": [
    {
      "id": 1,
      "origin": "City A",
      "destination": "City B"
    },
    {
      "id": 2,
      "origin": "City B",
      "destination": "City C"
    }
  ],
  "transporters": [
    {
      "id": 1,
      "name": "Transporter A",
      "laneQuotes": [
        {
          "laneId": 1,
          "quote": 1000
        },
        {
          "laneId": 2,
          "quote": 1500
        }
      ]
    },
    {
      "id": 2,
      "name": "Transporter B",
      "laneQuotes": [
        {
          "laneId": 1,
          "quote": 1200
        },
        {
          "laneId": 2,
          "quote": 1300
        }
      ]
    }
  ]
}
```

**Success Response**

```json
{
  "status": "success",
  "message": "Input data saved successfully."
}
```

### 2. Generate Transporter Assignments

**Endpoint**

```http
POST /api/v1/transporters/assignment
```

**Request Body**

```json
{
  "maxTransporters": 2
}
```

**Success Response**

```json
{
  "status": "success",
  "totalCost": 2300.00,
  "assignments": [
    {
      "laneId": 1,
      "transporterId": 1
    },
    {
      "laneId": 2,
      "transporterId": 2
    }
  ],
  "selectedTransporters": [
    {
      "id": 1,
      "name": "Transporter A"
    },
    {
      "id": 2,
      "name": "Transporter B"
    }
  ]
}
```

*The response demonstrates the API response structure and an example optimized assignment.*

## Postman Collection

A Postman collection is included in the `postman/` directory for testing both REST API endpoints.

### Import Instructions

1. Open Postman.
2. Click **Import**.
3. Select the collection JSON file from the `postman/` directory.
4. Start the Spring Boot application locally.
5. Execute the requests in the following order:
   - `Save Input Data`
   - `Generate Transporter Assignments`

**Base URL:** `http://localhost:8080`

The collection includes sample request bodies for both endpoints.

## Optimization Approach

The assignment algorithm follows these priorities:

1. Maximize the number of distinct transporters used, without exceeding `maxTransporters`.
2. Minimize the total cost among feasible assignments using that number of transporters.
3. Assign every lane to exactly one transporter.
4. Ensure every selected transporter is used at least once.

The implementation evaluates feasible transporter combinations and uses dynamic programming to find the minimum-cost lane assignments for each combination.

If a lane has no available transporter quote, the application returns an error instead of generating an incomplete assignment.

## Error Handling

The application handles:

- Invalid request data.
- Missing required fields.
- Invalid JSON.
- Lanes without available transporter quotes.
- Unexpected server errors.

## Testing

The APIs were tested using Postman.

Verified scenarios include:

- Successful input submission.
- Successful transporter assignment.
- Cost optimization.
- Maximum transporter limit.
- Missing lane quote handling.
- Invalid transporter limit.
- Official assignment test cases.

## Author

**Ajay Sharma**

---

**FreightFox Backend Assignment – Transporter Assignment on Lanes**