package uk.co.lightapps.app.forex.history.domain;

import lombok.Data;
import uk.co.lightapps.app.forex.account.domain.Figure;
import uk.co.lightapps.app.forex.account.domain.TradeStats;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static uk.co.lightapps.app.shared.CommonUtils.calculateBusinessDays;

/**
 * @author Asif Akhtar
 * 04/02/2021 20:23
 */
@Data
public class MonthlySnapshot {
    private LocalDate month;
    private TradeStats stats = new TradeStats();
    private TradeStats previous = new TradeStats();
    private long maxTrades;
    private double fees;
    private Figure profit;
    private double invested;
    private Figure manualWins;
    private Figure tpWins;
    private double open;
    private double closed;
    private double tradesPerDay;
    private double tradesPerWeek;

    public MonthlySnapshot(LocalDate month) {
        this.month = month;
    }

    public void calculate() {
        stats.setWinRatio((double) stats.getWon() / stats.getTrades());
        previous.setWinRatio((double) previous.getWon() / previous.getTrades());
    }
}
