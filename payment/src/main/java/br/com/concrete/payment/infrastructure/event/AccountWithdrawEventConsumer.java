package br.com.concrete.payment.infrastructure.event;

import br.com.concrete.AccountWithdraw;
import br.com.concrete.AccountWithdrawResult;
import br.com.concrete.payment.domain.business.UpdateBalance;
import br.com.concrete.payment.domain.event.EventConsumer;
import br.com.concrete.payment.domain.event.EventProducer;
import org.springframework.kafka.annotation.KafkaListener;

import javax.inject.Named;
import java.util.UUID;

import static br.com.concrete.AccountWithdrawStatus.APPROVED;
import static br.com.concrete.AccountWithdrawStatus.DENIED;

@Named
public class AccountWithdrawEventConsumer implements EventConsumer<AccountWithdraw> {

    private final UpdateBalance updateBalance;
    private final EventProducer<AccountWithdrawResult> eventProducer;

    public AccountWithdrawEventConsumer(
        UpdateBalance updateBalance,
        AccountWithdrawResultEventProducer eventProducer
    ) {
        this.updateBalance = updateBalance;
        this.eventProducer = eventProducer;
    }

    @Override
    @KafkaListener(topics = "${payment.topics.account-withdraw}")
    public void consume(AccountWithdraw accountWithdraw) {
        AccountWithdrawResult accountWithdrawResult = new AccountWithdrawResult();
        accountWithdrawResult.setAccountId(accountWithdraw.getAccountId());
        accountWithdrawResult.setOrderId(accountWithdraw.getOrderId());
        accountWithdrawResult.setAmount(accountWithdraw.getAmount());
        accountWithdrawResult.setRoomNumber(accountWithdraw.getRoomNumber());

        try {
            UUID accountId = UUID.fromString(accountWithdraw.getAccountId().toString());
            updateBalance.withdraw(accountId, accountWithdraw.getAmount());

            accountWithdrawResult.setStatus(APPROVED);
            accountWithdrawResult.setMessage("Success");
        } catch (RuntimeException runtimeException) {
            accountWithdrawResult.setStatus(DENIED);
            accountWithdrawResult.setMessage(runtimeException.getMessage());
        } finally {
            eventProducer.produce(accountWithdrawResult);
        }
    }
}
