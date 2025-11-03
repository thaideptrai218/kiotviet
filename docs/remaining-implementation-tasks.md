# Remaining Implementation Tasks - KiotViet Product Management System

## 🎯 **Overview**
This document outlines the **5 remaining tasks** needed to complete the KiotViet Product Management System. All foundational components are complete and ready for the next development phase.

---

## 📋 **Task 1: Service Layer Implementation**
**Priority**: HIGH | **Estimated**: 2-3 days

### **Service Classes to Create:**
```java
// Interfaces and implementations needed:
src/main/java/fa/training/kiotviet/service/
├── UserService.java + impl/UserServiceImpl.java
├── ProductService.java + impl/ProductServiceImpl.java
├── CategoryService.java + impl/CategoryServiceImpl.java
├── CustomerService.java + impl/CustomerServiceImpl.java
├── OrderService.java + impl/OrderServiceImpl.java
└── InventoryService.java + impl/InventoryServiceImpl.java
```

### **Key Business Logic:**
- Order number generation using `GeneratorUtil.generateOrderNumber()`
- Customer code generation using `GeneratorUtil.generateCustomerCode()`
- SKU generation using `GeneratorUtil.generateSKU()`
- Inventory stock calculations and transaction management
- Price calculations with tax and discounts
- Loyalty points accumulation and redemption
- Credit limit validation for customers

### **Dependencies Already Available:**
✅ Repository interfaces in `repository/` package
✅ Exception hierarchy in `exception/` package
✅ Utility classes in `util/` package
✅ Domain entities in `model/` package

---

## 📋 **Task 2: REST API Controllers**
**Priority**: HIGH | **Estimated**: 2-3 days

### **Controllers to Create:**
```java
src/main/java/fa/training/kiotviet/controller/
├── AuthController.java     # /api/v1/auth/*
├── UserController.java     # /api/v1/users/*
├── ProductController.java  # /api/v1/products/*
├── CategoryController.java # /api/v1/categories/*
├── CustomerController.java # /api/v1/customers/*
├── OrderController.java    # /api/v1/orders/*
├── InventoryController.java # /api/v1/inventory/*
└── ReportController.java   # /api/v1/reports/*
```

### **API Features to Implement:**
- CRUD operations for all entities
- Search and filtering with pagination
- File upload for product images
- Bulk operations (import/export)
- Business logic endpoints (process order, adjust inventory)

### **Response Format:**
Use `ApiResponse<T>` wrapper from `dto/ApiResponse.java` for consistent responses.

---

## 📋 **Task 3: Authentication and Security**
**Priority**: HIGH | **Estimated**: 2 days

### **Security Components to Create:**
```java
src/main/java/fa/training/kiotviet/security/
├── SecurityConfig.java           # Main security configuration
├── JwtAuthenticationFilter.java  # JWT token filter
├── JwtTokenProvider.java         # Token generation/validation
└── CustomUserDetailsService.java # User details service
```

### **Authentication Features:**
- JWT token-based authentication
- BCrypt password encoding
- Role-based authorization (ADMIN, MANAGER, STAFF, USER)
- Method-level security with `@PreAuthorize`
- Login/logout and token refresh endpoints
- Password reset functionality

### **User Roles Already Defined:**
✅ `UserRole` enum in `enums/UserRole.java`

---

## 📋 **Task 4: Frontend Views and Templates**
**Priority**: MEDIUM | **Estimated**: 3-4 days

### **Template Structure:**
```html
src/main/resources/templates/
├── layout/
│   ├── base.html      # Main layout template
│   ├── header.html    # Navigation header
│   └── footer.html    # Page footer
├── auth/
│   ├── login.html     # Login page
│   └── register.html  # User registration
├── dashboard/
│   └── dashboard.html # Main dashboard
├── product/
│   ├── product-list.html    # Product listing
│   └── product-form.html    # Product create/edit
├── customer/
│   ├── customer-list.html   # Customer listing
│   └── customer-form.html   # Customer create/edit
├── order/
│   ├── order-list.html      # Order listing
│   └── order-form.html      # Order creation
└── inventory/
    ├── inventory-list.html  # Inventory status
    └── transaction-form.html # Stock adjustments
```

