package uk.co.lightapps.app.forex.positions.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.co.lightapps.app.forex.positions.domain.MonthlyPosition;
import uk.co.lightapps.app.forex.positions.domain.WeeklyPosition;

/**
 * @author Asif Akhtar
 * 29/12/2020 20:37
 */
@Repository
public interface MonthlyPositionsRepository extends MongoRepository<MonthlyPosition, String> {
}
