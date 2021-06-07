package br.com.concrete.payment.infrastructure.web.model.request;

public class UpdateBalanceRequest {

    private double amount;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
