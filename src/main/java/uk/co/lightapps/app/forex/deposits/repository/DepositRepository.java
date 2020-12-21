package uk.co.lightapps.app.forex.deposits.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.co.lightapps.app.forex.deposits.domain.Deposit;

/**
 * @author Asif Akhtar
 * 06/12/2020 01:37
 */
@Repository
public interface DepositRepository extends MongoRepository<Deposit, String> {
}
