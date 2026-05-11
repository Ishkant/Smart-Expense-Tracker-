# 💰 Smart Expense Tracker — Backend

A **Spring Boot + MySQL** backend for the Smart Expense Tracker college project.

---

## 🚀 Tech Stack

| Layer       | Technology              |
|-------------|-------------------------|
| Framework   | Spring Boot 3.2         |
| Language    | Java 17                 |
| Security    | Spring Security + JWT   |
| Database    | MySQL 8                 |
| ORM         | Spring Data JPA / Hibernate |
| Build Tool  | Maven                   |

---

## ⚙️ Setup Instructions

### 1. Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.x running locally

### 2. Database Setup
```sql
CREATE DATABASE expense_tracker_db;
```
> The app will auto-create all tables on first run (`spring.jpa.hibernate.ddl-auto=update`).

### 3. Configure `application.properties`
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### 4. Run the Application
```bash
mvn spring-boot:run
```
Server starts at: `http://localhost:8080`

---

## 📁 Project Structure

```
src/main/java/com/expensetracker/
├── ExpenseTrackerApplication.java   # Main entry point
├── config/
│   ├── SecurityConfig.java          # Security & CORS config
│   └── CurrentUserHelper.java       # Auth user resolver
├── controller/
│   ├── AuthController.java          # /api/auth/**
│   ├── ExpenseController.java       # /api/expenses/**
│   └── BudgetController.java        # /api/budgets/**
├── dto/
│   ├── ApiResponse.java             # Generic wrapper
│   ├── AuthDTO.java                 # Register/Login DTOs
│   ├── ExpenseDTO.java              # Expense DTOs
│   └── BudgetDTO.java               # Budget & Analytics DTOs
├── entity/
│   ├── User.java
│   ├── Expense.java
│   └── Budget.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── BadRequestException.java
├── repository/
│   ├── UserRepository.java
│   ├── ExpenseRepository.java       # Custom JPQL queries
│   └── BudgetRepository.java
├── security/
│   ├── JwtUtils.java                # Token generation & validation
│   ├── JwtAuthFilter.java           # Request filter
│   └── UserDetailsServiceImpl.java
└── service/
    ├── AuthService.java
    ├── ExpenseService.java
    ├── BudgetService.java
    └── impl/
        ├── AuthServiceImpl.java
        ├── ExpenseServiceImpl.java
        └── BudgetServiceImpl.java
```

---

## 🔐 Authentication

All endpoints except `/api/auth/**` require a **Bearer JWT token** in the `Authorization` header.

```
Authorization: Bearer <your_token_here>
```

---

## 📡 API Reference

### Auth Endpoints

| Method | URL                  | Description          | Auth Required |
|--------|----------------------|----------------------|---------------|
| POST   | `/api/auth/register` | Register new user    | ❌            |
| POST   | `/api/auth/login`    | Login & get token    | ❌            |
| PUT    | `/api/auth/profile`  | Update user profile  | ✅            |

**Register Request:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "secret123",
  "currency": "INR",
  "monthlyBudget": 50000
}
```

**Login Request:**
```json
{
  "email": "john@example.com",
  "password": "secret123"
}
```

**Auth Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGci...",
    "type": "Bearer",
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "currency": "INR",
    "monthlyBudget": 50000
  }
}
```

---

### Expense Endpoints

| Method | URL                             | Description                  |
|--------|---------------------------------|------------------------------|
| POST   | `/api/expenses`                 | Add expense or income        |
| GET    | `/api/expenses`                 | Get all expenses             |
| GET    | `/api/expenses/{id}`            | Get single expense           |
| PUT    | `/api/expenses/{id}`            | Update expense               |
| DELETE | `/api/expenses/{id}`            | Delete expense               |
| GET    | `/api/expenses/month?month=4&year=2026` | Get by month/year  |
| GET    | `/api/expenses/category/{cat}`  | Filter by category           |
| GET    | `/api/expenses/search?keyword=food` | Search expenses         |
| GET    | `/api/expenses/categories`      | Get all default categories   |

