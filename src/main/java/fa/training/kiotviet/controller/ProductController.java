package fa.training.kiotviet.controller;

import fa.training.kiotviet.model.Product;
import fa.training.kiotviet.enums.ProductStatus;
import fa.training.kiotviet.dto.ApiResponse;
import fa.training.kiotviet.service.ProductService;
import fa.training.kiotviet.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Product operations.
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    /**
     * Get all products with pagination and search.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Product>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "true") boolean activeOnly) {

        log.info("Fetching products - page: {}, size: {}, keyword: {}, activeOnly: {}", page, size, keyword, activeOnly);

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Product> products;
        if (keyword != null && !keyword.trim().isEmpty()) {
            products = productService.searchProducts(keyword.trim(), pageable);
        } else if (activeOnly) {
            List<Product> activeProducts = productService.getActiveProducts();
            // Convert list to page for consistency
            int start = Math.min((int) pageable.getOffset(), activeProducts.size());
            int end = Math.min((start + pageable.getPageSize()), activeProducts.size());
            products = new org.springframework.data.domain.PageImpl<>(
                activeProducts.subList(start, end),
                pageable,
                activeProducts.size()
            );
        } else {
            products = productService.getAllProducts(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(products, "Products retrieved successfully"));
    }

    /**
     * Get product by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {
        log.info("Fetching product with ID: {}", id);

        Optional<Product> product = productService.findProductById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(product.get(), "Product retrieved successfully"));
        } else {
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }
    }

    /**
     * Get product by SKU.
     */
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponse<Product>> getProductBySku(@PathVariable String sku) {
        log.info("Fetching product with SKU: {}", sku);

        Optional<Product> product = productService.findProductBySku(sku);
        if (product.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(product.get(), "Product retrieved successfully"));
        } else {
            throw new ResourceNotFoundException("Product not found with SKU: " + sku);
        }
    }

    /**
     * Create new product.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Product>> createProduct(@Valid @RequestBody Product product) {
        log.info("Creating new product: {}", product.getName());

        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(ApiResponse.success(createdProduct, "Product created successfully"), HttpStatus.CREATED);
    }

    /**
     * Update existing product.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product product) {
        log.info("Updating product with ID: {}", id);

        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(ApiResponse.success(updatedProduct, "Product updated successfully"));
    }

    /**
     * Delete product.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        log.info("Deleting product with ID: {}", id);

        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
    }

    /**
     * Get products by category.
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByCategory(@PathVariable Long categoryId) {
        log.info("Fetching products for category ID: {}", categoryId);

        List<Product> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(products, "Products retrieved successfully"));
    }

    /**
     * Get products by status.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByStatus(@PathVariable ProductStatus status) {
        log.info("Fetching products with status: {}", status);

        List<Product> products = productService.getProductsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(products, "Products retrieved successfully"));
    }

    /**
     * Get low stock products.
     */
    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<List<Product>>> getLowStockProducts() {
        log.info("Fetching low stock products");

        List<Product> products = productService.getLowStockProducts();
        return ResponseEntity.ok(ApiResponse.success(products, "Low stock products retrieved successfully"));
    }

    /**
     * Search products by price range.
     */
    @GetMapping("/price-range")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        log.info("Fetching products in price range: {} - {}", minPrice, maxPrice);

        List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(ApiResponse.success(products, "Products retrieved successfully"));
    }

    /**
     * Generate unique SKU.
     */
    @GetMapping("/generate-sku")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<String>> generateUniqueSku() {
        log.info("Generating unique SKU");

        String sku = productService.generateUniqueSKU();
        return ResponseEntity.ok(ApiResponse.success(sku, "SKU generated successfully"));
    }

    /**
     * Calculate product price with tax.
     */
    @GetMapping("/{id}/price-with-tax")
    public ResponseEntity<ApiResponse<BigDecimal>> getPriceWithTax(@PathVariable Long id) {
        log.info("Calculating price with tax for product ID: {}", id);

        BigDecimal priceWithTax = productService.calculatePriceWithTax(id);
        return ResponseEntity.ok(ApiResponse.success(priceWithTax, "Price with tax calculated successfully"));
    }

    /**
     * Update product status.
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Product>> updateProductStatus(
            @PathVariable Long id,
            @RequestParam ProductStatus status) {
        log.info("Updating status for product ID: {} to status: {}", id, status);

        Product updatedProduct = productService.updateProductStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(updatedProduct, "Product status updated successfully"));
    }

    /**
     * Check if SKU exists.
     */
    @GetMapping("/check-sku/{sku}")
    public ResponseEntity<ApiResponse<Boolean>> checkSkuExists(@PathVariable String sku) {
        log.info("Checking if SKU exists: {}", sku);

        boolean exists = productService.existsBySku(sku);
        return ResponseEntity.ok(ApiResponse.success(exists, "SKU check completed"));
    }

    /**
     * Get product statistics.
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Object>> getProductStats() {
        log.info("Fetching product statistics");

        // This would typically involve more complex calculations
        // For now, return basic stats
        List<Product> allProducts = productService.getAllProducts(PageRequest.of(0, 1000)).getContent();
        List<Product> activeProducts = productService.getActiveProducts();
        List<Product> lowStockProducts = productService.getLowStockProducts();

        Object stats = java.util.Map.of(
            "totalProducts", allProducts.size(),
            "activeProducts", activeProducts.size(),
            "lowStockProducts", lowStockProducts.size()
        );

        return ResponseEntity.ok(ApiResponse.success(stats, "Product statistics retrieved successfully"));
    }
}