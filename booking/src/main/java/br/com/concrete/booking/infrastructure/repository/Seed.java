package br.com.concrete.booking.infrastructure.repository;

import br.com.concrete.booking.domain.entity.Room;
import br.com.concrete.booking.domain.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static br.com.concrete.booking.domain.entity.enums.RoomStatus.FREE;

@Configuration
public class Seed {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    public CommandLineRunner initDatabase(RoomRepository repository) {
        return args -> {
            if (repository.findAll().size() == 0){
                logger.info("Seed room: " + repository.save(new Room(102, null, 1000.0, FREE)));
                logger.info("Seed room: " + repository.save(new Room(103, null, 1000.0, FREE)));
            }
        };
    }
}
