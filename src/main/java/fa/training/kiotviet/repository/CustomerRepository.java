package fa.training.kiotviet.repository;

import fa.training.kiotviet.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Customer entity operations.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCustomerCode(String customerCode);

    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCustomerCode(String customerCode);

    List<Customer> findByActiveTrue();

    @Query("SELECT c FROM Customer c WHERE c.name LIKE %:keyword% OR c.email LIKE %:keyword% OR c.customerCode LIKE %:keyword%")
    Page<Customer> searchCustomers(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.loyaltyPoints > 0")
    List<Customer> findCustomersWithLoyaltyPoints();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.customer.id = :customerId")
    long countOrdersByCustomer(@Param("customerId") Long customerId);
}