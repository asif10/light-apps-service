package uk.co.lightapps.app.forex.positions.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.co.lightapps.app.forex.account.domain.Figure;
import uk.co.lightapps.app.forex.trades.domain.Client;
import uk.co.lightapps.app.forex.trades.domain.Pair;
import uk.co.lightapps.app.forex.trades.domain.Trade;
import uk.co.lightapps.app.forex.trades.domain.TradeType;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private double change;
    private double profit;
    private double fees;
    private double totalProfit;
    private Figure position;
    private double trade;
    private double account;

    public static DailyPosition logged(LocalDate date, double opening, double change, double profitLoss, double fees, double totalProfitLoss, double position, double positionP, double trades, double account) {
        return new DailyPosition(UUID.randomUUID().toString(), date, opening, change, profitLoss, fees, totalProfitLoss, new Figure(position, positionP), trades, account);
    }
}
