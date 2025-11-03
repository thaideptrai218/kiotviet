package fa.training.kiotviet.enums;

public enum UserRole {
    ADMIN("Administrator"),
    MANAGER("Store Manager"),
    STAFF("Sales Staff"),
    USER("Regular User");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}