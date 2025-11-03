# Claude Memory Context - KiotViet Product Management System

## 📋 Project Overview
**Project**: KiotViet-like Product Management System
**Purpose**: University project demonstrating full-stack Java development
**Architecture**: Monolithic Spring Boot application
**Database**: MySQL with JPA/Hibernate
**Deployment**: Docker containerized development environment

---

## 🏗️ **Architecture & Structure**

### **Technology Stack**
- **Backend**: Spring Boot 3.5.7, Java 17
- **Database**: MySQL 8.0 with connection pooling
- **Cache**: Redis (optional for caching)
- **Security**: Spring Security with JWT
- **Template Engine**: Thymeleaf
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose
- **Utilities**: Lombok, Validation

### **Package Structure**
```
fa.training.kiotviet/
├── config/          # Minimal Java config (YAML-first approach)
├── controller/      # REST API endpoints
├── dto/            # Data Transfer Objects & API responses
├── enums/          # Business enums (UserRole, ProductStatus, OrderStatus)
├── exception/      # Custom exception hierarchy
├── model/          # JPA entities with relationships
├── repository/     # Spring Data JPA repositories
├── security/       # Security configuration
├── service/        # Business logic layer
└── util/           # Utility classes & generators
```

### **Configuration Philosophy**
- **YAML-first**: Configure in application.yml when possible
- **Java-only**: Only when programmatic configuration is necessary
- **Environment profiles**: local, dev, docker profiles
- **Connection pooling**: HikariCP with optimized settings

---

## 🗄️ **Database Schema & Entities**

### **Core Domain Entities**
1. **User** - Authentication & role-based access
   - Implements UserDetails for Spring Security
   - Roles: ADMIN, MANAGER, STAFF, USER
   - Audit fields with @CreatedDate/@LastModifiedDate

2. **Category** - Hierarchical product categories
   - Self-referencing parent-child relationships
   - Sort order for display ordering
   - Active/inactive status management

3. **Product** - Product catalog with inventory
   - SKU and barcode tracking
   - Price management (cost, sale, retail)
   - Inventory threshold settings
   - Tax and weight information

