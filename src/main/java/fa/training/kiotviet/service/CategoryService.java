package fa.training.kiotviet.service;

import fa.training.kiotviet.model.Category;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Category business logic operations.
 */
public interface CategoryService {

    /**
     * Creates a new category.
     */
    Category createCategory(Category category);

    /**
     * Updates an existing category.
     */
    Category updateCategory(Long id, Category category);

    /**
     * Deletes a category by ID.
     */
    void deleteCategory(Long id);

    /**
     * Finds a category by ID.
     */
    Optional<Category> findCategoryById(Long id);

    /**
     * Finds a category by name.
     */
    Optional<Category> findCategoryByName(String name);

    /**
     * Gets all categories.
     */
    List<Category> getAllCategories();

    /**
     * Gets root categories (categories without parent).
     */
    List<Category> getRootCategories();

    /**
     * Gets subcategories of a parent category.
     */
    List<Category> getSubcategories(Long parentId);

    /**
     * Gets all active categories.
     */
    List<Category> getActiveCategories();

    /**
     * Searches categories by keyword.
     */
    List<Category> searchCategories(String keyword);

    /**
     * Gets category tree structure.
     */
    List<Category> getCategoryTree();

    /**
     * Moves a category to a new parent.
     */
    Category moveCategory(Long categoryId, Long newParentId);

    /**
     * Updates category sort order.
     */
    Category updateCategorySort(Long categoryId, int sortOrder);

    /**
     * Activates or deactivates a category.
     */
    Category toggleCategoryStatus(Long id, boolean active);

    /**
     * Gets product count for a category.
     */
    long getProductCountByCategory(Long categoryId);

    /**
     * Checks if category name exists.
     */
    boolean existsByName(String name);

    /**
     * Checks if category has subcategories.
     */
    boolean hasSubcategories(Long categoryId);

    /**
     * Checks if category has products.
     */
    boolean hasProducts(Long categoryId);

    /**
     * Reorders categories within the same parent.
     */
    List<Category> reorderCategories(List<Long> categoryIds);
}