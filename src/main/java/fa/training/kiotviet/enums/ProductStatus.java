package fa.training.kiotviet.enums;

public enum ProductStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    OUT_OF_STOCK("Out of Stock"),
    DISCONTINUED("Discontinued");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}