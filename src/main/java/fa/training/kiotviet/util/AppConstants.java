package fa.training.kiotviet.util;

/**
 * Application constants used throughout the system.
 */
public final class AppConstants {

    private AppConstants() {
        // Utility class - prevent instantiation
    }

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // Security
    public static final String DEFAULT_ADMIN_USERNAME = "admin";
    public static final String SYSTEM_USER = "system";

    // File upload
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_FILE_TYPES = {"jpg", "jpeg", "png", "gif", "pdf", "doc", "docx"};

    // Date formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    // Business constants
    public static final String ORDER_NUMBER_PREFIX = "HD";
    public static final String CUSTOMER_CODE_PREFIX = "KH";
    public static final String SKU_PREFIX = "SKU";

    // Response messages
    public static final String SUCCESS_MESSAGE = "Operation completed successfully";
    public static final String CREATED_MESSAGE = "Resource created successfully";
    public static final String UPDATED_MESSAGE = "Resource updated successfully";
    public static final String DELETED_MESSAGE = "Resource deleted successfully";

    // Cache keys
    public static final String PRODUCT_CACHE_PREFIX = "product:";
    public static final String CATEGORY_CACHE_PREFIX = "category:";
    public static final String USER_CACHE_PREFIX = "user:";
}