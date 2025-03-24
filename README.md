# Online Food Ordering System  

This is a microservices-based online food ordering system built using **Spring Boot**, **Spring Cloud**, and **Docker**. The project handles user authentication, restaurant management, order processing, payments, and delivery tracking.  

## Features  

- User authentication and management  
- Restaurant CRUD operations  
- Order creation and tracking  
- Payment processing  
- Delivery assignment and tracking  
- Email notifications for order updates  
- API Gateway for routing requests  
- Service Discovery using Eureka  

## Technologies Used  

- **Backend:** Java, Spring Boot, Spring Cloud, Microservices  
- **Database:** MySQL  
- **Messaging:** Apache Kafka  
- **Security:** Spring Security, JWT Authentication  
- **Containerization:** Docker, Docker Compose  
- **API Gateway:** Spring Cloud Gateway  
- **Service Discovery:** Eureka Server  

## Microservices Structure  

```
online-food-service/
│── eureka-server/          # Service Discovery
│── api-gateway/            # API Gateway
│── user-service/           # Manages users and authentication
│── restaurant-service/     # Manages restaurants and menus
│── order-service/          # Handles orders and status updates
│── payment-service/        # Processes payments
│── delivery-service/       # Assigns deliveries and tracks them
│── email-service/          # Sends email notifications
│── mysql-db/               # MySQL database setup
│── docker-compose.yaml     # Docker configuration
│── README.md               # Project documentation
```

## How to Run  

### 1. Clone the Repository  

```bash
git clone https://github.com/Vijay214271/online-food-service.git
cd online-food-service
```

### 2. Run with Docker Compose  

```bash
docker-compose up --build
```

### 3. Access the Services  

- Eureka Server (Service Discovery): `http://localhost:8761/`  
- API Gateway (Routes requests to microservices): `http://localhost:8080/`  
- User Service: `http://localhost:8081/users`  
- Restaurant Service: `http://localhost:8082/restaurants`  
- Order Service: `http://localhost:8083/orders`  
- Payment Service: `http://localhost:8084/payments`  
- Delivery Service: `http://localhost:8085/deliveries`  
- Email Service: `http://localhost:8086/emails`  

## Testing the APIs  

Use Postman or cURL to test the APIs.  

Example request to register a user:  

```bash
curl -X POST "http://localhost:8081/users/register" -H "Content-Type: application/json" -d '{"username":"testuser", "password":"123456"}'
```