4. **Customer** - Customer management
   - Unique customer codes (KH######)
   - Loyalty points system
   - Credit limit and balance tracking

5. **Order** - Sales order management
   - Unique order numbers (HDyyyyMMdd####)
   - Order status workflow
   - Payment and shipping tracking
   - Multiple item support via OrderItem

6. **OrderItem** - Order line items
   - Quantity and pricing
   - Discount calculations
   - Product references

7. **InventoryTransaction** - Stock movement
   - Transaction types: IN, OUT, ADJUSTMENT, RETURN
   - Reference tracking (purchase, sale, etc.)
   - Cost calculations

### **Database Configuration**
- **Schema**: `kiotviet_db`
- **Charset**: utf8mb4 with utf8mb4_unicode_ci collation
- **Engine**: InnoDB with foreign key constraints
- **Audit**: JPA auditing enabled with @CreatedBy/@LastModifiedBy

---

## 🔧 **Development Environment**

### **Docker Services**
- **MySQL**: Port 3306, persistent volumes
- **Redis**: Port 6379, optional caching
- **Application**: Port 8080, health checks
- **Adminer**: Port 8081, database admin UI
- **Redis Commander**: Port 8082, Redis admin UI

### **Development Commands**
```bash
# Complete setup
make setup

# Daily operations
make start|stop|restart|logs|rebuild|test

# Database operations
make db|backup|restore FILE=backup.sql

# Local development
make local-run|local-test|local-build
```

### **Environment Profiles**
- **local**: Local development without Docker
- **dev**: Development with Docker containers
- **docker**: Production-ready Docker configuration

---

## 🎯 **Business Logic & Rules**

### **Product Management**
- SKU generation: SKU + YYMM + 4-digit sequence
- Category hierarchy support
- Price calculation with tax
- Inventory threshold alerts
- Product status lifecycle

### **Order Processing**
- Order number generation: HD + yyyyMMdd + 4-digit sequence
- Status workflow: PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED
- Automatic total calculations
- Payment tracking
- Customer assignment

### **Customer Management**
- Customer code generation: KH + 6-digit sequence
- Loyalty points accumulation
- Credit limit enforcement
- Order history tracking

### **Inventory Management**
- Transaction types for different operations
- Current stock calculation
- Low stock alerts
- Cost tracking per transaction

---

## 🔐 **Security Implementation**

### **Authentication**
- Spring Security with UserDetails implementation
- Password encoding with BCrypt
- JWT token support (configurable)
- Role-based authorization

### **User Roles**
- **ADMIN**: Full system access
- **MANAGER**: Store operations management
- **STAFF**: Sales operations
- **USER**: Limited access

### **Security Configuration**
- CORS configuration for API access
- Session management
- Password policies
- API endpoint protection

---

## 📊 **API Design Standards**

### **Response Format**
```json
{
  "success": true,
  "data": {...},
  "message": "Operation completed successfully",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### **Error Handling**
```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Error description",
    "details": "Additional details"
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### **Pagination**
```json
{
  "success": true,
  "data": {
    "content": [...],
    "page": {
      "number": 0,
      "size": 20,
      "totalElements": 100,
      "totalPages": 5
    }
  }
}
```

---

## 🛠 **Development Guidelines**

### **Code Standards**
- **Java 17** features and conventions
- **Lombok** for reducing boilerplate
- **Spring Boot** best practices
- **JPA** entity relationships and auditing
- **REST API** design principles

### **Database Practices**
- **Foreign key constraints** for data integrity
- **Indexing** for performance
- **Connection pooling** with HikariCP
- **Batch operations** for bulk data
- **Transaction management** with @Transactional

### **Testing Strategy**
- **Unit tests** for business logic
- **Integration tests** for repository layer
- **API tests** for REST endpoints
- **Test data management** with fixtures

---

## 📁 **File Structure Reference**

### **Configuration Files**
- `application.yml` - Main configuration
- `application-docker.yml` - Docker-specific config
- `application-local.yml` - Local development config

### **Docker Files**
- `Dockerfile` - Multi-stage build
- `docker-compose.yml` - Main services
- `docker-compose.override.yml` - Development tools
- `.dockerignore` - Build optimizations

### **Scripts**
- `scripts/setup.sh` - Environment initialization
- `scripts/dev.sh` - Development commands
- `scripts/test.sh` - Test automation
- `Makefile` - Command shortcuts

### **Database Scripts**
- `docker/mysql/init/01-init-database.sql` - Initial data
- `docker/mysql/conf/my.cnf` - MySQL configuration

---

## 🚀 **Current Implementation Status**

### ✅ **Completed**
- [x] Project structure and configuration
- [x] Docker development environment
- [x] Domain entities and relationships
- [x] Repository layer with custom queries
- [x] Exception hierarchy
- [x] Utility classes and generators
- [x] Base DTO structure
- [x] YAML-first configuration approach

### 🔄 **In Progress**
- [ ] Service layer implementation
- [ ] REST API controllers
- [ ] Authentication and security
- [ ] Frontend templates

### ⏳ **Pending**
- [ ] Validation framework
- [ ] Reporting and analytics
- [ ] File upload handling
- [ ] Email notifications
- [ ] API documentation
- [ ] Performance optimization

---

## 🎯 **Next Implementation Steps**

1. **Service Layer** - Implement business logic interfaces and implementations
2. **Controllers** - Create REST API endpoints following established patterns
3. **Security** - Implement authentication and authorization
4. **Validation** - Add input validation and business rule enforcement
5. **Frontend** - Create Thymeleaf templates and static resources

---

## 📝 **Important Notes**

- **Database**: Ensure MySQL is running before starting application
- **Profiles**: Use appropriate Spring profile for environment
- **Testing**: Run tests before commits to ensure functionality
- **Security**: Never commit secrets or sensitive data
- **Performance**: Monitor query performance and add indexes as needed

## 🔗 **Useful References**

- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **JPA Documentation**: https://spring.io/projects/spring-data-jpa
- **MySQL Configuration**: https://dev.mysql.com/doc/
- **Docker Compose**: https://docs.docker.com/compose/

---

*Last Updated: $(date)*
*Project Version: 1.0.0*