package fa.training.kiotviet.service;

import fa.training.kiotviet.model.Customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Customer business logic operations.
 */
public interface CustomerService {

    /**
     * Creates a new customer.
     */
    Customer createCustomer(Customer customer);

    /**
     * Updates an existing customer.
     */
    Customer updateCustomer(Long id, Customer customer);

    /**
     * Deletes a customer by ID.
     */
    void deleteCustomer(Long id);

    /**
     * Finds a customer by ID.
     */
    Optional<Customer> findCustomerById(Long id);

    /**
     * Finds a customer by customer code.
     */
    Optional<Customer> findCustomerByCode(String customerCode);

    /**
     * Finds a customer by email.
     */
    Optional<Customer> findCustomerByEmail(String email);

    /**
     * Gets all customers with pagination.
     */
    Page<Customer> getAllCustomers(Pageable pageable);

    /**
     * Gets all active customers.
     */
    List<Customer> getActiveCustomers();

    /**
     * Searches customers by keyword with pagination.
     */
    Page<Customer> searchCustomers(String keyword, Pageable pageable);

    /**
     * Gets customers with loyalty points.
     */
    List<Customer> getCustomersWithLoyaltyPoints();

    /**
     * Adds loyalty points to customer.
     */
    Customer addLoyaltyPoints(Long customerId, int points);

    /**
     * Redeems loyalty points from customer.
     */
    Customer redeemLoyaltyPoints(Long customerId, int points);

    /**
     * Updates customer credit limit.
     */
    Customer updateCreditLimit(Long customerId, BigDecimal creditLimit);

    /**
     * Updates customer balance.
     */
    Customer updateBalance(Long customerId, BigDecimal balance);

    /**
     * Activates or deactivates a customer.
     */
    Customer toggleCustomerStatus(Long id, boolean active);

    /**
     * Gets order count for a customer.
     */
    long getOrderCountByCustomer(Long customerId);

    /**
     * Checks if customer code exists.
     */
    boolean existsByCustomerCode(String customerCode);

    /**
     * Checks if customer email exists.
     */
    boolean existsByEmail(String email);

    /**
     * Generates unique customer code for new customer.
     */
    String generateUniqueCustomerCode();

    /**
     * Checks if customer has sufficient credit limit.
     */
    boolean hasSufficientCredit(Long customerId, BigDecimal amount);

    /**
     * Gets customer total debt.
     */
    BigDecimal getCustomerDebt(Long customerId);

    /**
     * Updates customer contact information.
     */
    Customer updateContactInfo(Long customerId, String phone, String email, String address);

    /**
     * Bulk update customer status.
     */
    List<Customer> bulkUpdateStatus(List<Long> customerIds, boolean active);
}