#!/bin/bash

# KiotViet Development Environment Setup Script
# This script sets up the complete development environment

set -e  # Exit on any error

echo "🚀 Setting up KiotViet Product Management System..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Helper functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check prerequisites
check_prerequisites() {
    log_info "Checking prerequisites..."

    # Check Docker
    if ! command -v docker &> /dev/null; then
        log_error "Docker is not installed. Please install Docker first."
        exit 1
    fi

    # Check Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi

    # Check Git
    if ! command -v git &> /dev/null; then
        log_error "Git is not installed. Please install Git first."
        exit 1
    fi

    # Check Java (for local development)
    if ! command -v java &> /dev/null; then
        log_warning "Java is not installed. You'll need Java 17 for local development."
    fi

    # Check Maven (for local development)
    if ! command -v mvn &> /dev/null; then
        log_warning "Maven is not installed. You'll need Maven for local development."
    fi

    log_success "Prerequisites check completed!"
}

# Create necessary directories
create_directories() {
    log_info "Creating project directories..."

    mkdir -p logs
    mkdir -p uploads
    mkdir -p scripts
    mkdir -p docs
    mkdir -p backups
    mkdir -p docker/mysql/data
    mkdir -p docker/redis/data

    log_success "Directories created!"
}

# Setup environment file
setup_environment() {
    log_info "Setting up environment configuration..."

    if [ ! -f .env ]; then
        cat > .env << EOF
# KiotViet Environment Configuration
# Copy this file and modify as needed

# Database Configuration
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_DATABASE=kiotviet_db
MYSQL_USER=kiotviet_user
MYSQL_PASSWORD=kiotviet_password

# Application Configuration
SPRING_PROFILES_ACTIVE=dev,docker
SERVER_PORT=8080

# JWT Configuration
JWT_SECRET=kiotviet-secret-key-for-university-project-change-in-production
JWT_EXPIRATION=86400000

# File Upload
UPLOAD_DIR=./uploads
MAX_FILE_SIZE=10MB

# Development Tools
ENABLE_DEBUG=true
DEBUG_PORT=5005
EOF
        log_success "Environment file created!"
    else
        log_warning "Environment file already exists. Skipping..."
    fi
}

# Build Docker images
build_docker() {
    log_info "Building Docker images..."

    docker-compose build --no-cache

    log_success "Docker images built successfully!"
}

# Start services
start_services() {
    log_info "Starting development services..."

    # Start database and Redis first
    docker-compose up -d mysql redis

    log_info "Waiting for database to be ready..."
    sleep 30

    # Check if database is ready
    max_attempts=30
    attempt=1

    while [ $attempt -le $max_attempts ]; do
        if docker-compose exec -T mysql mysqladmin ping -h localhost --silent; then
            log_success "Database is ready!"
            break
        fi

        log_info "Waiting for database... (attempt $attempt/$max_attempts)"
        sleep 2
        ((attempt++))
    done

    if [ $attempt -gt $max_attempts ]; then
        log_error "Database failed to start within expected time."
        exit 1
    fi

    # Start the application
    docker-compose up -d app

    log_success "All services started!"
}

# Verify setup
verify_setup() {
    log_info "Verifying setup..."

    # Wait for application to start
    sleep 30

    # Check application health
    if curl -f http://localhost:8080/actuator/health &> /dev/null; then
        log_success "Application is running and healthy!"
    else
        log_warning "Application might not be fully ready yet. Please check logs."
    fi

    # Show service status
    log_info "Service status:"
    docker-compose ps
}

# Show next steps
show_next_steps() {
    log_success "🎉 KiotViet development environment is ready!"

    echo ""
    echo "📋 Next Steps:"
    echo "1. Access the application at: http://localhost:8080"
    echo "2. Database Admin: http://localhost:8081 (Adminer)"
    echo "3. Redis Commander: http://localhost:8082"
    echo "4. Check logs: docker-compose logs -f app"
    echo "5. Stop services: docker-compose down"
    echo "6. View application logs: docker-compose logs app"
    echo ""
    echo "📚 Useful Commands:"
    echo "• Rebuild application: docker-compose up -d --build app"
    echo "• View database: docker-compose exec mysql mysql -u kiotviet_user -p kiotviet_db"
    echo "• Debug mode: Connect to localhost:5005 with your IDE"
    echo "• Reset database: docker-compose down -v && docker-compose up -d mysql"
    echo ""
    echo "🔧 Development:"
    echo "• Run tests: ./scripts/test.sh"
    echo "• Create backup: ./scripts/backup.sh"
    echo "• Deploy: ./scripts/deploy.sh"
}

# Main execution
main() {
    echo "=========================================="
    echo "  KiotViet Product Management System"
    echo "  Development Environment Setup"
    echo "=========================================="
    echo ""

    check_prerequisites
    create_directories
    setup_environment
    build_docker
    start_services
    verify_setup
    show_next_steps

    log_success "Setup completed successfully! 🚀"
}

# Run main function
main "$@"