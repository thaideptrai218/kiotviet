package fa.training.kiotviet.service;

import fa.training.kiotviet.model.Order;
import fa.training.kiotviet.enums.OrderStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Order business logic operations.
 */
public interface OrderService {

    /**
     * Creates a new order.
     */
    Order createOrder(Order order);

    /**
     * Updates an existing order.
     */
    Order updateOrder(Long id, Order order);

    /**
     * Deletes an order by ID.
     */
    void deleteOrder(Long id);

    /**
     * Finds an order by ID.
     */
    Optional<Order> findOrderById(Long id);

    /**
     * Finds an order by order number.
     */
    Optional<Order> findOrderByNumber(String orderNumber);

    /**
     * Gets all orders with pagination.
     */
    Page<Order> getAllOrders(Pageable pageable);

    /**
     * Gets orders by customer.
     */
    List<Order> getOrdersByCustomer(Long customerId);

    /**
     * Gets orders by status.
     */
    List<Order> getOrdersByStatus(OrderStatus status);

    /**
     * Gets orders created by user.
     */
    List<Order> getOrdersByCreatedBy(Long createdBy);

    /**
     * Searches orders by keyword with pagination.
     */
    Page<Order> searchOrders(String keyword, Pageable pageable);

    /**
     * Gets orders by date range.
     */
    List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Updates order status.
     */
    Order updateOrderStatus(Long id, OrderStatus status);

    /**
     * Processes order payment.
     */
    Order processPayment(Long orderId, String paymentMethod, BigDecimal amount);

    /**
     * Cancels an order.
     */
    Order cancelOrder(Long id, String reason);

    /**
     * Gets order count by status.
     */
    long getOrderCountByStatus(OrderStatus status);

    /**
     * Gets total revenue by order statuses.
     */
    Double getTotalRevenueByStatus(List<OrderStatus> statuses);

    /**
     * Checks if order number exists.
     */
    boolean existsByOrderNumber(String orderNumber);

    /**
     * Generates unique order number for new order.
     */
    String generateUniqueOrderNumber();

    /**
     * Calculates order total with tax and discounts.
     */
    BigDecimal calculateOrderTotal(Long orderId);

    /**
     * Updates order shipping information.
     */
    Order updateShippingInfo(Long orderId, String trackingNumber, LocalDateTime estimatedDelivery);

    /**
     * Bulk update order status.
     */
    List<Order> bulkUpdateStatus(List<Long> orderIds, OrderStatus status);
}