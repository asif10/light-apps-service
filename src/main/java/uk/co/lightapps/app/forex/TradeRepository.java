package uk.co.lightapps.app.forex;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Asif Akhtar
 * 26/10/2020 18:32
 */
@Repository
public interface TradeRepository extends MongoRepository<Trade, String> {
}
