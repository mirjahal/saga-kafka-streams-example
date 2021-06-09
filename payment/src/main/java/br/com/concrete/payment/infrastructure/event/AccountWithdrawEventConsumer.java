package br.com.concrete.payment.infrastructure.event;

import br.com.concrete.AccountWithdraw;
import br.com.concrete.AccountWithdrawResult;
import br.com.concrete.payment.domain.business.UpdateBalance;
import br.com.concrete.payment.domain.event.EventConsumer;
import br.com.concrete.payment.domain.event.EventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

import javax.inject.Named;
import java.util.UUID;

import static br.com.concrete.AccountWithdrawStatus.APPROVED;
import static br.com.concrete.AccountWithdrawStatus.DENIED;

@Named
public class AccountWithdrawEventConsumer implements EventConsumer<AccountWithdraw> {

    private final UpdateBalance updateBalance;
    private final EventProducer<AccountWithdrawResult> eventProducer;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
        logger.info("Consuming account withdraw: " + accountWithdraw);

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
            logger.info("Exception when consuming account withdraw: " + runtimeException.getMessage());

            accountWithdrawResult.setStatus(DENIED);
            accountWithdrawResult.setMessage(runtimeException.getMessage());
        } finally {
            eventProducer.produce(accountWithdrawResult);
        }
    }
}
