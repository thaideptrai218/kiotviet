package fa.training.kiotviet.enums;

/**
 * Enumeration for inventory transaction types.
 */
public enum TransactionType {
    IN("Stock In"),
    OUT("Stock Out"),
    ADJUSTMENT("Stock Adjustment"),
    RETURN("Stock Return");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}