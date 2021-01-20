package uk.co.lightapps.app.forex.positions.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.co.lightapps.app.forex.account.domain.Figure;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Asif Akhtar
 * 10/12/2020 23:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("DailyPositions")
public class DailyPosition {
    @Id
    private String positionId;
    private LocalDate date;
    private double opening;
    private double difference;
    private double totalDifference;
    private double profit;
    private double fees;
    private double totalProfit;
    private Figure position;
    private double trade;
    private double account;
    private double perTrade;

    public static DailyPosition logged(LocalDate date, double opening, double difference, double totalDifference, double profitLoss, double fees, double totalProfitLoss, double position, double positionP, double trades, double account, double perTrade) {
        return new DailyPosition(UUID.randomUUID().toString(), date, opening, difference, totalDifference, profitLoss, fees, totalProfitLoss, new Figure(position, positionP), trades, account, perTrade);
    }
}
