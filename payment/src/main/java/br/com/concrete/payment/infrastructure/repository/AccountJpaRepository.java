package br.com.concrete.payment.infrastructure.repository;

import br.com.concrete.payment.domain.entity.Account;
import br.com.concrete.payment.domain.repository.AccountRepository;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.UUID;

@RepositoryDefinition(domainClass = Account.class, idClass = UUID.class)
public interface AccountJpaRepository extends AccountRepository {
}
