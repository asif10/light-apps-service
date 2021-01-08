package uk.co.lightapps.app.forex.account.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Asif Akhtar
 * 06/12/2020 01:50
 */
@Data
public class Account {
    private LocalDateTime startDate;
    private double deposited;
    private double opening;
    private double current;
    private double maxTradesThisWeek;
    private Figure profit;
    private Figure openProfit;
    private Figure openProfitIncFees;
    private double profitExclFees;
    private double fees;
    private Figure startPosition;
    private Figure currentPosition;
    private TradeStats totalTrades;
    private TradeStats tradesThisWeek;
    private TradeStats tradesThisMonth;
    private double tradesAvailableStart;
    private double tradesAvailableCurrent;
    private double profitThisWeek;
    private Figure returnPerTrade;
    private double tradesPerDay;
    private double returnPerDay;
    private double pipsPerDay;
    private double rrPerDay;
}
