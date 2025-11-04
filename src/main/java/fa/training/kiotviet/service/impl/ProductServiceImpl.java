package fa.training.kiotviet.service.impl;

import fa.training.kiotviet.model.Product;
import fa.training.kiotviet.enums.ProductStatus;
import fa.training.kiotviet.repository.ProductRepository;
import fa.training.kiotviet.service.ProductService;
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
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for Product business logic operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Product createProduct(Product product) {
        log.info("Creating new product: {}", product.getName());

        // Generate unique SKU if not provided
        if (product.getSku() == null || product.getSku().isEmpty()) {
            product.setSku(generateUniqueSKU());
        }

        // Check if SKU already exists
        if (existsBySku(product.getSku())) {
            throw new BusinessRuleException("SKU already exists: " + product.getSku());
        }

        // Check if barcode already exists
        if (product.getBarcode() != null && !product.getBarcode().isEmpty() &&
            existsByBarcode(product.getBarcode())) {
            throw new BusinessRuleException("Barcode already exists: " + product.getBarcode());
        }

        // Set default status if not provided
        if (product.getStatus() == null) {
            product.setStatus(ProductStatus.ACTIVE);
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {} and SKU: {}", savedProduct.getId(), savedProduct.getSku());
        return savedProduct;
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        log.info("Updating product with ID: {}", id);

        Product existingProduct = findProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        // Check if SKU is being changed and if it already exists
        if (!existingProduct.getSku().equals(product.getSku()) &&
            existsBySku(product.getSku())) {
            throw new BusinessRuleException("SKU already exists: " + product.getSku());
        }

        // Check if barcode is being changed and if it already exists
        if (product.getBarcode() != null && !product.getBarcode().isEmpty() &&
            (existingProduct.getBarcode() == null || !existingProduct.getBarcode().equals(product.getBarcode())) &&
            existsByBarcode(product.getBarcode())) {
            throw new BusinessRuleException("Barcode already exists: " + product.getBarcode());
        }

        // Update fields
        existingProduct.setName(product.getName());
        existingProduct.setSku(product.getSku());
        existingProduct.setBarcode(product.getBarcode());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setCostPrice(product.getCostPrice());
        existingProduct.setSalePrice(product.getSalePrice());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setTaxRate(product.getTaxRate());
        existingProduct.setWeight(product.getWeight());
        existingProduct.setMinStockLevel(product.getMinStockLevel());
        existingProduct.setMaxStockLevel(product.getMaxStockLevel());
        existingProduct.setStatus(product.getStatus());

        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Product updated successfully with ID: {}", updatedProduct.getId());
        return updatedProduct;
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);

        Product product = findProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        productRepository.delete(product);
        log.info("Product deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByStatus(ProductStatus status) {
        return productRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    @Override
    public Product updateStock(Long productId, int quantity) {
        log.info("Updating stock for product ID: {} to quantity: {}", productId, quantity);

        Product product = findProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (quantity < 0) {
            throw new BusinessRuleException("Stock quantity cannot be negative");
        }

        // Note: Stock quantity is managed through InventoryTransaction entities
        // This method updates the min/max stock levels for the product
        // Current stock should be calculated from inventory transactions

        log.info("Stock level update requested for product ID: {}. Note: Actual stock tracking should use InventoryService.", productId);
        return product;
    }

    @Override
    public Product updateProductStatus(Long id, ProductStatus status) {
        log.info("Updating product status for ID: {} to status: {}", id, status);

        Product product = findProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        product.setStatus(status);
        Product updatedProduct = productRepository.save(product);
        log.info("Product status updated successfully for ID: {}", id);
        return updatedProduct;
    }

    @Override
    @Transactional(readOnly = true)
    public long getProductCountByCategory(Long categoryId) {
        return productRepository.countByCategory(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySku(String sku) {
        return productRepository.existsBySku(sku);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByBarcode(String barcode) {
        return productRepository.existsByBarcode(barcode);
    }

    @Override
    public String generateUniqueSKU() {
        String sku;
        int attempts = 0;
        final int maxAttempts = 10;

        do {
            sku = GeneratorUtil.generateSKU();
            attempts++;
            if (attempts > maxAttempts) {
                throw new BusinessRuleException("Unable to generate unique SKU after multiple attempts");
            }
        } while (existsBySku(sku));

        return sku;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculatePriceWithTax(Long productId) {
        Product product = findProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        BigDecimal salePrice = product.getSalePrice();
        BigDecimal taxRate = product.getTaxRate();

        if (taxRate == null || taxRate.compareTo(BigDecimal.ZERO) == 0) {
            return salePrice;
        }

        BigDecimal taxAmount = salePrice.multiply(taxRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        return salePrice.add(taxAmount).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public List<Product> bulkUpdatePrices(List<Long> productIds, BigDecimal priceChange) {
        log.info("Bulk updating prices for {} products with change: {}", productIds.size(), priceChange);

        List<Product> products = productRepository.findAllById(productIds);

        for (Product product : products) {
            BigDecimal newPrice = product.getSalePrice().add(priceChange);
            if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessRuleException("Price cannot be negative for product: " + product.getName());
            }
            product.setSalePrice(newPrice);
        }

        List<Product> updatedProducts = productRepository.saveAll(products);
        log.info("Bulk price update completed for {} products", updatedProducts.size());
        return updatedProducts;
    }
}