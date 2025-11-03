package fa.training.kiotviet.model;

import fa.training.kiotviet.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType; // IN, OUT, ADJUSTMENT, RETURN

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_cost", precision = 19, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "total_cost", precision = 19, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "reference_type")
    private String referenceType; // PURCHASE, SALE, ADJUSTMENT, RETURN

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "notes")
    private String notes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
        calculateTotalCost();
    }

    @PreUpdate
    protected void onUpdate() {
        calculateTotalCost();
    }

    private void calculateTotalCost() {
        if (unitCost != null && quantity != null) {
            totalCost = unitCost.multiply(BigDecimal.valueOf(quantity));
        }
    }
}