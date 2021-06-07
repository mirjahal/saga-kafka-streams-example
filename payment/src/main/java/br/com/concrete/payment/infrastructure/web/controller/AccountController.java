package br.com.concrete.payment.infrastructure.web.controller;

import br.com.concrete.payment.domain.business.LoadAccount;
import br.com.concrete.payment.domain.business.UpdateBalance;
import br.com.concrete.payment.domain.entity.Account;
import br.com.concrete.payment.infrastructure.web.model.request.UpdateBalanceRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("payments")
public class AccountController {

    private final UpdateBalance updateBalance;
    private final LoadAccount loadAccount;

    public AccountController(
        UpdateBalance updateBalance,
        LoadAccount loadAccount
    ) {
        this.updateBalance = updateBalance;
        this.loadAccount = loadAccount;
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Account> withdraw(
        @PathVariable("id") UUID id,
        @RequestBody UpdateBalanceRequest updateBalanceRequest
    ) {
        Account accountUpdated = updateBalance.withdraw(
            id,
            updateBalanceRequest.getAmount()
        );

        return ResponseEntity.ok(accountUpdated);
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<Account> deposit(
        @PathVariable("id") UUID id,
        @RequestBody UpdateBalanceRequest updateBalanceRequest
    ) {
        Account accountUpdated = updateBalance.deposit(
            id,
            updateBalanceRequest.getAmount()
        );

        return ResponseEntity.ok(accountUpdated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> get(@PathVariable("id") UUID id) {
        Account account = loadAccount.get(id);

        return ResponseEntity.ok(account);
    }
}