**Expense Request Body:**
```json
{
  "title": "Lunch at Canteen",
  "amount": 120.00,
  "category": "Food & Dining",
  "date": "2026-04-13",
  "description": "Veg thali",
  "paymentMethod": "UPI",
  "type": "EXPENSE"
}
```

**Type values:** `EXPENSE` | `INCOME`

**PaymentMethod values:** `CASH` | `CARD` | `UPI` | `NET_BANKING` | `OTHER`

---

### Budget Endpoints

| Method | URL                              | Description                   |
|--------|----------------------------------|-------------------------------|
| POST   | `/api/budgets`                   | Set budget for a category     |
| GET    | `/api/budgets`                   | Get all budgets               |
| GET    | `/api/budgets/month?month=4&year=2026` | Get budgets for month   |
| PUT    | `/api/budgets/{id}`              | Update budget                 |
| DELETE | `/api/budgets/{id}`              | Delete budget                 |
| GET    | `/api/budgets/analytics?month=4&year=2026` | 📊 Get full analytics |

**Budget Request Body:**
```json
{
  "category": "Food & Dining",
  "limitAmount": 5000,
  "month": 4,
  "year": 2026
}
```

---

### 📊 Analytics Response (Smart Feature)

`GET /api/budgets/analytics?month=4&year=2026`

```json
{
  "success": true,
  "data": {
    "totalExpense": 12500.00,
    "totalIncome": 30000.00,
    "savings": 17500.00,
    "categoryBreakdown": {
      "Food & Dining": 3200.00,
      "Transportation": 1500.00,
      "Shopping": 4800.00
    },
    "dailyExpenses": [
      { "day": 1, "amount": 450.00 },
      { "day": 2, "amount": 120.00 }
    ],
    "monthlyTrend": [
      { "month": 1, "monthName": "Jan", "amount": 11000.00 },
      { "month": 2, "monthName": "Feb", "amount": 9500.00 }
    ],
    "budgetStatus": [
      {
        "id": 1,
        "category": "Food & Dining",
        "limitAmount": 5000.00,
        "spent": 3200.00,
        "remaining": 1800.00,
        "percentageUsed": 64.0,
        "month": 4,
        "year": 2026,
        "status": "SAFE"
      }
    ]
  }
}
```

**Budget Status values:** `SAFE` | `WARNING` (≥80%) | `EXCEEDED` (≥100%)

---

## 🗂️ Default Expense Categories
- Food & Dining
- Transportation
- Shopping
- Entertainment
- Bills & Utilities
- Healthcare
- Education
- Travel
- Personal Care
- Housing
- Salary
- Freelance
- Investment
- Other

---

## 🔒 Security Features
- Passwords are hashed using **BCrypt**
- JWT token expires in **24 hours**
- All user data is **isolated** — users can only access their own records

---

## 🧪 Testing with Postman / Thunder Client

1. **Register** → copy the `token` from response
2. In all subsequent requests, set header:
   ```
   Authorization: Bearer <token>
   ```
3. Test the `/api/budgets/analytics` endpoint for the smart dashboard data

---

## 🏗️ Features Summary

| Feature                        | Implemented |
|-------------------------------|-------------|
| JWT Authentication            | ✅          |
| User Registration & Login     | ✅          |
| Add / Edit / Delete Expenses  | ✅          |
| Income Tracking               | ✅          |
| Category Filtering            | ✅          |
| Month-wise Filtering          | ✅          |
| Keyword Search                | ✅          |
| Category Budget Setting       | ✅          |
| Budget vs Spent Tracking      | ✅          |
| Budget Alert (SAFE/WARNING/EXCEEDED) | ✅   |
| Monthly Analytics             | ✅          |
| Daily Expense Chart Data      | ✅          |
| Category Breakdown            | ✅          |
| Yearly Monthly Trend          | ✅          |
| CORS for Frontend Integration | ✅          |
