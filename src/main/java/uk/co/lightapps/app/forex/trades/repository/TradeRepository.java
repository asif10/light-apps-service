package uk.co.lightapps.app.forex.trades.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.co.lightapps.app.forex.trades.domain.Trade;

/**
 * @author Asif Akhtar
 * 26/10/2020 18:32
 */
@Repository
public interface TradeRepository extends MongoRepository<Trade, String> {
}
