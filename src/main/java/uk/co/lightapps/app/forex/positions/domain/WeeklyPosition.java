package uk.co.lightapps.app.forex.positions.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.co.lightapps.app.forex.account.domain.Figure;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * @author Asif Akhtar
 * 10/12/2020 23:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("WeeklyPositions")
public class WeeklyPosition {
    @Id
    private String positionId;
    private LocalDate date;
    private double start;
    private double end;
    private Figure profit;
    private double fees;
    private double total;
    private double tradesAvailable;
    private long trades;
    private long won;
    private long lost;
    private double ratio;
    private double rr;
    private double invested;
    private double roi;
    private double returnPerTrade;
    private double totalPosition;

    public void calculate() {
        double profit = end - start;
        double perc = new BigDecimal("" + profit).divide(new BigDecimal("" + start), 10, RoundingMode.HALF_UP).doubleValue();
        setProfit(new Figure(profit, perc));
    }
}
