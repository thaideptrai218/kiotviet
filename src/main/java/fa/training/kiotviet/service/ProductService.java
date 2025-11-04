package fa.training.kiotviet.service;

import fa.training.kiotviet.model.Product;
import fa.training.kiotviet.enums.ProductStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Product business logic operations.
 */
public interface ProductService {

    /**
     * Creates a new product.
     */
    Product createProduct(Product product);

    /**
     * Updates an existing product.
     */
    Product updateProduct(Long id, Product product);

    /**
     * Deletes a product by ID.
     */
    void deleteProduct(Long id);

    /**
     * Finds a product by ID.
     */
    Optional<Product> findProductById(Long id);

    /**
     * Finds a product by SKU.
     */
    Optional<Product> findProductBySku(String sku);

    /**
     * Gets all products with pagination.
     */
    Page<Product> getAllProducts(Pageable pageable);

    /**
     * Gets products by category.
     */
    List<Product> getProductsByCategory(Long categoryId);

    /**
     * Gets products by status.
     */
    List<Product> getProductsByStatus(ProductStatus status);

    /**
     * Gets all active products.
     */
    List<Product> getActiveProducts();

    /**
     * Searches products by keyword with pagination.
     */
    Page<Product> searchProducts(String keyword, Pageable pageable);

    /**
     * Gets products by price range.
     */
    List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Gets low stock products.
     */
    List<Product> getLowStockProducts();

    /**
     * Updates product stock quantity.
     */
    Product updateStock(Long productId, int quantity);

    /**
     * Updates product status.
     */
    Product updateProductStatus(Long id, ProductStatus status);

    /**
     * Gets product count by category.
     */
    long getProductCountByCategory(Long categoryId);

    /**
     * Checks if SKU exists.
     */
    boolean existsBySku(String sku);

    /**
     * Checks if barcode exists.
     */
    boolean existsByBarcode(String barcode);

    /**
     * Generates unique SKU for new product.
     */
    String generateUniqueSKU();

    /**
     * Calculates product price with tax.
     */
    BigDecimal calculatePriceWithTax(Long productId);

    /**
     * Bulk update product prices.
     */
    List<Product> bulkUpdatePrices(List<Long> productIds, BigDecimal priceChange);
}