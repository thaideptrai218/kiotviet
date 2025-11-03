package fa.training.kiotviet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_code", unique = true, nullable = false)
    private String customerCode;

    @Column(nullable = false)
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String email;

    private String address;

    private String city;

    private String district;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 0;

    @Column(name = "credit_limit")
    private java.math.BigDecimal creditLimit = java.math.BigDecimal.ZERO;

    @Column(name = "current_balance")
    private java.math.BigDecimal currentBalance = java.math.BigDecimal.ZERO;

    @Column(name = "is_active")
    private Boolean active = true;

    @Column(name = "notes")
    private String notes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;
}