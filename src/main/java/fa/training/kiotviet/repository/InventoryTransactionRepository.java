package fa.training.kiotviet.repository;

import fa.training.kiotviet.model.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for InventoryTransaction entity operations.
 */
@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    List<InventoryTransaction> findByProductId(Long productId);

    List<InventoryTransaction> findByTransactionType(String transactionType);

    List<InventoryTransaction> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId);

    @Query("SELECT it FROM InventoryTransaction it WHERE it.transactionDate BETWEEN :startDate AND :endDate")
    List<InventoryTransaction> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(CASE WHEN it.transactionType = 'IN' THEN it.quantity ELSE 0 END) - " +
           "SUM(CASE WHEN it.transactionType = 'OUT' THEN it.quantity ELSE 0 END) " +
           "FROM InventoryTransaction it WHERE it.product.id = :productId")
    Integer getCurrentStock(@Param("productId") Long productId);

    @Query("SELECT it FROM InventoryTransaction it WHERE it.notes LIKE %:keyword%")
    List<InventoryTransaction> searchByNotes(@Param("keyword") String keyword);
}