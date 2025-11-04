package fa.training.kiotviet.controller;

import fa.training.kiotviet.model.Category;
import fa.training.kiotviet.dto.ApiResponse;
import fa.training.kiotviet.service.CategoryService;
import fa.training.kiotviet.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Category operations.
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Get all categories.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        log.info("Fetching all categories");

        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories, "Categories retrieved successfully"));
    }

    /**
     * Get category by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long id) {
        log.info("Fetching category with ID: {}", id);

        Optional<Category> category = categoryService.findCategoryById(id);
        if (category.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(category.get(), "Category retrieved successfully"));
        } else {
            throw new ResourceNotFoundException("Category not found with ID: " + id);
        }
    }

    /**
     * Get root categories (categories without parent).
     */
    @GetMapping("/root")
    public ResponseEntity<ApiResponse<List<Category>>> getRootCategories() {
        log.info("Fetching root categories");

        List<Category> categories = categoryService.getRootCategories();
        return ResponseEntity.ok(ApiResponse.success(categories, "Root categories retrieved successfully"));
    }

    /**
     * Get subcategories of a parent category.
     */
    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<ApiResponse<List<Category>>> getSubcategories(@PathVariable Long parentId) {
        log.info("Fetching subcategories for parent ID: {}", parentId);

        List<Category> subcategories = categoryService.getSubcategories(parentId);
        return ResponseEntity.ok(ApiResponse.success(subcategories, "Subcategories retrieved successfully"));
    }

    /**
     * Get category tree structure.
     */
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<Category>>> getCategoryTree() {
        log.info("Fetching category tree");

        List<Category> categoryTree = categoryService.getCategoryTree();
        return ResponseEntity.ok(ApiResponse.success(categoryTree, "Category tree retrieved successfully"));
    }

    /**
     * Get active categories.
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Category>>> getActiveCategories() {
        log.info("Fetching active categories");

        List<Category> categories = categoryService.getActiveCategories();
        return ResponseEntity.ok(ApiResponse.success(categories, "Active categories retrieved successfully"));
    }

    /**
     * Search categories by keyword.
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Category>>> searchCategories(@RequestParam String keyword) {
        log.info("Searching categories with keyword: {}", keyword);

        List<Category> categories = categoryService.searchCategories(keyword);
        return ResponseEntity.ok(ApiResponse.success(categories, "Categories retrieved successfully"));
    }

    /**
     * Create new category.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody Category category) {
        log.info("Creating new category: {}", category.getName());

        Category createdCategory = categoryService.createCategory(category);
        return new ResponseEntity<>(ApiResponse.success(createdCategory, "Category created successfully"), HttpStatus.CREATED);
    }

    /**
     * Update existing category.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody Category category) {
        log.info("Updating category with ID: {}", id);

        Category updatedCategory = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(ApiResponse.success(updatedCategory, "Category updated successfully"));
    }

    /**
     * Delete category.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        log.info("Deleting category with ID: {}", id);

        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Category deleted successfully"));
    }

    /**
     * Move category to new parent.
     */
    @PatchMapping("/{id}/move")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Category>> moveCategory(
            @PathVariable Long id,
            @RequestParam(required = false) Long newParentId) {
        log.info("Moving category ID: {} to parent ID: {}", id, newParentId);

        Category movedCategory = categoryService.moveCategory(id, newParentId);
        return ResponseEntity.ok(ApiResponse.success(movedCategory, "Category moved successfully"));
    }

    /**
     * Update category sort order.
     */
    @PatchMapping("/{id}/sort")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Category>> updateCategorySort(
            @PathVariable Long id,
            @RequestParam int sortOrder) {
        log.info("Updating sort order for category ID: {} to: {}", id, sortOrder);

        Category updatedCategory = categoryService.updateCategorySort(id, sortOrder);
        return ResponseEntity.ok(ApiResponse.success(updatedCategory, "Category sort order updated successfully"));
    }

    /**
     * Toggle category status.
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Category>> toggleCategoryStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        log.info("Toggling category status for ID: {} to active: {}", id, active);

        Category updatedCategory = categoryService.toggleCategoryStatus(id, active);
        return ResponseEntity.ok(ApiResponse.success(updatedCategory, "Category status updated successfully"));
    }

    /**
     * Check if category name exists.
     */
    @GetMapping("/check-name/{name}")
    public ResponseEntity<ApiResponse<Boolean>> checkNameExists(@PathVariable String name) {
        log.info("Checking if category name exists: {}", name);

        boolean exists = categoryService.existsByName(name);
        return ResponseEntity.ok(ApiResponse.success(exists, "Category name check completed"));
    }

    /**
     * Get product count for a category.
     */
    @GetMapping("/{id}/product-count")
    public ResponseEntity<ApiResponse<Long>> getProductCount(@PathVariable Long id) {
        log.info("Getting product count for category ID: {}", id);

        long count = categoryService.getProductCountByCategory(id);
        return ResponseEntity.ok(ApiResponse.success(count, "Product count retrieved successfully"));
    }
}