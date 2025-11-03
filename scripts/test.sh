#!/bin/bash

# KiotViet Test Runner Script
# This script handles testing for the application

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Check if services are running
check_services() {
    if ! docker-compose ps app | grep -q "Up"; then
        log_error "Application is not running. Please start services first: ./scripts/dev.sh start"
        exit 1
    fi
}

# Run unit tests
run_unit_tests() {
    log_info "Running unit tests..."
    docker-compose exec app mvn clean test
    log_success "Unit tests completed!"
}

# Run integration tests
run_integration_tests() {
    log_info "Running integration tests..."
    docker-compose exec app mvn verify -P integration-test
    log_success "Integration tests completed!"
}

# Run all tests
run_all_tests() {
    log_info "Running all tests..."
    check_services
    run_unit_tests
    run_integration_tests
    log_success "All tests completed successfully!"
}

# Generate test report
generate_report() {
    log_info "Generating test report..."
    docker-compose exec app mvn jacoco:report
    log_info "Test report generated at: target/site/jacoco/index.html"
}

# Run with coverage
run_with_coverage() {
    log_info "Running tests with coverage analysis..."
    docker-compose exec app mvn clean verify jacoco:report
    log_success "Tests with coverage completed!"
}

# Run specific test class
run_test_class() {
    if [ -z "$1" ]; then
        log_error "Please specify test class: $0 class <ClassName>"
        exit 1
    fi

    log_info "Running test class: $1"
    docker-compose exec app mvn test -Dtest="$1"
    log_success "Test class completed!"
}

# Run specific test method
run_test_method() {
    if [ -z "$1" ]; then
        log_error "Please specify test method: $0 method <ClassName#methodName>"
        exit 1
    fi

    log_info "Running test method: $1"
    docker-compose exec app mvn test -Dtest="$1"
    log_success "Test method completed!"
}

# Show help
show_help() {
    echo "KiotViet Test Runner"
    echo ""
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo ""
    echo "Commands:"
    echo "  unit          Run unit tests only"
    echo "  integration   Run integration tests only"
    echo "  all           Run all tests (unit + integration)"
    echo "  coverage      Run tests with coverage analysis"
    echo "  report        Generate test coverage report"
    echo "  class <name>  Run specific test class"
    echo "  method <name> Run specific test method"
    echo "  help          Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 all"
    echo "  $0 class UserServiceTest"
    echo "  $0 method UserServiceTest#testCreateUser"
    echo "  $0 coverage"
}

# Main logic
case "${1:-help}" in
    unit)
        check_services
        run_unit_tests
        ;;
    integration)
        check_services
        run_integration_tests
        ;;
    all)
        run_all_tests
        ;;
    coverage)
        check_services
        run_with_coverage
        generate_report
        ;;
    report)
        generate_report
        ;;
    class)
        check_services
        run_test_class "$2"
        ;;
    method)
        check_services
        run_test_method "$2"
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