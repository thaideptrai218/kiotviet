# KiotViet Product Management System Makefile
# Provides convenient commands for development workflow

.PHONY: help setup start stop restart logs clean rebuild test shell db status backup restore

# Default target
help:
	@echo "KiotViet Product Management System"
	@echo "================================="
	@echo ""
	@echo "Development Commands:"
	@echo "  make setup     - Set up development environment"
	@echo "  make start     - Start all services"
	@echo "  make stop      - Stop all services"
	@echo "  make restart   - Restart all services"
	@echo "  make logs      - Show application logs"
	@echo "  make clean     - Clean up Docker resources"
	@echo "  make rebuild   - Rebuild and restart application"
	@echo "  make test      - Run all tests"
	@echo "  make shell     - Open application shell"
	@echo "  make db        - Connect to database"
	@echo "  make status    - Show service status"
	@echo "  make backup    - Create database backup"
	@echo "  make restore   - Restore database from backup"
	@echo ""
	@echo "Local Development:"
	@echo "  make local-run - Run application locally"
	@echo "  make local-test- Run tests locally"

# Setup development environment
setup:
	@echo "Setting up KiotViet development environment..."
	@chmod +x scripts/*.sh
	@./scripts/setup.sh

# Start services
start:
	@echo "Starting services..."
	@docker-compose up -d

# Stop services
stop:
	@echo "Stopping services..."
	@docker-compose down

# Restart services
restart:
	@echo "Restarting services..."
	@docker-compose restart

# Show logs
logs:
	@docker-compose logs -f app

# Clean up
clean:
	@echo "Cleaning up Docker resources..."
	@docker-compose down -v --remove-orphans
	@docker system prune -f

# Rebuild application
rebuild:
	@echo "Rebuilding application..."
	@docker-compose up -d --build app

# Run tests
test:
	@echo "Running tests..."
	@./scripts/test.sh all

# Open shell
shell:
	@echo "Opening application shell..."
	@docker-compose exec app bash

# Connect to database
db:
	@echo "Connecting to database..."
	@docker-compose exec mysql mysql -u kiotviet_user -p kiotviet_db

# Show status
status:
	@echo "Service status:"
	@docker-compose ps

# Create backup
backup:
	@echo "Creating database backup..."
	@./scripts/dev.sh backup

# Restore backup
restore:
	@if [ -z "$(FILE)" ]; then \
		echo "Usage: make restore FILE=backup_file.sql"; \
		exit 1; \
	fi
	@echo "Restoring database from $(FILE)..."
	@./scripts/dev.sh restore $(FILE)

# Local development commands
local-run:
	@echo "Running application locally..."
	@mvn spring-boot:run

local-test:
	@echo "Running tests locally..."
	@mvn clean test

local-build:
	@echo "Building application locally..."
	@mvn clean package -DskipTests

# Git workflow shortcuts
git-add:
	@git add .

git-commit:
	@if [ -z "$(MSG)" ]; then \
		echo "Usage: make git-commit MSG='commit message'"; \
		exit 1; \
	fi
	@git add .
	@git commit -m "$(MSG)"
	@echo "Committed with message: $(MSG)"

git-push:
	@git push origin main

git-status:
	@git status

git-log:
	@git log --oneline -10

# Development utilities
install-deps:
	@echo "Installing dependencies..."
	@mvn clean install

format-code:
	@echo "Formatting code..."
	@mvn com.coveo:fmt-maven-plugin:format

check-style:
	@echo "Checking code style..."
	@mvn checkstyle:check

# Docker utilities
docker-build:
	@echo "Building Docker images..."
	@docker-compose build

docker-pull:
	@echo "Pulling latest images..."
	@docker-compose pull

docker-clean:
	@echo "Cleaning Docker resources..."
	@docker system prune -a -f --volumes