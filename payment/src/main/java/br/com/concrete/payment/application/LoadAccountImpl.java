package br.com.concrete.payment.application;

import br.com.concrete.payment.domain.business.LoadAccount;
import br.com.concrete.payment.domain.entity.Account;
import br.com.concrete.payment.domain.repository.AccountRepository;

import javax.inject.Named;
import java.util.UUID;

@Named
public class LoadAccountImpl implements LoadAccount {

    private final AccountRepository accountRepository;

    public LoadAccountImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account get(UUID id) {
        return accountRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Account does not exist"));
    }
}
