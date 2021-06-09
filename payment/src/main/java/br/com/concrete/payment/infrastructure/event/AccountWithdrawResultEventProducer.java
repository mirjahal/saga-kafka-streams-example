package br.com.concrete.payment.infrastructure.event;

import br.com.concrete.AccountWithdrawResult;
import br.com.concrete.payment.domain.event.EventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import javax.inject.Named;

@Named
public class AccountWithdrawResultEventProducer implements EventProducer<AccountWithdrawResult> {

    private final KafkaTemplate<String, AccountWithdrawResult> kafkaTemplate;
    private final String accountWithdrawResultTopic;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AccountWithdrawResultEventProducer(
        KafkaTemplate<String, AccountWithdrawResult> kafkaTemplate,
        @Value("${payment.topics.account-withdraw-result}") String accountWithdrawResultTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.accountWithdrawResultTopic = accountWithdrawResultTopic;
    }

    @Override
    public void produce(AccountWithdrawResult accountWithdrawResult) {
        logger.info("Producing account withdraw result: " + accountWithdrawResult);

        kafkaTemplate.send(accountWithdrawResultTopic, accountWithdrawResult);
    }
}
