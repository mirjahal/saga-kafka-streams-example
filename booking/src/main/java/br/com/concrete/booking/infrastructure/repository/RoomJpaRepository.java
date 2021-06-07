package br.com.concrete.booking.infrastructure.repository;

import br.com.concrete.booking.domain.entity.Room;
import br.com.concrete.booking.domain.repository.RoomRepository;
import org.springframework.data.repository.RepositoryDefinition;

@RepositoryDefinition(domainClass = Room.class, idClass = Integer.class)
public interface RoomJpaRepository extends RoomRepository {
}
