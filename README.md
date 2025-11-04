# KiotViet Product Management System

**hello world**

A comprehensive product management system inspired by KiotViet, built for university project purposes using Spring Boot and modern technologies.

## 🚀 Features

- **Product Management**: Create, update, and manage products with categories
- **Inventory Tracking**: Real-time inventory monitoring and transactions
- **Customer Management**: Complete customer lifecycle management
- **Order Processing**: Sales order creation and management
- **User Authentication**: Role-based access control (Admin, Manager, Staff, User)
- **Reporting**: Sales and inventory reports
- **Responsive Design**: Modern web interface using Thymeleaf

## 🛠 Technology Stack

- **Backend**: Spring Boot 3.5.7, Java 17
- **Database**: MySQL 8.0
- **Cache**: Redis
- **Security**: Spring Security
- **Template Engine**: Thymeleaf
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose
- **Utilities**: Lombok, Spring Validation

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- Java 17+
- Maven 3.6+
- Docker & Docker Compose
- Github

## 🚀 Quick Start

### Using Docker (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd kiotviet
   ```

2. **Setup the environment**
   ```bash
   make setup
   # or
   ./scripts/setup.sh
   ```

3. **Start all services**
   ```bash
   make start
   # or
   docker-compose up -d
   ```

4. **Access the application**
   - Application: http://localhost:8080
   - Database Admin: http://localhost:8081 (Adminer)
   - Redis Commander: http://localhost:8082

### Local Development

1. **Install dependencies**
   ```bash
   mvn clean install
   ```

2. **Start MySQL locally** (or use Docker)
   ```bash
   docker-compose up -d mysql
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

## 📚 Available Commands

### Development Commands

```bash
# Start all services
make start

# Stop all services
make stop

# Restart services
make restart

# View logs
make logs

# Rebuild application
make rebuild

# Run tests
make test

# Connect to database
make db

# Show service status
make status

# Create database backup
make backup

# Restore database
make restore FILE=backup_file.sql
```

### Local Development

```bash
# Run application locally
make local-run

# Run tests locally
make local-test

# Build locally
make local-build
```

### Git Workflow

```bash
# Add all changes
make git-add

# Commit with message
make git-commit MSG="your commit message"

# Push to remote
make git-push

# View status
make git-status

# View recent commits
make git-log
```

## 🗂 Project Structure

```
kiotviet/
├── src/
│   └── main/
│       ├── java/fa/training/kiotviet/
│       │   ├── config/          # Configuration classes
│       │   ├── controller/      # REST Controllers
│       │   ├── dto/            # Data Transfer Objects
│       │   ├── enums/          # Enums
│       │   ├── exception/      # Custom exceptions
│       │   ├── model/          # Domain entities
│       │   ├── repository/     # JPA repositories
│       │   ├── security/       # Security configuration
│       │   ├── service/        # Business logic
│       │   └── util/           # Utility classes
│       └── resources/
│           ├── static/         # CSS, JS, images
│           ├── templates/      # Thymeleaf templates
│           ├── application.yml # Application configuration
│           └── application-docker.yml # Docker-specific config
├── docker/                    # Docker configurations
├── scripts/                   # Bash scripts
├── docs/                     # Documentation
├── uploads/                  # File uploads
├── logs/                     # Application logs
├── backups/                  # Database backups
├── docker-compose.yml        # Docker Compose configuration
├── docker-compose.override.yml # Development overrides
├── Dockerfile               # Application Docker image
├── Makefile                 # Development commands
└── README.md               # This file
```

## 🐳 Docker Services

The Docker Compose setup includes:

- **MySQL**: Primary database server
- **Redis**: Caching and session storage
- **Application**: Spring Boot application
- **Adminer**: Database management tool (development only)
- **Redis Commander**: Redis management tool (development only)

## 🔧 Configuration

### Environment Variables

Key environment variables that can be configured:

- `MYSQL_ROOT_PASSWORD`: MySQL root password
- `MYSQL_DATABASE`: Database name
- `MYSQL_USER`: Database user
- `MYSQL_PASSWORD`: Database password
- `SPRING_PROFILES_ACTIVE`: Spring profile to use
- `JWT_SECRET`: JWT signing secret

### Application Profiles

- `local`: Local development without Docker
- `dev`: Development environment with Docker
- `docker`: Production-ready Docker environment

## 📊 Database Schema

The system uses the following main entities:

- **Users**: Application users with role-based access
- **Categories**: Product categories with hierarchical structure
- **Products**: Product catalog with inventory tracking
- **Customers**: Customer management with loyalty points
- **Orders**: Sales orders with items
- **Inventory Transactions**: Stock movement tracking

## 🧪 Testing

```bash
# Run all tests
make test

# Run tests with coverage
./scripts/test.sh coverage

# Run specific test class
./scripts/test.sh class UserServiceTest

# Run specific test method
./scripts/test.sh method UserServiceTest#testCreateUser
```

## 📝 Development Guidelines

1. **Code Style**: Follow Java naming conventions
2. **Commits**: Use clear, descriptive commit messages
3. **Testing**: Write unit tests for all business logic
4. **Documentation**: Update README and inline comments
5. **Security**: Never commit sensitive data

## 🚀 Deployment

For production deployment:

1. Update environment variables in `.env`
2. Use production Docker Compose configuration
3. Set up proper SSL certificates
4. Configure reverse proxy (nginx)
5. Set up database backups
6. Monitor application health

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is for educational purposes only.

## 🆘 Support

If you encounter any issues:

1. Check the logs: `docker-compose logs app`
2. Verify service status: `make status`
3. Check configuration files
4. Review Docker setup

## 📈 Roadmap

- [ ] Complete repository layer implementation
- [ ] Implement service layer with business logic
- [ ] Create REST API controllers
- [ ] Set up authentication and authorization
- [ ] Build frontend interface
- [ ] Add reporting and analytics
- [ ] Implement API documentation
- [ ] Add comprehensive testing
- [ ] Performance optimization
- [ ] Production deployment guide