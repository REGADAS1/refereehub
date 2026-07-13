package com.regadas.refereehub.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false, unique = true)
    private Match match;

    @Column(name = "fee_amount", nullable = false)
    private BigDecimal feeAmount;

    @Column(name = "paid", nullable = false)
    private boolean paid;

    @Column(name = "paid_at")
    private LocalDate paidAt;

    @Column(name = "kilometers")
    private BigDecimal kilometers;

    @Column(name = "km_rate")
    private BigDecimal kmRate;

    @Column(name = "night_subsidy_applied", nullable = false)
    private boolean nightSubsidyApplied;

    @Column(name = "night_subsidy_amount")
    private BigDecimal nightSubsidyAmount;

    @Column(name = "notes")
    private String notes;

    public Payment() {
    }

    public Long getId() {
        return id;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public LocalDate getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDate paidAt) {
        this.paidAt = paidAt;
    }

    public BigDecimal getKilometers() {
        return kilometers;
    }

    public void setKilometers(BigDecimal kilometers) {
        this.kilometers = kilometers;
    }

    public BigDecimal getKmRate() {
        return kmRate;
    }

    public void setKmRate(BigDecimal kmRate) {
        this.kmRate = kmRate;
    }

    public boolean isNightSubsidyApplied() {
        return nightSubsidyApplied;
    }

    public void setNightSubsidyApplied(boolean nightSubsidyApplied) {
        this.nightSubsidyApplied = nightSubsidyApplied;
    }

    public BigDecimal getNightSubsidyAmount() {
        return nightSubsidyAmount;
    }

    public void setNightSubsidyAmount(BigDecimal nightSubsidyAmount) {
        this.nightSubsidyAmount = nightSubsidyAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}