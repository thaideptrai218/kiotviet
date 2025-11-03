#!/bin/bash

# KiotViet Development Helper Script
# This script provides common development commands

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Show help
show_help() {
    echo "KiotViet Development Helper"
    echo ""
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  start         Start all development services"
    echo "  stop          Stop all services"
    echo "  restart       Restart all services"
    echo "  logs          Show application logs"
    echo "  logs-all      Show all service logs"
    echo "  clean         Clean up Docker containers and images"
    echo "  rebuild       Rebuild and restart the application"
    echo "  test          Run tests"
    echo "  shell         Open application shell"
    echo "  db            Connect to database"
    echo "  status        Show service status"
    echo "  backup        Create database backup"
    echo "  restore       Restore database from backup"
    echo "  help          Show this help message"
}

# Start services
start_services() {
    log_info "Starting development services..."
    docker-compose up -d
    log_success "Services started!"
}

# Stop services
stop_services() {
    log_info "Stopping services..."
    docker-compose down
    log_success "Services stopped!"
}

# Restart services
restart_services() {
    log_info "Restarting services..."
    docker-compose restart
    log_success "Services restarted!"
}

# Show logs
show_logs() {
    if [ "$1" = "all" ]; then
        docker-compose logs -f
    else
        docker-compose logs -f app
    fi
}

# Clean up
clean_up() {
    log_info "Cleaning up Docker resources..."
    docker-compose down -v --remove-orphans
    docker system prune -f
    log_success "Cleanup completed!"
}

# Rebuild application
rebuild_app() {
    log_info "Rebuilding application..."
    docker-compose up -d --build app
    log_success "Application rebuilt!"
}

# Run tests
run_tests() {
    log_info "Running tests..."
    docker-compose exec app mvn clean test
    log_success "Tests completed!"
}

# Open shell
open_shell() {
    log_info "Opening application shell..."
    docker-compose exec app bash
}

# Connect to database
connect_db() {
    log_info "Connecting to database..."
    docker-compose exec mysql mysql -u kiotviet_user -p kiotviet_db
}

# Show status
show_status() {
    log_info "Service status:"
    docker-compose ps
}

# Create backup
create_backup() {
    local backup_file="backups/kiotviet_backup_$(date +%Y%m%d_%H%M%S).sql"
    log_info "Creating database backup: $backup_file"

    mkdir -p backups
    docker-compose exec mysql mysqldump -u kiotviet_user -pkiotviet_password kiotviet_db > "$backup_file"

    log_success "Backup created: $backup_file"
}

# Restore backup
restore_backup() {
    if [ -z "$1" ]; then
        log_error "Please specify backup file: $0 restore <backup_file>"
        exit 1
    fi

    local backup_file="$1"
    if [ ! -f "$backup_file" ]; then
        log_error "Backup file not found: $backup_file"
        exit 1
    fi

    log_info "Restoring database from: $backup_file"
    docker-compose exec -T mysql mysql -u kiotviet_user -pkiotviet_password kiotviet_db < "$backup_file"
    log_success "Database restored!"
}

# Main script logic
case "${1:-help}" in
    start)
        start_services
        ;;
    stop)
        stop_services
        ;;
    restart)
        restart_services
        ;;
    logs)
        show_logs "$2"
        ;;
    logs-all)
        show_logs "all"
        ;;
    clean)
        clean_up
        ;;
    rebuild)
        rebuild_app
        ;;
    test)
        run_tests
        ;;
    shell)
        open_shell
        ;;
    db)
        connect_db
        ;;
    status)
        show_status
        ;;
    backup)
        create_backup
        ;;
    restore)
        restore_backup "$2"
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        log_error "Unknown command: $1"
        show_help
        exit 1
        ;;
esac