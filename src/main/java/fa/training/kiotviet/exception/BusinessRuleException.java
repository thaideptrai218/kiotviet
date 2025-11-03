package fa.training.kiotviet.exception;

/**
 * Exception thrown when a business rule is violated.
 */
public class BusinessRuleException extends KiotVietException {

    public BusinessRuleException(String message) {
        super("BUSINESS_RULE_VIOLATION", message);
    }

    public BusinessRuleException(String ruleName, String message) {
        super("BUSINESS_RULE_VIOLATION", String.format("Rule '%s' violated: %s", ruleName, message));
    }
}