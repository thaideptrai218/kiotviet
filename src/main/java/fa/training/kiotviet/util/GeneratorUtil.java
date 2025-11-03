package fa.training.kiotviet.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class for generating unique identifiers.
 */
public final class GeneratorUtil {

    private static final AtomicLong orderSequence = new AtomicLong(0);
    private static final AtomicLong customerSequence = new AtomicLong(0);
    private static final AtomicLong skuSequence = new AtomicLong(0);

    private GeneratorUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Generates a unique order number with date prefix.
     */
    public static String generateOrderNumber() {
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long sequence = orderSequence.incrementAndGet();
        return String.format("%s%04d", AppConstants.ORDER_NUMBER_PREFIX, sequence);
    }

    /**
     * Generates a unique customer code.
     */
    public static String generateCustomerCode() {
        long sequence = customerSequence.incrementAndGet();
        return String.format("%s%06d", AppConstants.CUSTOMER_CODE_PREFIX, sequence);
    }

    /**
     * Generates a unique SKU.
     */
    public static String generateSKU() {
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMM"));
        long sequence = skuSequence.incrementAndGet();
        return String.format("%s%s%04d", AppConstants.SKU_PREFIX, datePrefix, sequence);
    }

    /**
     * Resets sequences (mainly for testing).
     */
    public static void resetSequences() {
        orderSequence.set(0);
        customerSequence.set(0);
        skuSequence.set(0);
    }
}