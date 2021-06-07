package br.com.concrete.payment.domain.repository;

import br.com.concrete.payment.domain.entity.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

    Account save(Account account);
    Optional<Account> findById(UUID id);
    boolean existsById(UUID id);
}
