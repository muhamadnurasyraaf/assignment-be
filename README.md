# Assignment — Inventory Management API

A Spring Boot REST API for multi-tenant inventory management: companies onboard, staff/admin users manage products under their company, and stock movements track inventory changes.

## Tech Stack

- **Java 25**, **Spring Boot 4.1**
- **PostgreSQL** + **Flyway** (schema-managed via versioned SQL migrations, `spring.jpa.hibernate.ddl-auto=validate` — Hibernate never generates schema)
- **Spring Security** + **JWT** (`jjwt`) for stateless auth
- **Cloudinary** for product image storage

## Project Structure

Package-by-feature, each module following the same shape: `Entity`, `Repository`, `Service`, `Controller`, `dto/`.

```
com.asyraaf.assignment
├── Auth            # register / login, JWT issuing
├── User            # user accounts, roles (ADMIN, STAFF)
├── Company         # tenant boundary — one company per owner
├── Product         # products, scoped to a company
├── StockMovement   # IN/OUT movement log per product
├── common          # ApiResponse envelope, CloudinaryService
└── config          # SecurityConfig, JwtFilter
```

Every request/response outside `/auth/**` requires a `Bearer` JWT. Endpoints are open to any authenticated user unless noted with `@PreAuthorize`.

## Setup

1. **Database** — create a local Postgres DB named `assignment`. Flyway runs all migrations in `src/main/resources/db/migration` automatically on startup.

2. **Config** — copy your own values into `src/main/resources/application-local.properties` (gitignored, not committed):

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/assignment
   spring.datasource.username=postgres
   spring.datasource.password=<your-password>
   spring.jpa.hibernate.ddl-auto=validate
   jwt.secret=<base64-secret>
   jwt.expiration=900000
   spring.jackson.mapper.accept-case-insensitive-enums=true
   cloudinary.cloud-name=<your-cloud-name>
   cloudinary.api-key=<your-api-key>
   cloudinary.api-secret=<your-api-secret>
   ```

   Get Cloudinary credentials from your [Cloudinary dashboard](https://cloudinary.com/console) — required for `POST /product/create` since it uploads the product image before saving the record.

3. **Run**:
   ```bash
   ./mvnw spring-boot:run
   ```
   App serves on `http://localhost:8081/api`.

## API Overview

All paths below are relative to `/api`.

### Auth (`/auth`) — public

| Method | Path        | Description                     |
|--------|-------------|----------------------------------|
| POST   | `/register` | Create a user account, returns JWT |
| POST   | `/login`    | Authenticate, returns JWT       |

### Company (`/company`) — authenticated

| Method | Path      | Description                                  |
|--------|-----------|-----------------------------------------------|
| POST   | `/create` | Create a company owned by the calling user (one company per user, DB-enforced) |
| GET    | `/me`     | Fetch the calling user's company              |

### User (`/user`) — authenticated

| Method | Path      | Description                          | Access       |
|--------|-----------|---------------------------------------|--------------|
| POST   | `/create` | Admin creates a staff/admin user under their own company | `ADMIN` only |
| GET    | `/`       | List all users in the caller's company | `ADMIN` only |
| GET    | `/{id}`   | Fetch a single user (must be in caller's company) | any authenticated user |
| PUT    | `/{id}`   | Update `username`/`role` (must be in caller's company) | any authenticated user |
| DELETE | `/{id}`   | Hard delete a user (must be in caller's company) | any authenticated user |

### Product (`/product`) — authenticated

| Method | Path      | Description                              |
|--------|-----------|--------------------------------------------|
| POST   | `/create` | Create a product under the caller's company (`multipart/form-data`: `name`, `image`, optional `quantity`) |
| GET    | `/`       | List all products in the caller's company |
| GET    | `/{id}`   | Fetch a single product (must belong to caller's company) |
| PUT    | `/{id}`   | Update a product (`multipart/form-data`, all fields optional: `name`, `image`, `quantity`) |
| DELETE | `/{id}`   | Delete a product (must belong to caller's company) |

SKU is auto-generated (`<3-letter company prefix><product count>`), and product creation is serialized per-company (pessimistic lock) to prevent duplicate SKUs under concurrent requests. `quantity` defaults to `0` if omitted.

### Stock Movement (`/stock-movement`) — authenticated

| Method | Path                       | Description                              |
|--------|----------------------------|--------------------------------------------|
| POST   | `/create`                  | Record an `IN`/`OUT` movement for a product in the caller's company, atomically adjusting its `quantity` |
| GET    | `/`                        | Paginated, latest-first list of all movements in the caller's company (`page`, `size` query params, default `0`/`10`) |
| GET    | `/product/{productId}`     | List movement history for a product (must belong to caller's company) |

An `OUT` movement larger than the current stock is rejected with `409 CONFLICT` — the quantity check and update happen in a single atomic DB statement, so this is safe under concurrent requests.

## Known Limitations

- `PUT /user/{id}` isn't role-restricted — any authenticated user can change any other user's `role` in the same company, including promoting themselves/others to `ADMIN`. Only `POST /user/create` and `GET /user` are locked to `ADMIN`. Flagging this since it's a privilege-escalation path; restrict it if that's not intended.
- `DELETE /product/{id}` and `DELETE /user/{id}` don't check for dependent rows first. `stock_movements.product_id` and `stock_movements.user_id` are `NOT NULL` FKs with no `ON DELETE` rule, so deleting a product or user that has movement history fails with an unhandled DB constraint error (500) instead of a friendly response.
- Registering without a `role` sends `null` through to `User.role` explicitly (bypassing the entity's default of `STAFF`), which will fail the `NOT NULL` constraint on insert.
- `Company.create` doesn't catch the DB unique-constraint violation if two requests race to create a company for the same user — the second gets an unhandled 500 instead of a friendly 409.
