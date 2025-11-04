package fa.training.kiotviet.service.impl;

import fa.training.kiotviet.model.Customer;
import fa.training.kiotviet.repository.CustomerRepository;
import fa.training.kiotviet.service.CustomerService;
import fa.training.kiotviet.util.GeneratorUtil;
import fa.training.kiotviet.exception.ResourceNotFoundException;
import fa.training.kiotviet.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for Customer business logic operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer createCustomer(Customer customer) {
        log.info("Creating new customer: {}", customer.getName());

        // Generate unique customer code if not provided
        if (customer.getCustomerCode() == null || customer.getCustomerCode().isEmpty()) {
            customer.setCustomerCode(generateUniqueCustomerCode());
        }

        // Check if customer code already exists
        if (existsByCustomerCode(customer.getCustomerCode())) {
            throw new BusinessRuleException("Customer code already exists: " + customer.getCustomerCode());
        }

        // Check if email already exists
        if (customer.getEmail() != null && !customer.getEmail().isEmpty() &&
            existsByEmail(customer.getEmail())) {
            throw new BusinessRuleException("Email already exists: " + customer.getEmail());
        }

        // Set default values
        if (customer.getLoyaltyPoints() == null) {
            customer.setLoyaltyPoints(0);
        }
        if (customer.getCreditLimit() == null) {
            customer.setCreditLimit(BigDecimal.ZERO);
        }
        if (customer.getCurrentBalance() == null) {
            customer.setCurrentBalance(BigDecimal.ZERO);
        }
        if (customer.getActive() == null) {
            customer.setActive(true);
        }

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created successfully with ID: {} and code: {}", savedCustomer.getId(), savedCustomer.getCustomerCode());
        return savedCustomer;
    }

    @Override
    public Customer updateCustomer(Long id, Customer customer) {
        log.info("Updating customer with ID: {}", id);

        Customer existingCustomer = findCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        // Check if customer code is being changed and if it already exists
        if (!existingCustomer.getCustomerCode().equals(customer.getCustomerCode()) &&
            existsByCustomerCode(customer.getCustomerCode())) {
            throw new BusinessRuleException("Customer code already exists: " + customer.getCustomerCode());
        }

        // Check if email is being changed and if it already exists
        if (customer.getEmail() != null && !customer.getEmail().isEmpty() &&
            (existingCustomer.getEmail() == null || !existingCustomer.getEmail().equals(customer.getEmail())) &&
            existsByEmail(customer.getEmail())) {
            throw new BusinessRuleException("Email already exists: " + customer.getEmail());
        }

        // Update fields
        existingCustomer.setName(customer.getName());
        existingCustomer.setCustomerCode(customer.getCustomerCode());
        existingCustomer.setEmail(customer.getEmail());
        existingCustomer.setPhoneNumber(customer.getPhoneNumber());
        existingCustomer.setAddress(customer.getAddress());
        existingCustomer.setCity(customer.getCity());
        existingCustomer.setDistrict(customer.getDistrict());
        existingCustomer.setPostalCode(customer.getPostalCode());
        existingCustomer.setBirthDate(customer.getBirthDate());
        existingCustomer.setLoyaltyPoints(customer.getLoyaltyPoints());
        existingCustomer.setCreditLimit(customer.getCreditLimit());
        existingCustomer.setCurrentBalance(customer.getCurrentBalance());
        existingCustomer.setNotes(customer.getNotes());
        existingCustomer.setActive(customer.getActive());

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        log.info("Customer updated successfully with ID: {}", updatedCustomer.getId());
        return updatedCustomer;
    }

    @Override
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);

        Customer customer = findCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        // Check if customer has orders
        long orderCount = getOrderCountByCustomer(id);
        if (orderCount > 0) {
            throw new BusinessRuleException("Cannot delete customer with " + orderCount + " orders. Please delete or transfer orders first.");
        }

        customerRepository.delete(customer);
        log.info("Customer deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findCustomerByCode(String customerCode) {
        return customerRepository.findByCustomerCode(customerCode);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Customer> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getActiveCustomers() {
        return customerRepository.findByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Customer> searchCustomers(String keyword, Pageable pageable) {
        return customerRepository.searchCustomers(keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getCustomersWithLoyaltyPoints() {
        return customerRepository.findCustomersWithLoyaltyPoints();
    }

    @Override
    public Customer addLoyaltyPoints(Long customerId, int points) {
        log.info("Adding {} loyalty points to customer ID: {}", points, customerId);

        if (points <= 0) {
            throw new BusinessRuleException("Loyalty points must be positive");
        }

        Customer customer = findCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        int currentPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
        customer.setLoyaltyPoints(currentPoints + points);

        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Loyalty points added successfully for customer ID: {}", customerId);
        return updatedCustomer;
    }

    @Override
    public Customer redeemLoyaltyPoints(Long customerId, int points) {
        log.info("Redeeming {} loyalty points from customer ID: {}", points, customerId);

        if (points <= 0) {
            throw new BusinessRuleException("Redemption points must be positive");
        }

        Customer customer = findCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        int currentPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
        if (currentPoints < points) {
            throw new BusinessRuleException("Insufficient loyalty points. Current: " + currentPoints + ", Requested: " + points);
        }

        customer.setLoyaltyPoints(currentPoints - points);
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Loyalty points redeemed successfully for customer ID: {}", customerId);
        return updatedCustomer;
    }

    @Override
    public Customer updateCreditLimit(Long customerId, BigDecimal creditLimit) {
        log.info("Updating credit limit for customer ID: {} to: {}", customerId, creditLimit);

        if (creditLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("Credit limit cannot be negative");
        }

        Customer customer = findCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        customer.setCreditLimit(creditLimit);
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Credit limit updated successfully for customer ID: {}", customerId);
        return updatedCustomer;
    }

    @Override
    public Customer updateBalance(Long customerId, BigDecimal balance) {
        log.info("Updating balance for customer ID: {} to: {}", customerId, balance);

        Customer customer = findCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        customer.setCurrentBalance(balance);
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Balance updated successfully for customer ID: {}", customerId);
        return updatedCustomer;
    }

    @Override
    public Customer toggleCustomerStatus(Long id, boolean active) {
        log.info("Toggling customer status for ID: {} to active: {}", id, active);

        Customer customer = findCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        customer.setActive(active);
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer status updated successfully for ID: {}", id);
        return updatedCustomer;
    }

    @Override
    @Transactional(readOnly = true)
    public long getOrderCountByCustomer(Long customerId) {
        return customerRepository.countOrdersByCustomer(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCustomerCode(String customerCode) {
        return customerRepository.existsByCustomerCode(customerCode);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    @Override
    public String generateUniqueCustomerCode() {
        String customerCode;
        int attempts = 0;
        final int maxAttempts = 10;

        do {
            customerCode = GeneratorUtil.generateCustomerCode();
            attempts++;
            if (attempts > maxAttempts) {
                throw new BusinessRuleException("Unable to generate unique customer code after multiple attempts");
            }
        } while (existsByCustomerCode(customerCode));

        return customerCode;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasSufficientCredit(Long customerId, BigDecimal amount) {
        Customer customer = findCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        BigDecimal creditLimit = customer.getCreditLimit() != null ? customer.getCreditLimit() : BigDecimal.ZERO;
        BigDecimal currentBalance = customer.getCurrentBalance() != null ? customer.getCurrentBalance() : BigDecimal.ZERO;
        BigDecimal availableCredit = creditLimit.subtract(currentBalance);

        return amount.compareTo(availableCredit) <= 0;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCustomerDebt(Long customerId) {
        Customer customer = findCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        return customer.getCurrentBalance() != null ? customer.getCurrentBalance() : BigDecimal.ZERO;
    }

    @Override
    public Customer updateContactInfo(Long customerId, String phone, String email, String address) {
        log.info("Updating contact information for customer ID: {}", customerId);

        Customer customer = findCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        // Check if email is being changed and if it already exists
        if (email != null && !email.isEmpty() &&
            (customer.getEmail() == null || !customer.getEmail().equals(email)) &&
            existsByEmail(email)) {
            throw new BusinessRuleException("Email already exists: " + email);
        }

        customer.setPhoneNumber(phone);
        customer.setEmail(email);
        customer.setAddress(address);

        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Contact information updated successfully for customer ID: {}", customerId);
        return updatedCustomer;
    }

    @Override
    public List<Customer> bulkUpdateStatus(List<Long> customerIds, boolean active) {
        log.info("Bulk updating status for {} customers to active: {}", customerIds.size(), active);

        List<Customer> customers = customerRepository.findAllById(customerIds);

        for (Customer customer : customers) {
            customer.setActive(active);
        }

        List<Customer> updatedCustomers = customerRepository.saveAll(customers);
        log.info("Bulk status update completed for {} customers", updatedCustomers.size());
        return updatedCustomers;
    }
}