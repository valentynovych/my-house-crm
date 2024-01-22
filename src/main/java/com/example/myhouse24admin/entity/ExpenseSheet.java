package com.example.myhouse24admin.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "expense_sheets")
public class ExpenseSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;
    @Column(nullable = false)
    private boolean isProcessed;
    @Column(nullable = false)
    private BigDecimal amount;
    private String comment;
    @Column(nullable = false)
    private boolean deleted;
    @ManyToOne
    @JoinColumn(name = "payment_item_id", referencedColumnName = "id", nullable = false)
    private PaymentItem paymentItem;
    @ManyToOne
    @JoinColumn(name = "staff_id", referencedColumnName = "id", nullable = false)
    private Staff staff;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public PaymentItem getPaymentItem() {
        return paymentItem;
    }

    public void setPaymentItem(PaymentItem paymentItem) {
        this.paymentItem = paymentItem;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}