### **UI Features:**
- Responsive design with Bootstrap
- Data tables with pagination and sorting
- Form validation and error handling
- Modal dialogs for confirmations
- Search and filtering interfaces
- File upload for product images

---

## 📋 **Task 5: Advanced Validation and Exception Handling**
**Priority**: MEDIUM | **Estimated**: 1-2 days

### **Validation Components:**
```java
src/main/java/fa/training/kiotviet/
├── dto/request/           # Request DTOs with validation
├── dto/response/          # Response DTOs
├── validation/            # Custom validators
└── exception/
    └── GlobalExceptionHandler.java # Global exception handling
```

### **Validation Features:**
- Bean Validation annotations (@Valid, @NotNull, etc.)
- Custom validation constraints
- Business rule validation
- Global exception handling with @ControllerAdvice
- User-friendly error messages

---

## 🎯 **Implementation Guidelines for Future Claude Sessions**

### **Always Reference These Files:**
1. **`CLAUDE.md`** - Complete project memory context
2. **Business Rules** - Documented in CLAUDE.md sections
3. **Entity Relationships** - Defined in `model/` package
4. **Repository Queries** - Available in `repository/` interfaces
5. **API Standards** - Response format in `dto/ApiResponse.java`

### **Development Approach:**
1. **Start with Service Layer** - Implement business logic first
2. **Add Controllers** - Create REST endpoints using services
3. **Implement Security** - Add authentication and authorization
4. **Build Frontend** - Create user interface with Thymeleaf
5. **Add Validation** - Implement comprehensive validation

### **Code Patterns to Follow:**
- Use `@Service` annotation for service classes
- Use `@Transactional` for data consistency
- Use `ApiResponse<T>` for all API responses
- Use custom exceptions from `exception/` package
- Use utility methods from `util/` package

---

## 🚀 **Quick Start for Next Development Session**

1. **Checkout this branch**: `git checkout feature/remaining-implementation-tasks`
2. **Read context**: Review `CLAUDE.md` for complete project understanding
3. **Start with Task 1**: Begin service layer implementation
4. **Follow patterns**: Use existing code as templates
5. **Test frequently**: Run tests after each component
6. **Commit progress**: Make small, focused commits

---

## 📊 **Current Foundation Status**

### ✅ **Completed (Ready to Use):**
- **Domain Entities**: All 7 core entities with relationships
- **Repository Layer**: 7 repositories with custom queries
- **Exception Hierarchy**: Base, ResourceNotFound, BusinessRule exceptions
- **Utility Classes**: AppConstants, GeneratorUtil for ID generation
- **Configuration**: YAML-first approach with minimal Java config
- **Docker Environment**: Complete development setup with MySQL, Redis
- **API Response Format**: Standardized ApiResponse wrapper
- **Documentation**: Complete memory context in CLAUDE.md

### 🔄 **Dependencies Established:**
- All repositories are ready for service layer
- Exception handling framework is in place
- Utility methods for business logic are available
- Database schema is defined and configured
- Development environment is containerized and ready

---

## 🎯 **Success Criteria**

Each task is complete when:
- ✅ All functional requirements implemented
- ✅ Unit tests pass (>80% coverage)
- ✅ Integration tests verify functionality
- ✅ Code follows established patterns
- ✅ Security requirements met
- ✅ Performance is acceptable

---

**This PR serves as a comprehensive guide for completing the remaining implementation tasks. Future Claude sessions should reference this document and the main CLAUDE.md file for complete project context.**

---

*Created: November 2024*
*Branch: feature/remaining-implementation-tasks*
*Next: Start with Service Layer Implementation*