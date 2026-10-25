# Movie Reservation System (Backend REST API)

Spring Boot 3 · Java 17 · PostgreSQL · Spring Security + JWT · Spring Data JPA

## 1. Setup
1. Install Java 17+, Maven, PostgreSQL.
2. Create the database:
   ```sql
   CREATE DATABASE movie_reservation;
   ```
3. Edit `src/main/resources/application.yml` if your Postgres username/password differ.
4. Run:
   ```bash
   mvn spring-boot:run
   ```
   Tables are auto-created. On first start a seeder adds:
   - Admin: `admin@mrs.com` / `admin123`
   - 50 seats each for "Screen 1" and "Screen 2" (rows A-E, seats 1-10)
   - Sample movie "Inception" with one showtime on Screen 1

## 2. Endpoints
| Method | Path | Access | Purpose |
|---|---|---|---|
| POST | /api/auth/register | public | Sign up (FR1) |
| POST | /api/auth/login | public | Login, returns JWT (FR1) |
| GET | /api/movies, /api/movies/{id} | public | Browse movies (FR2) |
| POST/PUT/DELETE | /api/movies[/{id}] | ADMIN | Manage movies (FR7) |
| GET | /api/showtimes?movieId=1 | public | Showtimes for a movie (FR3) |
| GET | /api/showtimes/{id}/seats | public | Seat map + availability |
| POST/PUT/DELETE | /api/showtimes[/{id}] | ADMIN | Manage showtimes (FR7) |
| POST | /api/reservations | USER | Reserve seats (FR4, FR5) |
| GET | /api/reservations/my | USER | My reservations (FR6) |
| DELETE | /api/reservations/{id} | owner/ADMIN | Cancel reservation (FR6) |
| GET | /api/admin/reports/showtimes | ADMIN | Seats booked + revenue per showtime (FR8) |
| GET | /api/admin/reports/movies/{movieId} | ADMIN | Same, for one movie (FR8) |

Send the token as `Authorization: Bearer <token>`.

## 3. Try it
```bash
# login
curl -X POST localhost:8080/api/auth/login -H "Content-Type: application/json" \
  -d '{"email":"admin@mrs.com","password":"admin123"}'

# list seats for showtime 1
curl localhost:8080/api/showtimes/1/seats

# register a user, then reserve seats 1 and 2 for showtime 1
curl -X POST localhost:8080/api/auth/register -H "Content-Type: application/json" \
  -d '{"name":"Nithya","email":"n@test.com","password":"secret123"}'
curl -X POST localhost:8080/api/reservations -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" -d '{"showtimeId":1,"seatIds":[1,2]}'
# repeat the same call -> 409 Conflict (double-booking prevented)
```

## 4. How double-booking is prevented (FR5 / NFR2)
`ReservationService.reserveSeats` is `@Transactional` and first runs `SELECT ... FOR UPDATE`
on the requested seat rows (`SeatRepository.lockAllByIds`). A concurrent request for the same
seats blocks until the first commits, then re-checks availability and receives `409 Conflict`.
Cancelling sets status to `CANCELLED`, which frees the seats again.

## 5. Project layout
`controller/` REST endpoints · `service/` business logic · `repository/` Spring Data JPA ·
`entity/` JPA models (matches ER diagram) · `dto/` request/response records ·
`security/` + `config/` JWT and Spring Security · `exception/` global JSON error handling.
