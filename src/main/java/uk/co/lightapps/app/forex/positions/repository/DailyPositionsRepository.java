package uk.co.lightapps.app.forex.positions.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.co.lightapps.app.forex.positions.domain.DailyPosition;

/**
 * @author Asif Akhtar
 * 10/12/2020 23:47
 */
@Repository
public interface DailyPositionsRepository extends MongoRepository<DailyPosition, String> {
}
