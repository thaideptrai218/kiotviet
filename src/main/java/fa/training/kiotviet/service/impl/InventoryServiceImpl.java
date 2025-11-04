package fa.training.kiotviet.service.impl;

import fa.training.kiotviet.model.InventoryTransaction;
import fa.training.kiotviet.model.Product;
import fa.training.kiotviet.repository.InventoryTransactionRepository;
import fa.training.kiotviet.repository.ProductRepository;
import fa.training.kiotviet.service.InventoryService;
import fa.training.kiotviet.exception.ResourceNotFoundException;
import fa.training.kiotviet.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service implementation for Inventory business logic operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final ProductRepository productRepository;

    @Override
    public InventoryTransaction createTransaction(InventoryTransaction transaction) {
        log.info("Creating inventory transaction for product ID: {}", transaction.getProduct().getId());

        Product product = productRepository.findById(transaction.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + transaction.getProduct().getId()));

        transaction.setProduct(product);

        // Set transaction date if not provided
        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDateTime.now());
        }

        InventoryTransaction savedTransaction = inventoryTransactionRepository.save(transaction);
        log.info("Inventory transaction created successfully with ID: {}", savedTransaction.getId());
        return savedTransaction;
    }

    @Override
    @Transactional(readOnly = true)
    public int getCurrentStock(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        Integer stock = inventoryTransactionRepository.getCurrentStock(productId);
        return stock != null ? stock : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> getProductTransactions(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        return inventoryTransactionRepository.findByProductId(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return inventoryTransactionRepository.findByDateRange(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> getTransactionsByType(fa.training.kiotviet.enums.TransactionType type) {
        return inventoryTransactionRepository.findByTransactionType(type.name());
    }

    @Override
    public InventoryTransaction recordStockIn(Long productId, int quantity, BigDecimal cost, String reference) {
        log.info("Recording stock IN transaction for product ID: {} quantity: {}", productId, quantity);

        if (quantity <= 0) {
            throw new BusinessRuleException("Stock IN quantity must be positive");
        }

        InventoryTransaction transaction = InventoryTransaction.builder()
                .product(Product.builder().id(productId).build())
                .transactionType("IN")
                .quantity(quantity)
                .unitCost(cost)
                .referenceType("PURCHASE")
                .referenceId(reference != null ? Long.valueOf(reference.hashCode()) : null)
                .notes(reference)
                .transactionDate(LocalDateTime.now())
                .build();

        return createTransaction(transaction);
    }

    @Override
    public InventoryTransaction recordStockOut(Long productId, int quantity, BigDecimal cost, String reference) {
        log.info("Recording stock OUT transaction for product ID: {} quantity: {}", productId, quantity);

        if (quantity <= 0) {
            throw new BusinessRuleException("Stock OUT quantity must be positive");
        }

        // Check if enough stock is available
        if (!hasEnoughStock(productId, quantity)) {
            throw new BusinessRuleException("Insufficient stock for product ID: " + productId);
        }

        InventoryTransaction transaction = InventoryTransaction.builder()
                .product(Product.builder().id(productId).build())
                .transactionType("OUT")
                .quantity(quantity)
                .unitCost(cost)
                .referenceType("SALE")
                .referenceId(reference != null ? Long.valueOf(reference.hashCode()) : null)
                .notes(reference)
                .transactionDate(LocalDateTime.now())
                .build();

        return createTransaction(transaction);
    }

    @Override
    public InventoryTransaction recordStockAdjustment(Long productId, int quantity, BigDecimal cost, String reason) {
        log.info("Recording stock adjustment transaction for product ID: {} quantity: {}", productId, quantity);

        InventoryTransaction transaction = InventoryTransaction.builder()
                .product(Product.builder().id(productId).build())
                .transactionType("ADJUSTMENT")
                .quantity(Math.abs(quantity))
                .unitCost(cost)
                .referenceType("ADJUSTMENT")
                .referenceId(null)
                .notes(reason)
                .transactionDate(LocalDateTime.now())
                .build();

        return createTransaction(transaction);
    }

    @Override
    public InventoryTransaction recordStockReturn(Long productId, int quantity, BigDecimal cost, String reference) {
        log.info("Recording stock return transaction for product ID: {} quantity: {}", productId, quantity);

        if (quantity <= 0) {
            throw new BusinessRuleException("Stock return quantity must be positive");
        }

        InventoryTransaction transaction = InventoryTransaction.builder()
                .product(Product.builder().id(productId).build())
                .transactionType("RETURN")
                .quantity(quantity)
                .unitCost(cost)
                .referenceType("RETURN")
                .referenceId(reference != null ? Long.valueOf(reference.hashCode()) : null)
                .notes(reference)
                .transactionDate(LocalDateTime.now())
                .build();

        return createTransaction(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getLowStockProductIds() {
        return productRepository.findLowStockProducts().stream()
                .map(Product::getId)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getInventoryValue(Long productId) {
        List<InventoryTransaction> transactions = getProductTransactions(productId);

        return transactions.stream()
                .filter(t -> "IN".equals(t.getTransactionType()) || "RETURN".equals(t.getTransactionType()))
                .map(t -> {
                    BigDecimal unitCost = t.getUnitCost() != null ? t.getUnitCost() : BigDecimal.ZERO;
                    return unitCost.multiply(BigDecimal.valueOf(t.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalInventoryValue() {
        List<Product> allProducts = productRepository.findAll();

        return allProducts.stream()
                .map(Product::getId)
                .map(this::getInventoryValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<InventoryTransaction> bulkStockUpdate(List<InventoryTransaction> transactions) {
        log.info("Processing bulk stock update for {} transactions", transactions.size());

        List<InventoryTransaction> savedTransactions = inventoryTransactionRepository.saveAll(transactions);
        log.info("Bulk stock update completed successfully");
        return savedTransactions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> getTransactionHistory(Long productId, int page, int size) {
        // Simple pagination - in production, use Pageable
        List<InventoryTransaction> allTransactions = getProductTransactions(productId);

        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, allTransactions.size());

        if (startIndex >= allTransactions.size()) {
            return List.of();
        }

        return allTransactions.subList(startIndex, endIndex);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasEnoughStock(Long productId, int requiredQuantity) {
        int currentStock = getCurrentStock(productId);
        return currentStock >= requiredQuantity;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> getStockMovementReport(LocalDateTime startDate, LocalDateTime endDate) {
        return getTransactionsByDateRange(startDate, endDate);
    }
}