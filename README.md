# Asteroid Collision Notification Service

A microservices-based application that monitors potentially hazardous asteroids using NASA's Near Earth Object (NEO) API and sends email notifications about upcoming close approaches.

## 📋 Overview

This project consists of two Spring Boot microservices that work together to provide real-time asteroid collision alerts:

1. **Asteroid Alerting Service** - Fetches data from NASA's NEO API and identifies potentially hazardous asteroids
2. **Notification Service** - Consumes asteroid alerts and sends email notifications to users

The services communicate asynchronously using Apache Kafka for reliable message delivery.

## 🏗️ Architecture

```
┌─────────────────────────────────┐
│     NASA NEO API                │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│  Asteroid Alerting Service      │
│  (Port 8080)                    │
│  - Fetches asteroid data        │
│  - Filters hazardous asteroids  │
│  - Publishes to Kafka           │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│     Apache Kafka                │
│     Topic: asteroid-alerts      │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│  Notification Service           │
│  (Port 8081)                    │
│  - Consumes Kafka events        │
│  - Stores notifications in DB   │
│  - Sends email alerts           │
└─────────────────────────────────┘
```

## 🚀 Features

- **Real-time Asteroid Monitoring**: Fetches asteroid data from NASA's NEO API for the next 7 days
- **Hazard Detection**: Automatically identifies potentially hazardous asteroids
- **Event-Driven Architecture**: Uses Kafka for reliable async communication
- **Email Notifications**: Sends detailed email alerts about dangerous asteroids
- **Data Persistence**: Stores notification history in MySQL database
- **Scheduled Processing**: Automatically sends batched email notifications every 10 seconds
- **Docker Support**: Complete Docker Compose setup for all infrastructure components

## 🛠️ Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.4.12**
- **Spring Web** - REST API endpoints
- **Spring Kafka** - Event streaming
- **Spring Data JPA** - Database access
- **Spring Mail** - Email functionality
- **Lombok** - Boilerplate code reduction

### Infrastructure
- **Apache Kafka 7.5.0** - Message broker
- **Apache Zookeeper** - Kafka coordination
- **MySQL 8.3.0** - Database
- **Kafka Schema Registry** - Schema management
- **Kafka UI** - Kafka monitoring and management

### Build Tool
- **Maven** - Dependency management and build automation

## 📦 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- NASA API Key (default uses DEMO_KEY)

## 🔧 Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Asteroid-Collision-Notification-Service
```

### 2. Start Infrastructure Services

Navigate to the asteroidalerting directory and start Docker services:

```bash
cd asteroidalerting
docker-compose up -d
```

This will start:
- MySQL (Port 3306)
- Zookeeper (Port 2181)
- Kafka Broker (Port 9092)
- Kafka UI (Port 8084)
- Kafka Schema Registry (Port 8083)

### 3. Configure Application Properties

#### Asteroid Alerting Service
Update `asteroidalerting/src/main/resources/application.properties`:

```properties
# NASA API Configuration
nasa.api.key=YOUR_NASA_API_KEY  # Get from https://api.nasa.gov
nasa.neo.api.url=https://api.nasa.gov/neo/rest/v1/feed

# Kafka Configuration
spring.kafka.bootstrap-servers=localhost:9092
```

#### Notification Service
Update `notificationservice/src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/asteroidalerting
spring.datasource.username=root
spring.datasource.password=password

# Email Configuration (MailTrap or your SMTP server)
spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=YOUR_MAILTRAP_USERNAME
spring.mail.password=YOUR_MAILTRAP_PASSWORD
email.service.from.email=your-email@example.com
```

### 4. Build the Services

Build both services using Maven:

```bash
# Build Asteroid Alerting Service
cd asteroidalerting
./mvnw clean install

# Build Notification Service
cd ../notificationservice
./mvnw clean install
```

### 5. Run the Services

Start both services in separate terminal windows:

```bash
# Terminal 1 - Asteroid Alerting Service
cd asteroidalerting
./mvnw spring-boot:run

