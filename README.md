# TicketGateway

A full-stack enterprise ticketing system built with Spring Boot. A 4-week learning project covering role-based access control, ticket lifecycle management, email notifications, PDF generation, and file uploads.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 4, Spring Security, Spring Data JPA |
| Frontend | JSP, HTML/CSS, jQuery, AJAX |
| Database | MySQL (H2 for dev/testing) |
| Extras | iText7 (PDF), JavaMailSender (Gmail SMTP), Async processing |

---

## Role System

| Role | Permissions |
|---|---|
| `USER` | Create tickets, view own tickets, close/reopen |
| `MANAGER` | Approve/reject pending tickets, assign to admins |
| `ADMIN` | View assigned tickets, resolve and close tickets |

---

## Ticket Lifecycle

```
OPEN → PENDING_APPROVAL → APPROVED / REJECTED
                              ↓
                          ASSIGNED → RESOLVED → CLOSED
                                                   ↓
                                               REOPENED
```

---

## Project Structure

```
com.synex
├── config/         # Security config, async config, data seeder
├── controller/     # REST + MVC controllers (tickets, employees, roles)
├── entity/         # Ticket, Employee, Role, TicketHistory
├── enums/          # TicketStatus, TicketAction, TicketPriority, RoleName
├── repository/     # Spring Data JPA repositories
└── service/        # Business logic, email, PDF, file storage
```

---

## Running Locally

**Prerequisites:** Java 17, Maven, MySQL

1. Create a database named `ticketgateway`
2. Update credentials in `src/main/resources/application.properties`
3. Add a Gmail App Password for email notifications
4. Run:

```bash
mvn spring-boot:run
```

App starts on `http://localhost:8282`

> Default users are seeded automatically on first run via `DataLoader.java`.

---

## Key Concepts Practiced

- Spring Security filter chain configuration
- Custom `UserDetails` and `UserDetailsService` implementations
- JPA entity relationships and Hibernate DDL auto-update
- Separation of REST API vs MVC controller concerns
- Async method execution with `@Async` and `@EnableAsync`
- iText7 PDF document generation
- Multipart file upload with size limits
