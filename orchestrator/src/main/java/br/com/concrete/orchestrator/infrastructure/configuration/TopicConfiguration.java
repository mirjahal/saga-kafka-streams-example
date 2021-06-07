package br.com.concrete.orchestrator.infrastructure.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;

@ConstructorBinding
@ConfigurationProperties("orchestrator.topics")
public class TopicConfiguration {

    private final String orderCreated;
    private final String bookingCreate;
    private final String bookingResult;
    private final String orderCancel;
    private final String accountWithdraw;
    private final String accountWithdrawResult;
    private final String bookingCancel;
    private final String orderConfirm;
    private final String bookingConfirm;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public TopicConfiguration(
        String orderCreated,
        String bookingCreate,
        String bookingResult,
        String orderCancel,
        String accountWithdraw,
        String accountWithdrawResult,
        String bookingCancel,
        String orderConfirm,
        String bookingConfirm
    ) {
        this.orderCreated = orderCreated;
        this.bookingCreate = bookingCreate;
        this.bookingResult = bookingResult;
        this.orderCancel = orderCancel;
        this.accountWithdraw = accountWithdraw;
        this.accountWithdrawResult = accountWithdrawResult;
        this.bookingCancel = bookingCancel;
        this.orderConfirm = orderConfirm;
        this.bookingConfirm = bookingConfirm;
    }

    @Bean
    NewTopic createOrderCreatedTopic() {
        logger.info(String.format("Creating topic: %s", orderCreated));
        return new NewTopic(orderCreated, 1, (short) 1);
    }

    @Bean
    NewTopic createOrderCancelTopic() {
        logger.info(String.format("Creating topic: %s", orderCancel));
        return new NewTopic(orderCancel, 1, (short) 1);
    }

    @Bean
    NewTopic createBookingCreateTopic() {
        logger.info(String.format("Creating topic: %s", bookingCreate));
        return new NewTopic(bookingCreate, 1, (short) 1);
    }

    @Bean
    NewTopic createBookingResultTopic() {
        logger.info(String.format("Creating topic: %s", bookingResult));
        return new NewTopic(bookingResult, 1, (short) 1);
    }

    @Bean
    NewTopic createAccountWithdrawTopic() {
        logger.info(String.format("Creating topic: %s", accountWithdraw));
        return new NewTopic(accountWithdraw, 1, (short) 1);
    }

    @Bean
    NewTopic createAccountWithdrawResultTopic() {
        logger.info(String.format("Creating topic: %s", accountWithdrawResult));
        return new NewTopic(accountWithdrawResult, 1, (short) 1);
    }

    @Bean
    NewTopic createBookingCancelTopic() {
        logger.info(String.format("Creating topic: %s", bookingCancel));
        return new NewTopic(bookingCancel, 1, (short) 1);
    }

    @Bean
    NewTopic createOrderConfirmTopic() {
        logger.info(String.format("Creating topic: %s", orderConfirm));
        return new NewTopic(orderConfirm, 1, (short) 1);
    }

    @Bean
    NewTopic createBookingConfirmTopic() {
        logger.info(String.format("Creating topic: %s", bookingConfirm));
        return new NewTopic(bookingConfirm, 1, (short) 1);
    }

    public String getOrderCreatedTopic() {
        return orderCreated;
    }

    public String getBookingCreateTopic() {
        return bookingCreate;
    }

    public String getBookingResultTopic() {
        return bookingResult;
    }

    public String getOrderCancelTopic() {
        return orderCancel;
    }

    public String getAccountWithdrawTopic() {
        return accountWithdraw;
    }

    public String getAccountWithdrawResultTopic() {
        return accountWithdrawResult;
    }

    public String getBookingCancelTopic() {
        return bookingCancel;
    }

    public String getOrderConfirmTopic() {
        return orderConfirm;
    }

    public String getBookingConfirmTopic() {
        return bookingConfirm;
    }
}