# Terminal 2 - Notification Service
cd notificationservice
./mvnw spring-boot:run
```

## 📖 Usage

### Trigger Asteroid Alert Check

Send a POST request to trigger the asteroid monitoring:

```bash
curl -X POST http://localhost:8080/api/v1/asteroid-altering/alert
```

This will:
1. Fetch asteroid data from NASA API for the next 7 days
2. Filter potentially hazardous asteroids
3. Publish alerts to Kafka topic `asteroid-alerts`
4. Notification service consumes the alerts
5. Alerts are stored in MySQL database
6. Emails are sent every 10 seconds for unsent notifications

### Monitor Kafka Messages

Access Kafka UI at: `http://localhost:8084`

You can view:
- Topic messages
- Consumer groups
- Broker health
- Schema registry

## 📊 Data Flow

1. **API Call**: POST to `/api/v1/asteroid-altering/alert`
2. **NASA API**: Service fetches NEO data for next 7 days
3. **Filtering**: Identifies potentially hazardous asteroids
4. **Event Publishing**: Publishes `AsteroidCollisionEvent` to Kafka
5. **Event Consumption**: Notification service receives events
6. **Persistence**: Saves notifications to MySQL
7. **Email Scheduling**: Scheduled job sends emails every 10 seconds
8. **Email Delivery**: Sends detailed asteroid information via email

## 📝 API Endpoints

### Asteroid Alerting Service (Port 8080)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/asteroid-altering/alert` | Trigger asteroid monitoring and alerting |

## 🗄️ Database Schema

The notification service uses MySQL with the following schema:

### Notification Table
- `id` - Primary key
- `asteroid_name` - Name of the asteroid
- `close_approach_date` - Date of closest approach to Earth
- `estimated_diameter_avg_meters` - Average estimated diameter in meters
- `miss_distance_kilometers` - Distance from Earth in kilometers
- `email_sent` - Boolean flag indicating if email was sent
- `created_at` - Timestamp of record creation

## 🔒 Environment Variables

### Asteroid Alerting Service
- `NASA_API_KEY` - NASA API key
- `KAFKA_BOOTSTRAP_SERVERS` - Kafka broker address

### Notification Service
- `SPRING_DATASOURCE_URL` - MySQL connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `SPRING_MAIL_HOST` - SMTP server host
- `SPRING_MAIL_PORT` - SMTP server port
- `SPRING_MAIL_USERNAME` - Email service username
- `SPRING_MAIL_PASSWORD` - Email service password

## 🧪 Testing

Run tests for both services:

```bash
# Test Asteroid Alerting Service
cd asteroidalerting
./mvnw test

# Test Notification Service
cd notificationservice
./mvnw test
```

## 📦 Docker Services

The Docker Compose setup includes:

| Service | Port | Description |
|---------|------|-------------|
| MySQL | 3306 | Database server |
| Zookeeper | 2181 | Kafka coordination service |
| Kafka Broker | 9092, 29092 | Message broker |
| Kafka UI | 8084 | Web UI for Kafka management |
| Kafka Schema Registry | 8083 | Schema management |

## 🐛 Troubleshooting

### Kafka Connection Issues
```bash
# Check if Kafka is running
docker ps | grep kafka

# Restart Kafka services
docker-compose restart kafka-broker zookeeper
```

### Database Connection Issues
```bash
# Check MySQL logs
docker logs mysql

# Verify database exists
docker exec -it mysql mysql -u root -p
USE asteroidalerting;
```

### Email Not Sending
- Verify MailTrap credentials in application.properties
- Check notification service logs for errors
- Ensure `emailSent` flag is being updated correctly

## 📄 License

This project is available under the MIT License.


## 🔗 Resources

- [NASA NEO API Documentation](https://api.nasa.gov/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Spring Kafka Documentation](https://spring.io/projects/spring-kafka)


---

**Note**: Remember to replace `DEMO_KEY` with your actual NASA API key for production use. The demo key has rate limits and should only be used for testing.

