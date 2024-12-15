package com.ejemplo.usuarios_api.dto;

import java.math.BigDecimal;

public class IndicadoresDTO {
    private BigDecimal totalPayments;
    private BigDecimal totalDebt;
    private BigDecimal currentMonthDebt;
    private LastTransactionDTO lastTransaction;

    // Constructor
    public IndicadoresDTO(BigDecimal totalPayments, BigDecimal totalDebt, BigDecimal currentMonthDebt, LastTransactionDTO lastTransaction) {
        this.totalPayments = totalPayments;
        this.totalDebt = totalDebt;
        this.currentMonthDebt = currentMonthDebt;
        this.lastTransaction = lastTransaction;
    }

    // Getters y Setters
    public BigDecimal getTotalPayments() {
        return totalPayments;
    }

    public void setTotalPayments(BigDecimal totalPayments) {
        this.totalPayments = totalPayments;
    }

    public BigDecimal getTotalDebt() {
        return totalDebt;
    }

    public void setTotalDebt(BigDecimal totalDebt) {
        this.totalDebt = totalDebt;
    }

    public BigDecimal getCurrentMonthDebt() {
        return currentMonthDebt;
    }

    public void setCurrentMonthDebt(BigDecimal currentMonthDebt) {
        this.currentMonthDebt = currentMonthDebt;
    }

    public LastTransactionDTO getLastTransaction() {
        return lastTransaction;
    }

    public void setLastTransaction(LastTransactionDTO lastTransaction) {
        this.lastTransaction = lastTransaction;
    }

    public static class LastTransactionDTO {
        private String date;
        private BigDecimal amount;

        public LastTransactionDTO(String date, BigDecimal amount) {
            this.date = date;
            this.amount = amount;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
}
