package br.com.concrete.payment.domain.business;

import br.com.concrete.payment.domain.entity.Account;

import java.util.UUID;

public interface UpdateBalance {

    Account withdraw(UUID id, double amount);
    Account deposit(UUID id, double amount);
}
