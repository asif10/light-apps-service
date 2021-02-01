package uk.co.lightapps.app.forex.positions.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.co.lightapps.app.forex.account.domain.Figure;
import uk.co.lightapps.app.forex.account.domain.PLSplit;
import uk.co.lightapps.app.forex.account.domain.TradeStats;

import java.time.LocalDate;

/**
 * @author Asif Akhtar
 * 31/01/2021 02:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("MonthlyPositions")
public class MonthlyPosition {
    @Id
    private String positionId;
    private LocalDate date;
    private double start;
    private double end;
    private Figure profit;
    private double fees;
    private double total;
    private TradeStats stats;
    private double invested;
    private double roi;
    private double tradesPerDay;
    private double tradesPerWeek;
    private PLSplit winsSplit;
    private PLSplit lossesSplit;


}
