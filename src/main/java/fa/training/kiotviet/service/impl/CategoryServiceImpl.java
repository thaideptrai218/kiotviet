package fa.training.kiotviet.service.impl;

import fa.training.kiotviet.model.Category;
import fa.training.kiotviet.repository.CategoryRepository;
import fa.training.kiotviet.service.CategoryService;
import fa.training.kiotviet.exception.ResourceNotFoundException;
import fa.training.kiotviet.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for Category business logic operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(Category category) {
        log.info("Creating new category: {}", category.getName());

        // Check if category name already exists
        if (existsByName(category.getName())) {
            throw new BusinessRuleException("Category name already exists: " + category.getName());
        }

        // If parent is specified, verify it exists
        if (category.getParentId() != null) {
            Category parent = findCategoryById(category.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with ID: " + category.getParentId()));
            // Parent ID is already set
        }

        // Set default values
        if (category.getSortOrder() == null) {
            category.setSortOrder(getNextSortOrder(category.getParentId()));
        }
        if (category.getActive() == null) {
            category.setActive(true);
        }

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", savedCategory.getId());
        return savedCategory;
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        log.info("Updating category with ID: {}", id);

        Category existingCategory = findCategoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        // Check if name is being changed and if it already exists
        if (!existingCategory.getName().equals(category.getName()) &&
            existsByName(category.getName())) {
            throw new BusinessRuleException("Category name already exists: " + category.getName());
        }

        // Prevent circular reference in parent-child relationship
        if (category.getParentId() != null) {
            if (category.getParentId().equals(id)) {
                throw new BusinessRuleException("Category cannot be its own parent");
            }

            Category parent = findCategoryById(category.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with ID: " + category.getParentId()));

            if (isDescendant(parent, id)) {
                throw new BusinessRuleException("Cannot move category to its own descendant");
            }

            existingCategory.setParentId(parent.getId());
        } else {
            existingCategory.setParentId(null);
        }

        // Update fields
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setSortOrder(category.getSortOrder());
        existingCategory.setActive(category.getActive());

        Category updatedCategory = categoryRepository.save(existingCategory);
        log.info("Category updated successfully with ID: {}", updatedCategory.getId());
        return updatedCategory;
    }

    @Override
    public void deleteCategory(Long id) {
        log.info("Deleting category with ID: {}", id);

        Category category = findCategoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        // Check if category has subcategories
        if (hasSubcategories(id)) {
            throw new BusinessRuleException("Cannot delete category with subcategories. Please delete or move subcategories first.");
        }

        // Check if category has products
        if (hasProducts(id)) {
            throw new BusinessRuleException("Cannot delete category with products. Please move or delete products first.");
        }

        categoryRepository.delete(category);
        log.info("Category deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIdIsNull();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getSubcategories(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getActiveCategories() {
        return categoryRepository.findByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> searchCategories(String keyword) {
        return categoryRepository.searchCategories(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategoryTree() {
        List<Category> rootCategories = getRootCategories();
        List<Category> categoryTree = new ArrayList<>();

        for (Category root : rootCategories) {
            categoryTree.add(buildCategoryTree(root));
        }

        return categoryTree;
    }

    @Override
    public Category moveCategory(Long categoryId, Long newParentId) {
        log.info("Moving category ID: {} to parent ID: {}", categoryId, newParentId);

        Category category = findCategoryById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

        if (newParentId != null) {
            if (newParentId.equals(categoryId)) {
                throw new BusinessRuleException("Category cannot be its own parent");
            }

            Category newParent = findCategoryById(newParentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with ID: " + newParentId));

            if (isDescendant(newParent, categoryId)) {
                throw new BusinessRuleException("Cannot move category to its own descendant");
            }

            category.setParentId(newParentId);
        } else {
            category.setParentId(null);
        }

        Category movedCategory = categoryRepository.save(category);
        log.info("Category moved successfully with ID: {}", categoryId);
        return movedCategory;
    }

    @Override
    public Category updateCategorySort(Long categoryId, int sortOrder) {
        log.info("Updating sort order for category ID: {} to: {}", categoryId, sortOrder);

        Category category = findCategoryById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

        category.setSortOrder(sortOrder);
        Category updatedCategory = categoryRepository.save(category);
        log.info("Category sort order updated successfully for ID: {}", categoryId);
        return updatedCategory;
    }

    @Override
    public Category toggleCategoryStatus(Long id, boolean active) {
        log.info("Toggling category status for ID: {} to active: {}", id, active);

        Category category = findCategoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        category.setActive(active);
        Category updatedCategory = categoryRepository.save(category);
        log.info("Category status updated successfully for ID: {}", id);
        return updatedCategory;
    }

    @Override
    @Transactional(readOnly = true)
    public long getProductCountByCategory(Long categoryId) {
        return categoryRepository.countProductsByCategory(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasSubcategories(Long categoryId) {
        List<Category> subcategories = getSubcategories(categoryId);
        return !subcategories.isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasProducts(Long categoryId) {
        return getProductCountByCategory(categoryId) > 0;
    }

    @Override
    public List<Category> reorderCategories(List<Long> categoryIds) {
        log.info("Reordering {} categories", categoryIds.size());

        List<Category> categories = categoryRepository.findAllById(categoryIds);

        for (int i = 0; i < categoryIds.size(); i++) {
            Long categoryId = categoryIds.get(i);
            Category category = categories.stream()
                    .filter(c -> c.getId().equals(categoryId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

            category.setSortOrder(i + 1);
        }

        List<Category> reorderedCategories = categoryRepository.saveAll(categories);
        log.info("Categories reordered successfully");
        return reorderedCategories;
    }

    /**
     * Builds category tree recursively.
     */
    private Category buildCategoryTree(Category category) {
        List<Category> subcategories = getSubcategories(category.getId());
        for (Category subcategory : subcategories) {
            buildCategoryTree(subcategory);
        }
        return category;
    }

    /**
     * Checks if a category is a descendant of another category.
     */
    private boolean isDescendant(Category potentialAncestor, Long categoryId) {
        if (potentialAncestor.getId().equals(categoryId)) {
            return true;
        }

        List<Category> children = getSubcategories(potentialAncestor.getId());
        for (Category child : children) {
            if (isDescendant(child, categoryId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the next sort order for a category.
     */
    private Integer getNextSortOrder(Long parentId) {
        List<Category> siblings;
        if (parentId != null) {
            siblings = getSubcategories(parentId);
        } else {
            siblings = getRootCategories();
        }

        return siblings.stream()
                .mapToInt(c -> c.getSortOrder() != null ? c.getSortOrder() : 0)
                .max()
                .orElse(0) + 1;
    }
}