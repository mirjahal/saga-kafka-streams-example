package br.com.concrete.payment.application;

import br.com.concrete.payment.domain.business.UpdateBalance;
import br.com.concrete.payment.domain.entity.Account;
import br.com.concrete.payment.domain.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.util.UUID;

@Named
public class UpdateBalanceImpl implements UpdateBalance {

    private final AccountRepository accountRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public UpdateBalanceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account withdraw(UUID id, double amount) {
        Account account = accountRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Account does not exist"));

        if (amount > account.getBalance()) {
            throw new RuntimeException("Insufficient account balance");
        }

        logger.info("Current balance: " + account.getBalance());
        logger.info("Amount to withdraw: " + amount);

        double newBalance = account.getBalance() - amount;
        account.setBalance(newBalance);

        logger.info("Balance updated for: " + newBalance);

        return accountRepository.save(account);
    }

    @Override
    public Account deposit(UUID id, double amount) {
        Account account = accountRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Account does not exist"));

        double newBalance = account.getBalance() + amount;
        account.setBalance(newBalance);

        return accountRepository.save(account);
    }
}
