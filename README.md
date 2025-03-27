# Payment Operations Simulation - Microservices Architecture With JWT and Web MVC

## Overview
This application is a simulation of payment operations and serves as an example of a microservices architecture that operates using data transmission through RabbitMQ message queues. Each individual service has its own database and adaptive models for interaction. The Eureka registration and discovery service acts as the central registry, connecting all services together. The application has Spring Security using JWT (JSON Web Token), designed to verify the user on the API Gateway service side, which receives user data from the USER-SERVICE service (this is the only service that also has Spring Security settings, all others are designed to prohibit direct access to their endpoints). A special point of this application is the use of API Gateway using Spring Web MVC without asynchronous data transfer.

## Responsibilities

### **user-service**
- Creates users and stores their data.
- Has a relationship with **wallet-service**, to which it sends data via a queue for the creation of a new wallet.

### **wallet-service**
- Creates wallets and allows balance top-ups or expense transactions.
- Receives data from **user-service** and creates a wallet based on this information.
- Interacts with **expenses-service** and **journal-service**, sending relevant data via queues.

### **expenses-service**
- Manages expense records.
- Receives data from **wallet-service**, processes it, and creates a corresponding record in its database.

### **journal-service**
- Monitors user account data.
- Receives data from **wallet-service** and creates records in its own database.
- Allows tracking of fund movements.

## **Technology Stack**
- **Spring Cloud** - Used to manage microservices communication.
- **RabbitMQ** - Message broker for data exchange between services.
- **Eureka Server** - Service registry and discovery system.
- **API Gateway** - Central entry point for managing service requests (without ASYNCH, on Spring Web MVC).

## **Scalability & Management**
This project demonstrates the capabilities of **Spring Cloud** for applications with a moderate number of microservices (up to approximately 50 units). It includes:
- A **single entry point** via **API Gateway**.
- **Eureka service registry**, enabling easy scaling and efficient management of separate modules (services).

## **Postman Endpoints Testing**

### 1. USER-SERVICE
**addNewUser():**
POST `http://localhost:8081/api/v1/users/add`
**Headers:**
```
Authorization: Bearer <your_token_here>
```
**Body -> raw:**
```json
{
  "username": "UserOne",
  "firstName": "User1",
  "lastName": "User1",
  "email": "user1@example.com",
  "phone": "111111"
}
```

**getUserById(userId):**
GET `http://localhost:8081/api/v1/users/1996395b-6e65-40a3-8d87-4e6ac3549f5e`
**Headers:**
```
Authorization: Bearer <your_token_here>
```

**getAll():**
GET `http://localhost:8081/api/v1/users/all`
**Headers:**
```
Authorization: Bearer <your_token_here>
```

### 2. WALLET-SERVICE
**getAll():**
GET `http://localhost:8082/api/v1/wallets/all`
**Headers:**
```
Authorization: Bearer <your_token_here>
```

**getWalletById():**
GET `http://localhost:8082/api/v1/wallets/3b15ecb4-815b-4d1b-9cdd-b63854a1fe21`
**Headers:**
```
Authorization: Bearer <your_token_here>
```

**getBalanceByWalletId():**
GET `http://localhost:8082/api/v1/wallets/3b15ecb4-815b-4d1b-9cdd-b63854a1fe21/balance`
**Headers:**
```
Authorization: Bearer <your_token_here>
```

**replenishBalance():**
POST `http://localhost:8082/api/v1/wallets/up-balance`
**Headers:**
```
Authorization: Bearer <your_token_here>
```
**Body -> raw:**
```json
{
  "userId": "3b15ecb4-815b-4d1b-9cdd-b63854a1fe21",
  "amount": 1004
}
```

**deductBalance():**
PATCH `http://localhost:8082/api/v1/wallets/deduct-balance`
**Headers:**
```
Authorization: Bearer <your_token_here>
```
**Body -> raw:**
```json
{
  "userId": "3b15ecb4-815b-4d1b-9cdd-b63854a1fe21",
  "amount": 155
}
```

### 3. EXPENSES-SERVICE
**getAllExpensesByUserId():**
GET `http://localhost:8084/api/v1/expenses/3b15ecb4-815b-4d1b-9cdd-b63854a1fe21`
**Headers:**
```
Authorization: Bearer <your_token_here>
```

### 4. JOURNAL-SERVICE
**getAllEntries():**
GET `http://localhost:8083/api/v1/journal/all`
**Headers:**
```
Authorization: Bearer <your_token_here>
```

**getAllUserJournalEntries():**
GET `http://localhost:8083/api/v1/journal/3b15ecb4-815b-4d1b-9cdd-b63854a1fe21`
**Headers:**
```
Authorization: Bearer <your_token_here>
```


