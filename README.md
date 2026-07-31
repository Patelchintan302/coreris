# CoreRIS: Radiology Information System (RIS) Backend

CoreRIS is a production-oriented, secure Spring Boot backend REST API designed for managing clinical radiology workflows, patient registration, scan scheduling, diagnostic reports, and role-based staff operations.

---

## 🚀 Key Technical Features

*   **Clinical State Machine:** Manages the entire lifecycle of a patient's radiology visit:
    `PENDING` ➔ `BOOKED` ➔ `SCANNING` ➔ `SCAN_COMPLETE` ➔ `REPORTING` ➔ `COMPLETED` (or `CANCELLED`).
*   **Role-Based Access Control (RBAC):** Method-level access control (`@PreAuthorize`) restricts clinical operations to specific roles:
    *   **Receptionists:** Register patients, book/cancel appointments.
    *   **Technicians:** Claim appointments, perform scans, and upload raw image results.
    *   **Radiologists:** Claim scans, write diagnostic reports, and finalize cases.
    *   **Administrators:** Full system overrides and user credential management.
*   **Global Role Hierarchy:** Configured via `RoleHierarchy` in `SecurityConfig` so that `ROLE_ADMIN` dynamically inherits all capabilities of clinical roles, keeping controller annotations clean and dry.
*   **Stateless Security & JWT:** Implements stateless session management, BCrypt password hashing, and token verification middleware (`JwtAuthenticationFilter`) signing claims with HS512 HMAC signatures.
*   **Dynamic Principal Resolution:** Resolves logged-in technician and radiologist database IDs dynamically from cryptographically verified tokens using Spring's `@AuthenticationPrincipal`, closing parameters-forgery vulnerabilities (ID spoofing).
*   **Automatic Database Seeding:** Automatically populates realistic, dummy data (staff, patients, appointments, and diagnostic history) on clean startups for fast manual/Postman testing.
*   **Interactive API Docs (Swagger):** Built-in SpringDoc OpenAPI with an **"Authorize" (lock icon) button** that automatically injects your Bearer JWT tokens into browser test queries.

---

## 🛠️ Prerequisites

To run this project locally, you will need:
1.  **Java SDK 25** (OpenJDK)
2.  **PostgreSQL** database (running on port `5432`)
3.  **Maven** (or use the included wrapper `./mvnw`)

---

## 📦 Local Setup & Execution Guide

Follow these steps to run the project from scratch on your local machine:

### 1. Configure the Database
Create a database named **`ris_db`** in your PostgreSQL instance:
```sql
CREATE DATABASE ris_db;
```

### 2. Configure Environment Properties
Navigate to `coreris/src/main/resources/application.properties` and verify/update your database username and password credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ris_db?options=-c timezone=Asia/Kolkata
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
```

### 3. Change Directory & Build the Project
Open your terminal at the root directory of the repository, navigate into the project folder, and compile the code:
```bash
cd coreris
mvn clean compile
```

### 4. Run the Application
Start the Spring Boot server:
```bash
mvn spring-boot:run
```
Once the log prints `Started CorerisApplication in X.XXX seconds`, the server is running locally on **`http://localhost:8080`**.

---

## 🔑 Pre-seeded Test Credentials

For quick local testing and JWT generation, the database seeder creates the following accounts automatically on startup:

| Staff Name | Role | Username | Password |
| :--- | :--- | :--- | :--- |
| **System Administrator** | `ROLE_ADMIN` | `admin1` | `adminpassword` |
| **Jane Tech** (Technician) | `ROLE_TECHNICIAN` | `user1` | `password1` |
| **Dr. John Smith** (Radiologist) | `ROLE_RADIOLOGIST` | `user2` | `password2` |
| **Alice Reception** (Receptionist) | `ROLE_RECEPTIONIST` | `user3` | `password3` |

---

## 🌐 API Documentation & Testing

### 1. View Interactive Swagger UI
Open your browser and navigate to:
```text
http://localhost:8080/swagger-ui/index.html
```

### 2. How to Test Secure Endpoints in Swagger:
1.  Locate the `/auth/login` endpoint inside Swagger, click **Try it out**, and execute with one of the credentials above (e.g. `user3` / `password3` to log in as the Receptionist).
2.  Copy the returned `jwt` token string from the JSON response.
3.  Scroll to the top of the Swagger page and click the green **Authorize** button.
4.  Paste your token into the field and click **Authorize**.
5.  All secured endpoints (like `GET /patients`) are now unlocked and can be tested directly from the browser!

---

## 📅 Future Roadmap

*   **Local & Cloud Image Storage Engine:** Upload raw X-rays/MRIs directly via `multipart/form-data` and stream them securely to the browser.
*   **Billing & Invoicing Module:** Automated price lists based on scan types, invoice creation, and billing workflows.
*   **Frontend Client Integration:** React/Next.js client interface with dedicated receptionist, technician, radiologist, and admin portals.

*Stay tuned for the next updates!*
