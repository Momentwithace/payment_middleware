# Middleware for Digital-Only Fintech - README

This project involves the development and testing of a middleware solution to power a digital-only fintech company. The middleware is designed using a micro-service architecture and provides essential functionalities to support various digital channels, such as Mobile, Web, and USSD.

---

## Table of Contents
1. [Features](#features)
2. [Architecture Overview](#architecture-overview)
3. [Tech Stack](#tech-stack)
4. [Project Structure](#project-structure)
5. [Setup and Installation](#setup-and-installation)
6. [Usage](#usage)
7. Contributing.

---

## Features

The middleware provides the following core functionalities:

1. **Customer Onboarding**:
   - Captures customer details such as BVN and/or NIN.
   - Validates ownership of BVN and/or NIN.
   - Creates an account for the customer upon successful onboarding.

2. **Login**:
   - Secure authentication mechanism for customers.

3. **Dashboard**:
   - Retrieves customer account details, including balances.
   - Provides additional dashboard-related data.

4. **Bills Payment**:
   - Lists bill categories, billers, and products.
   - Enables customers to submit payments.

5. **Funds Transfer**:
   - Provides a list of banks.
   - Allows customers to initiate and submit transfers.

---

## Architecture Overview

The middleware is built using a micro-service architecture and includes the following infrastructure services:

1. **Discovery Service**:
   - Ensures seamless communication between microservices.

2. **API Gateway** (using Spring Cloud):
   - Handles routing, load balancing, and security.

3. **Configuration Server**:
   - Centralizes configuration management for all services.

Each micro-service is independent and responsible for a specific domain functionality, ensuring scalability and maintainability.

---

## Tech Stack

- **Programming Language**: Java (version 11 or higher)
- **Framework**: Spring Boot (version 3.x)
- **Infrastructure Tools**:
  - Spring Cloud for API Gateway and Discovery Service
  - Spring Cloud Config for centralized configuration management
- **Build Tool**: Maven
- **Database**: H2Database
- **Security**: Spring Security, OAuth2/JWT

---

## Project Structure

```
|-- account
|-- api-gateway
|-- auth
|-- config-server
|-- payment
|-- service-registry
|-- shared-libraries
```

---

## Setup and Installation

### Prerequisites:
- Java 11 or higher
- Maven

### Steps:
1. Clone the repository:
   - Project Link: https://github.com/Momentwithace/payment_middleware.git
   

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Start each micro-service:
   ```bash
   cd service-name
   mvn spring-boot:run
   ```

4. Run the discovery service, follow by the config server and API gateway first to ensure proper communication between microservices.

5. Access to API documentation:
   - Postman: https://documenter.getpostman.com/view/21740517/2sAYQUrEnt

---

## Usage

### API Endpoints:
1. **Onboarding**
   - `POST /register`: Register new customers.
   - `POST /verifyNin`: Validate NIN.
   - `POST /verifyBvn`: Validate BVN.
    
2. **Login**
   - `POST /auth/login`: Authenticate customers.

3. **Dashboard**
   - `GET /getUserDeatils`: Retrieve customer account and balance.

4. **Bills Payment**
   - `GET /bills/categories`: List bill categories.
   - `POST /bills/pay`: Submit a payment.
   - `GET /getBillers`: Fetch Billers.
   - `GET /banks`: Fetch Banks.

5. **Funds Transfer**
   - `GET /transfer/banks`: List available banks.
   - `POST /transfer`: Submit a transfer.

---

## Contributing

Contributions are welcome! Please follow the guidelines below:
1. Fork the repository.
2. Create a feature branch: `git checkout -b feature-name`.
3. Commit changes: `git commit -m "Feature description"`.
4. Push to the branch: `git push origin feature-name`.
5. Open a Pull Request.

---

