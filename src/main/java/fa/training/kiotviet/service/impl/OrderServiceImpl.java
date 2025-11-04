package fa.training.kiotviet.service.impl;

import fa.training.kiotviet.model.Order;
import fa.training.kiotviet.enums.OrderStatus;
import fa.training.kiotviet.repository.OrderRepository;
import fa.training.kiotviet.service.OrderService;
import fa.training.kiotviet.util.GeneratorUtil;
import fa.training.kiotviet.exception.ResourceNotFoundException;
import fa.training.kiotviet.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for Order business logic operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public Order createOrder(Order order) {
        log.info("Creating new order");

        // Generate unique order number if not provided
        if (order.getOrderNumber() == null || order.getOrderNumber().isEmpty()) {
            order.setOrderNumber(generateUniqueOrderNumber());
        }

        // Check if order number already exists
        if (existsByOrderNumber(order.getOrderNumber())) {
            throw new BusinessRuleException("Order number already exists: " + order.getOrderNumber());
        }

        // Set default status if not provided
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }

        // Set order date if not provided
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }

        // Calculate total amount if not provided
        if (order.getTotalAmount() == null) {
            order.setTotalAmount(calculateOrderTotalFromItems(order));
        }

        // Set default values
        if (order.getPaidAmount() == null) {
            order.setPaidAmount(BigDecimal.ZERO);
        }

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {} and number: {}", savedOrder.getId(), savedOrder.getOrderNumber());
        return savedOrder;
    }

    @Override
    public Order updateOrder(Long id, Order order) {
        log.info("Updating order with ID: {}", id);

        Order existingOrder = findOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        // Don't allow updating order number
        order.setOrderNumber(existingOrder.getOrderNumber());

        // Update allowed fields
        existingOrder.setCustomer(order.getCustomer());
        existingOrder.setOrderItems(order.getOrderItems());
        existingOrder.setTotalAmount(order.getTotalAmount());
        existingOrder.setShippingAddress(order.getShippingAddress());
        existingOrder.setShippingFee(order.getShippingFee());
        existingOrder.setTaxAmount(order.getTaxAmount());
        existingOrder.setDiscountAmount(order.getDiscountAmount());
        existingOrder.setPaidAmount(order.getPaidAmount());
        existingOrder.setPaymentMethod(order.getPaymentMethod());
        existingOrder.setPaymentStatus(order.getPaymentStatus());
        existingOrder.setNotes(order.getNotes());

        Order updatedOrder = orderRepository.save(existingOrder);
        log.info("Order updated successfully with ID: {}", updatedOrder.getId());
        return updatedOrder;
    }

    @Override
    public void deleteOrder(Long id) {
        log.info("Deleting order with ID: {}", id);

        Order order = findOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        // Only allow deletion of orders in PENDING status
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessRuleException("Cannot delete order in status: " + order.getStatus());
        }

        orderRepository.delete(order);
        log.info("Order deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCreatedBy(Long createdBy) {
        return orderRepository.findByCreatedBy(createdBy);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> searchOrders(String keyword, Pageable pageable) {
        return orderRepository.searchOrders(keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByDateRange(startDate, endDate);
    }

    @Override
    public Order updateOrderStatus(Long id, OrderStatus status) {
        log.info("Updating order status for ID: {} to status: {}", id, status);

        Order order = findOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        // Validate status transitions
        validateStatusTransition(order.getStatus(), status);

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated successfully for ID: {}", id);
        return updatedOrder;
    }

    @Override
    public Order processPayment(Long orderId, String paymentMethod, BigDecimal amount) {
        log.info("Processing payment for order ID: {} amount: {}", orderId, amount);

        Order order = findOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessRuleException("Payment can only be processed for orders in PENDING status");
        }

        if (amount.compareTo(order.getTotalAmount()) != 0) {
            throw new BusinessRuleException("Payment amount must equal order total");
        }

        order.setPaymentMethod(paymentMethod);
        order.setPaidAmount(amount);
        order.setPaymentStatus("PAID");
        order.setStatus(OrderStatus.CONFIRMED);

        Order updatedOrder = orderRepository.save(order);
        log.info("Payment processed successfully for order ID: {}", orderId);
        return updatedOrder;
    }

    @Override
    public Order cancelOrder(Long id, String reason) {
        log.info("Cancelling order with ID: {}. Reason: {}", id, reason);

        Order order = findOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessRuleException("Cannot cancel delivered order");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessRuleException("Order is already cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setNotes(reason);

        Order updatedOrder = orderRepository.save(order);
        log.info("Order cancelled successfully with ID: {}", id);
        return updatedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public long getOrderCountByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalRevenueByStatus(List<OrderStatus> statuses) {
        return orderRepository.getTotalRevenueByStatus(statuses);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByOrderNumber(String orderNumber) {
        return orderRepository.existsByOrderNumber(orderNumber);
    }

    @Override
    public String generateUniqueOrderNumber() {
        String orderNumber;
        int attempts = 0;
        final int maxAttempts = 10;

        do {
            orderNumber = GeneratorUtil.generateOrderNumber();
            attempts++;
            if (attempts > maxAttempts) {
                throw new BusinessRuleException("Unable to generate unique order number after multiple attempts");
            }
        } while (existsByOrderNumber(orderNumber));

        return orderNumber;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateOrderTotal(Long orderId) {
        Order order = findOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        return calculateOrderTotalFromItems(order);
    }

    @Override
    public Order updateShippingInfo(Long orderId, String trackingNumber, LocalDateTime estimatedDelivery) {
        log.info("Updating shipping info for order ID: {}", orderId);

        Order order = findOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        // Update shipping information
        order.setNotes("Tracking: " + trackingNumber + ", Estimated delivery: " + estimatedDelivery);
        order.setDeliveryDate(estimatedDelivery);

        Order updatedOrder = orderRepository.save(order);
        log.info("Shipping info updated successfully for order ID: {}", orderId);
        return updatedOrder;
    }

    @Override
    public List<Order> bulkUpdateStatus(List<Long> orderIds, OrderStatus status) {
        log.info("Bulk updating status for {} orders to status: {}", orderIds.size(), status);

        List<Order> orders = orderRepository.findAllById(orderIds);

        for (Order order : orders) {
            validateStatusTransition(order.getStatus(), status);
            order.setStatus(status);
        }

        List<Order> updatedOrders = orderRepository.saveAll(orders);
        log.info("Bulk status update completed for {} orders", updatedOrders.size());
        return updatedOrders;
    }

    /**
     * Validates order status transitions.
     */
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == newStatus) {
            return; // No change needed
        }

        // Define valid transitions
        switch (currentStatus) {
            case PENDING:
                if (newStatus != OrderStatus.CONFIRMED && newStatus != OrderStatus.CANCELLED) {
                    throw new BusinessRuleException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case CONFIRMED:
                if (newStatus != OrderStatus.PROCESSING && newStatus != OrderStatus.CANCELLED) {
                    throw new BusinessRuleException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case PROCESSING:
                if (newStatus != OrderStatus.SHIPPED && newStatus != OrderStatus.CANCELLED) {
                    throw new BusinessRuleException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case SHIPPED:
                if (newStatus != OrderStatus.DELIVERED) {
                    throw new BusinessRuleException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case DELIVERED:
            case CANCELLED:
                throw new BusinessRuleException("Cannot change status from " + currentStatus);
        }
    }

    /**
     * Calculates order total from order items.
     */
    private BigDecimal calculateOrderTotalFromItems(Order order) {
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal itemsTotal = order.getOrderItems().stream()
                .map(item -> {
                    BigDecimal unitPrice = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
                    Integer quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                    BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
                    BigDecimal discountAmount = item.getDiscountAmount() != null ? item.getDiscountAmount() : BigDecimal.ZERO;
                    return lineTotal.subtract(discountAmount);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Add shipping fee and tax, subtract discount
        BigDecimal shippingFee = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        BigDecimal taxAmount = order.getTaxAmount() != null ? order.getTaxAmount() : BigDecimal.ZERO;
        BigDecimal discountAmount = order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO;

        return itemsTotal.add(shippingFee).add(taxAmount).subtract(discountAmount);
    }
}