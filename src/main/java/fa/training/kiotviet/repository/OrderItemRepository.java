package fa.training.kiotviet.repository;

import fa.training.kiotviet.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for OrderItem entity operations.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByProductId(Long productId);

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product.id = :productId")
    Integer getTotalQuantitySold(@Param("productId") Long productId);

    @Query("SELECT SUM(oi.totalPrice) FROM OrderItem oi WHERE oi.product.id = :productId")
    Double getTotalRevenueByProduct(@Param("productId") Long productId);
}