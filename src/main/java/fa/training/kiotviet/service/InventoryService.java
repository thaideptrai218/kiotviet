package fa.training.kiotviet.service;

import fa.training.kiotviet.model.InventoryTransaction;
import fa.training.kiotviet.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Inventory business logic operations.
 */
public interface InventoryService {

    /**
     * Creates a new inventory transaction.
     */
    InventoryTransaction createTransaction(InventoryTransaction transaction);

    /**
     * Gets current stock for a product.
     */
    int getCurrentStock(Long productId);

    /**
     * Gets inventory transactions for a product.
     */
    List<InventoryTransaction> getProductTransactions(Long productId);

    /**
     * Gets inventory transactions by date range.
     */
    List<InventoryTransaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Gets inventory transactions by type.
     */
    List<InventoryTransaction> getTransactionsByType(TransactionType type);

    /**
     * Records stock IN transaction.
     */
    InventoryTransaction recordStockIn(Long productId, int quantity, BigDecimal cost, String reference);

    /**
     * Records stock OUT transaction.
     */
    InventoryTransaction recordStockOut(Long productId, int quantity, BigDecimal cost, String reference);

    /**
     * Records stock adjustment transaction.
     */
    InventoryTransaction recordStockAdjustment(Long productId, int quantity, BigDecimal cost, String reason);

    /**
     * Records stock return transaction.
     */
    InventoryTransaction recordStockReturn(Long productId, int quantity, BigDecimal cost, String reference);

    /**
     * Gets low stock products.
     */
    List<Long> getLowStockProductIds();

    /**
     * Gets inventory value for a product.
     */
    BigDecimal getInventoryValue(Long productId);

    /**
     * Gets total inventory value.
     */
    BigDecimal getTotalInventoryValue();

    /**
     * Bulk stock update for multiple products.
     */
    List<InventoryTransaction> bulkStockUpdate(List<InventoryTransaction> transactions);

    /**
     * Gets transaction history with pagination.
     */
    List<InventoryTransaction> getTransactionHistory(Long productId, int page, int size);

    /**
     * Validates stock availability.
     */
    boolean hasEnoughStock(Long productId, int requiredQuantity);

    /**
     * Gets stock movement report.
     */
    List<InventoryTransaction> getStockMovementReport(LocalDateTime startDate, LocalDateTime endDate);
}