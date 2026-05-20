# Land Route App

A simple Spring Boot application that calculates a possible land route from one country to another using country border information.

Countries are identified using the `cca3` field from the country data source.

Example response:

```json
{
  "route": ["CZE", "AUT", "ITA"]
}
```

---

## Requirement Summary

This application covers the following requirements:

- Spring Boot application
- Maven-based project
- Loads country data from the provided JSON source
- Exposes REST endpoint:

```http
GET api/v1/routing/{origin}/{destination}
```

- Calculates a land route using country border information
- Returns a single possible route if land travel is possible
- Uses an efficient graph traversal algorithm
- Returns HTTP `400 Bad Request` if:
    - Origin country is invalid
    - Destination country is invalid
    - No land route exists between the countries
- Countries are identified by the `cca3` field

---

## Data Source

The application loads country data from:

```text
https://raw.githubusercontent.com/mledoze/countries/master/countries.json
```

The data source is configured in:

```text
src/main/resources/application.yml
```

```yaml
server:
  port: 8080

spring:
  application:
    name: land-route-app

countries:
  data-url: https://raw.githubusercontent.com/mledoze/countries/master/countries.json
```

---

## Technology Stack

- Java 17
- Spring Boot
- Maven

---

## Project Structure

```text
src/main/java/com/lra/landroute
├── algorithm
│   └── ShortestRouteFinder.java
│
├── client
│   ├── CountryDataClient.java
│   └── HttpCountryDataClient.java
│
├── controller
│   └── RoutingController.java
│
├── dto
│   ├── ErrorResponseDTO.java
│   └── RouteResponseDTO.java
│
├── exception
│   ├── CountryDataLoadException.java
│   ├── GlobalExceptionHandler.java
│   ├── InvalidCountryException.java
│   └── RouteNotFoundException.java
│
├── graph
│   └── CountryBorderGraphBuilder.java
│
├── helper
│   └── RoutingServiceHelper.java
│
├── model
│   └── Country.java
│
├── service
│   ├── RoutingService.java
│   └── impl
│       └── RoutingServiceImpl.java
│
├── util
│   └── StringUtil.java
│
└── LandRouteApplication.java
```

---

## Design Overview

The application separates responsibilities into small, testable components.

### `HttpCountryDataClient`

Loads the country JSON data from the configured external URL.

### `CountryBorderGraphBuilder`

Builds an in-memory country border graph from the loaded country data.

The graph is represented as an adjacency list:

```text
CZE -> [AUT, DEU, POL, SVK]
AUT -> [CZE, DEU, HUN, ITA, SVK, SVN]
ITA -> [AUT, FRA, CHE, SVN]
```

### `ShortestRouteFinder`

Uses Breadth-First Search to find the shortest land route between two countries.

### `RoutingServiceHelper`

Coordinates country validation and route finding.

### `RoutingServiceImpl`

Handles the main routing use case.

### `GlobalExceptionHandler`

Converts application exceptions into proper HTTP error responses.

---

## Algorithm

The application uses Breadth-First Search.

Each country is treated as a graph node.  
Each land border is treated as an undirected edge.

Because each border crossing has equal cost, BFS is suitable for finding the shortest route in terms of number of border crossings.

### Complexity

```text
Graph building: O(V + E)
Route search:   O(V + E)
```

Where:

```text
V = number of countries
E = number of border relationships
```

The country dataset is small, so the graph is loaded once during application startup and kept in memory. This avoids repeated network calls and JSON parsing for every request.

---

## Build and Run Instructions

### Prerequisites

Make sure the following tools are installed:

- Java 17 or later
- Maven 3.8 or later

Check Java version:

```bash
java -version
```

Check Maven version:

```bash
mvn -version
```

---

## 1. Clone or Extract the Project

If using Git:

```bash
git clone https://github.com/ShashiLakshan/land-route-app.git
cd land-route-app
```

Or extract the submitted ZIP file and open the project directory:

```bash
cd land-route-app
```

---

## 2. Build the Application

Run:

```bash
mvn clean package
```

This command will:

- Compile the source code
- Run unit tests
- Package the application as a JAR file

The generated JAR file will be available under:

```text
target/
```

---

## 3. Run the Application

Run using Maven:

```bash
mvn spring-boot:run
```

Or run the packaged JAR:

```bash
java -jar target/land-route-app-0.0.1.jar
```

The application starts on:

```text
http://localhost:8080
```

---

## 4. API Usage

### Find Land Route

```http
GET /routing/{origin}/{destination}
```

### Example Request

```bash
curl http://localhost:8080/api/v1/routing/CZE/ITA
```

### Example Response

```json
{
  "route": ["CZE", "AUT", "ITA"]
}
```

---

## 5. Error Handling

The application returns HTTP `400 Bad Request` for invalid input or impossible land routes.

### Invalid Origin Country

Request:

```bash
curl http://localhost:8080/api/v1/routing/XXX/ITA
```

Response:

```json
{
  "timestamp": "2026-05-21T10:15:30.123",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid origin country code: XXX",
  "path": "/routing/XXX/ITA"
}
```

### Invalid Destination Country

Request:

```bash
curl http://localhost:8080/api/v1/routing/CZE/XXX
```

Response:

```json
{
  "timestamp": "2026-05-21T10:15:30.123",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid destination country code: XXX",
  "path": "/routing/CZE/XXX"
}
```

### No Land Route Found

Request:

```bash
curl http://localhost:8080/api/v1/routing/LKA/ITA
```

Response:

```json
{
  "timestamp": "2026-05-21T10:15:30.123",
  "status": 400,
  "error": "Bad Request",
  "message": "No land route found from LKA to ITA",
  "path": "/routing/LKA/ITA"
}
```

---

## Notes

- The country data is loaded once during application startup.
- The full country JSON is not kept permanently in memory.
- Only the required border graph is stored as an immutable adjacency list.
- The endpoint must remain `api/v1/routing/{origin}/{destination}` to match the test specification.