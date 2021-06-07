package br.com.concrete.payment.domain.business;

import br.com.concrete.payment.domain.entity.Account;

import java.util.UUID;

public interface LoadAccount {

    Account get(UUID id);
}
