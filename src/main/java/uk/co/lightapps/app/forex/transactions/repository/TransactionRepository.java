package uk.co.lightapps.app.forex.transactions.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.co.lightapps.app.forex.transactions.domain.Transaction;

/**
 * @author Asif Akhtar
 * 06/12/2020 01:37
 */
@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
}
