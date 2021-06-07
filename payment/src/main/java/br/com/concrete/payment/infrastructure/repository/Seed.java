package br.com.concrete.payment.infrastructure.repository;

import br.com.concrete.payment.domain.entity.Account;
import br.com.concrete.payment.domain.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class Seed {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UUID accountId = UUID.fromString("304648ff-6efa-4a75-81f3-d7718d06b2a5");

    @Bean
    public CommandLineRunner initDatabase(AccountRepository repository) {
        return args -> {
            if (!repository.findById(accountId).isPresent()) {
                logger.info("Seed account: " + repository.save(new Account(accountId, 6000.0)));
            }
        };
    }
}
