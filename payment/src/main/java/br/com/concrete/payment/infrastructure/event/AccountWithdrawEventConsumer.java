package br.com.concrete.payment.infrastructure.event;

import br.com.concrete.AccountWithdraw;
import br.com.concrete.AccountWithdrawResult;
import br.com.concrete.payment.domain.business.UpdateBalance;
import br.com.concrete.payment.domain.event.EventConsumer;
import org.springframework.kafka.annotation.KafkaListener;

import javax.inject.Named;
import java.util.UUID;

import static br.com.concrete.AccountWithdrawStatus.APPROVED;
import static br.com.concrete.AccountWithdrawStatus.DENIED;

@Named
public class AccountWithdrawEventConsumer implements EventConsumer<AccountWithdraw> {

    private final UpdateBalance updateBalance;
    private final AccountWithdrawResultEventProducer accountWithdrawResultEventProducer;

    public AccountWithdrawEventConsumer(
        UpdateBalance updateBalance,
        AccountWithdrawResultEventProducer accountWithdrawResultEventProducer
    ) {
        this.updateBalance = updateBalance;
        this.accountWithdrawResultEventProducer = accountWithdrawResultEventProducer;
    }

    @Override
    @KafkaListener(topics = "${payment.topics.account-withdraw}")
    public void consume(AccountWithdraw accountWithdraw) {
        AccountWithdrawResult accountWithdrawResult = new AccountWithdrawResult();
        accountWithdrawResult.setAccountId(accountWithdraw.getAccountId());
        accountWithdrawResult.setOrderId(accountWithdraw.getOrderId());
        accountWithdrawResult.setAmount(accountWithdraw.getAmount());

        try {
            UUID accountId = UUID.fromString(accountWithdraw.getAccountId().toString());
            updateBalance.withdraw(accountId, accountWithdraw.getAmount());

            accountWithdrawResult.setStatus(APPROVED);
            accountWithdrawResult.setMessage("Success");
        } catch (RuntimeException runtimeException) {
            accountWithdrawResult.setStatus(DENIED);
            accountWithdrawResult.setMessage(runtimeException.getMessage());
        } finally {
            accountWithdrawResultEventProducer.produce(accountWithdrawResult);
        }
    }
}
