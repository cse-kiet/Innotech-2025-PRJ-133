# Expiry Tracker Backend API

A FastAPI backend for tracking product expiry dates with authentication support.

## Features

- User authentication with JWT tokens
- Product management (CRUD operations)
- Product categories: Medicine, Food, Miscellaneous
- User-specific product tracking
- Expiry date tracking with filtering

## Setup

1. Install PostgreSQL and create a database:
```bash
# Create database (using psql)
createdb expiry_tracker

# Or using SQL:
# CREATE DATABASE expiry_tracker;
```

2. Install dependencies:
```bash
pip install -r requirements.txt
```

3. Set up environment variables (optional):
```bash
# Windows (PowerShell)
$env:DATABASE_URL="postgresql://username:password@localhost:5432/expiry_tracker"

# Linux/Mac
export DATABASE_URL="postgresql://username:password@localhost:5432/expiry_tracker"
```

4. Run the application:
```bash
uvicorn main:app --reload
```

The API will be available at `http://localhost:8000`

## API Documentation

Once the server is running, you can access:
- Interactive API docs: `http://localhost:8000/docs`
- Alternative docs: `http://localhost:8000/redoc`

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get access token
- `GET /api/auth/me` - Get current user info (requires authentication)

### Products

- `POST /api/products/` - Create a new product (requires authentication)
- `GET /api/products/` - Get all products with optional filtering (requires authentication)
- `GET /api/products/{product_id}` - Get a specific product (requires authentication)
- `PUT /api/products/{product_id}` - Update a product (requires authentication)
- `DELETE /api/products/{product_id}` - Delete a product (requires authentication)
- `GET /api/products/category/{category}` - Get products by category (requires authentication)
- `GET /api/products/expiring/soon` - Get products expiring soon (requires authentication)

## Usage Example

1. Register a user:
```bash
curl -X POST "http://localhost:8000/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "username": "user123", "password": "password123"}'
```

2. Login:
```bash
curl -X POST "http://localhost:8000/api/auth/login" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=user@example.com&password=password123"
```

3. Create a product (use the access_token from login):
```bash
curl -X POST "http://localhost:8000/api/products/" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Aspirin",
    "category": "medicine",
    "expiry_date": "2025-12-31",
    "quantity": 10,
    "description": "Pain relief medicine"
  }'
```

## Database

The application uses PostgreSQL. Configure the database connection by setting the `DATABASE_URL` environment variable:
- Format: `postgresql://username:password@host:port/database_name`
- Default: `postgresql://postgres:postgres@localhost:5432/expiry_tracker`

Make sure PostgreSQL is running and the database exists before starting the application.

## Security

- Passwords are hashed using bcrypt
- JWT tokens for authentication
- Change the `SECRET_KEY` environment variable in production

