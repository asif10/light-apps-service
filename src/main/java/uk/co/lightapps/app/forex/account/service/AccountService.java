package uk.co.lightapps.app.forex.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.lightapps.app.forex.account.domain.Account;
import uk.co.lightapps.app.forex.account.domain.Figure;
import uk.co.lightapps.app.forex.account.domain.TradeStats;
import uk.co.lightapps.app.forex.trades.domain.Trade;
import uk.co.lightapps.app.forex.trades.services.TradeService;
import uk.co.lightapps.app.forex.transactions.services.TransactionService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.DayOfWeek.*;
import static uk.co.lightapps.app.shared.CommonUtils.TRADES_PER_DAY;
import static uk.co.lightapps.app.shared.CommonUtils.calculateBusinessDays;

/**
 * @author Asif Akhtar
 * 06/12/2020 01:42
 */
@Service
@RequiredArgsConstructor
public class AccountService {
    private final TradeService tradeService;
    private final TransactionService transactionService;

    public double calculateCurrentBalance() {
        return calculateStartBalance() + sumTradesReturnValue() + sumTradesFeesValue();
    }

    public double calculateStartBalance() {
        return transactionService.getBalance();
    }

    public double sumTradesReturnValue() {
        return tradeService.findAll().stream().mapToDouble(Trade::getProfit).sum();
    }

    public double sumTradesFeesValue() {
        return tradeService.findAll().stream().mapToDouble(Trade::getFees).sum();
    }

    public double calculateDepositAmount() {
        return transactionService.getDepositBalance();
    }

    public Figure calculateCurrentPosition() {
        Figure figure = new Figure();
        figure.setValue(calculateCurrentBalance() - calculateDepositAmount());
        figure.setPercentage(figure.getValue() / calculateDepositAmount());
        return figure;
    }

    public Figure calculateStartPosition() {
        Figure figure = new Figure();
        figure.setValue(calculateStartBalance() - calculateDepositAmount());
        figure.setPercentage(figure.getValue() / calculateDepositAmount());
        return figure;
    }

    public Figure calculateProfit() {
        Figure figure = new Figure();
        figure.setValue(calculateCurrentBalance() - calculateStartBalance());
        figure.setPercentage(figure.getValue() / calculateStartBalance());
        return figure;
    }

    public Account getAccountInfo() {
        Account account = new Account();
        account.setDeposited(calculateDepositAmount());
        account.setProfit(calculateProfit());
        account.setOpening(calculateStartBalance());
        account.setCurrent(calculateCurrentBalance());
        account.setFees(sumTradesFeesValue());
        account.setCurrentPosition(calculateCurrentPosition());
        account.setStartPosition(calculateStartPosition());
        account.setTradesAvailableStart(calculateAvailableTradesOnStart());
        account.setProfitExclFees(account.getProfit().getValue() - sumTradesFeesValue());

        account.setTotalTrades(calculateTotalTrades());
        account.setTradesThisWeek(calculateWeeklyTrades());
        account.setTradesThisMonth(calculateMonthlyTrades());
        account.setProfitThisWeek(calculateWeeklyProfit());
        account.setReturnPerTrade(returnPerTrades(account));

        account.setMaxTradesThisWeek(calculateMaxTradesThisWeek());
        calculateMaxTrades(account);
        account.setTradesPerDay(calculateTradesPerDay(account));

        account.setTradesPerWeek(calculateTradesPerWeek(account));

        List<Trade> trades = tradeService.findAll();

        account.setRrPerDay(calculateRrPerDay(account, trades));
        account.setRrPerWeek(calculateRrPerWeek(account, trades));
        account.setPipsPerDay(calculatePipsPerDay(account, trades));
        account.setPipsPerWeek(calculatePipsPerWeek(account, trades));
        account.setReturnPerDay(calculateReturnPerDay(account, trades));
        account.setReturnPerWeek(calculateReturnPerWeek(account, trades));

        calculateRRPerProfitLoss(account, trades);

        setOpenProfit(account);
        return account;
    }

    public Account getAccountInfo(LocalDate date) {
        Account account = getAccountInfo();
//        account.setDeposited(calculateDepositAmount());
//        account.setProfit(calculateProfit());
//        account.setOpening(calculateStartBalance());
//        account.setCurrent(calculateCurrentBalance());
//        account.setFees(sumTradesFeesValue());
//        account.setCurrentPosition(calculateCurrentPosition());
//        account.setStartPosition(calculateStartPosition());
//        account.setTradesAvailableStart(calculateAvailableTradesOnStart());
//        account.setProfitExclFees(account.getProfit().getValue() - sumTradesFeesValue());

        account.setTotalTrades(calculateTotalTrades(date));
        account.setTradesThisWeek(calculateWeeklyTrades());
        account.setTradesThisMonth(calculateMonthlyTrades());
        account.setProfitThisWeek(calculateWeeklyProfit());
        account.setReturnPerTrade(returnPerTrades(account));

        account.setMaxTradesThisWeek(calculateMaxTradesThisWeek());
        calculateMaxTrades(account);
        account.setTradesPerDay(calculateTradesPerDay(account, date));

        account.setTradesPerWeek(calculateTradesPerWeek(account));

//        List<Trade> trades = tradeService.getAll();
//
//        account.setRrPerDay(calculateRrPerDay(account, trades));
//        account.setRrPerWeek(calculateRrPerWeek(account, trades));
//        account.setPipsPerDay(calculatePipsPerDay(account, trades));
//        account.setPipsPerWeek(calculatePipsPerWeek(account, trades));
//        account.setReturnPerDay(calculateReturnPerDay(account, trades));
//        account.setReturnPerWeek(calculateReturnPerWeek(account, trades));
//
//        calculateRRPerProfitLoss(account, trades);
//
//        setOpenProfit(account);
        return account;
    }

    private class TradeData {
        int profit;
        int loss;
        double profitRR;
        double lossRR;
    }

    private void calculateRRPerProfitLoss(Account account, List<Trade> trades) {
        TradeData tradeData = new TradeData();

        trades.forEach(trade -> {
            if (trade.getProfit() > 0) {
                tradeData.profit += 1;
                tradeData.profitRR += trade.getRr();
            } else if (trade.getProfit() < 0) {
                tradeData.loss += 1;
                tradeData.lossRR += trade.getRr();
            }
        });

        account.setAverageRrPerLoss(tradeData.lossRR / tradeData.loss);
        account.setAverageRrPerProfit(tradeData.profitRR / tradeData.profit);
    }

    private double calculateReturnPerDay(Account account, List<Trade> trades) {
        long days = calculateBusinessDaysPassed();
        return account.getProfit().getValue() / days;
    }

    private double calculateReturnPerWeek(Account account, List<Trade> trades) {
        long weeks = calculateWeeksPassed();
        return account.getProfit().getValue() / weeks;
    }

    private double calculatePipsPerDay(Account account, List<Trade> trades) {
        long days = calculateBusinessDaysPassed();
        return account.getTotalTrades().getPips() / days;
    }

    private double calculatePipsPerWeek(Account account, List<Trade> trades) {
        long weeks = calculateWeeksPassed();
        return account.getTotalTrades().getPips() / weeks;
    }

    private double calculateRrPerDay(Account account, List<Trade> trades) {
        long days = calculateBusinessDaysPassed();
        return account.getTotalTrades().getRr() / days;
    }

    private double calculateRrPerWeek(Account account, List<Trade> trades) {
        long weeks = calculateWeeksPassed();
        return account.getTotalTrades().getRr() / weeks;
    }

    private double calculateTradesPerDay(Account account) {
        long days = calculateBusinessDaysPassed() + 1;
        return (double) account.getTotalTrades().getTrades() / days;
    }

    private double calculateTradesPerDay(Account account, LocalDate date) {
        long days = calculateBusinessDays(date, endOfMonth(date));
        return (double) account.getTotalTrades().getTrades() / days;
    }

    private double calculateTradesPerWeek(Account account) {
        long weeks = calculateWeeksPassed();
        return (double) account.getTotalTrades().getTrades() / weeks;
    }

    private long calculateWeeksPassed() {
        long days = calculateBusinessDaysPassed();
        return days / 5;
    }

    private long calculateBusinessDaysPassed() {
        return calculateBusinessDays(LocalDate.of(2021, 1, 1), LocalDate.now());
    }

    private double calculateMaxTradesThisWeek() {
        int day = LocalDate.now().getDayOfWeek().getValue();
        return Math.min(day, 5) * TRADES_PER_DAY;
    }

    private void calculateMaxTrades(Account account) {
        long days = calculateBusinessDaysPassed() + 1;
        long total = days * TRADES_PER_DAY;
        account.setMaxTrades(new Figure(total, (double) account.getTotalTrades().getTrades() / total));
    }

    private Figure returnPerTrades(Account account) {
        double returned = account.getProfit().getValue() / account.getTotalTrades().getTrades();
        return new Figure(returned, returned / account.getCurrent());
    }

    private void setOpenProfit(Account account) {
        List<Trade> openTrades = tradeService.getOpenTrades();
        double profit = openTrades.stream().mapToDouble(Trade::getProfit).sum();
        double fees = openTrades.stream().mapToDouble(Trade::getFees).sum();

        account.setOpenProfit(new Figure(profit, 0));
        account.setOpenProfitIncFees(new Figure(profit + fees, 0));
    }

    private TradeStats calculateTotalTrades() {
        return calculateTotalTrades(tradeService.findAll());
    }

    private TradeStats calculateTotalTrades(LocalDate date) {
        return calculateTotalTrades(tradeService.findAll(date, endOfMonth(date)));
    }

    private LocalDate endOfMonth(LocalDate date) {
        return date.plusMonths(1).minusDays(1);
    }

    private TradeStats calculateTotalTrades(List<Trade> allTrades) {
        TradeStats total = new TradeStats();
        total.setTrades(allTrades.size());
        total.setWon(allTrades.stream().filter(e -> e.getProfit() > 0).count());
        total.setRr(allTrades.stream().mapToDouble(Trade::getRr).sum());
        total.setPips(allTrades.stream().mapToDouble(Trade::getPips).sum());
        total.setLost(total.getTrades() - total.getWon());
        total.setWinRatio((double) total.getWon() / total.getTrades());
        return total;
    }

    private List<Trade> getTrades(LocalDateTime start, LocalDateTime end) {
        LocalDateTime startDate = LocalDateTime.of(start.toLocalDate(), LocalTime.MIN);
        LocalDateTime endDate = LocalDateTime.of(end.toLocalDate(), LocalTime.MAX);
        return tradeService.findAll().stream().filter(e -> e.getDate().compareTo(startDate) >= 0 && e.getDate().compareTo(endDate) <= 0).collect(Collectors.toList());
    }

    private LocalDateTime calculateStartOfWeek() {
        LocalDateTime firstSunday = LocalDateTime.now().with(SUNDAY);
        LocalDateTime startOfWeek;
        if (firstSunday.isAfter(LocalDateTime.now())) {
            startOfWeek = firstSunday.minusDays(7);
        } else {
            startOfWeek = firstSunday;
        }

        return startOfWeek;
    }

    private TradeStats calculateWeeklyTrades() {
        LocalDateTime startOfWeek = calculateStartOfWeek();
        LocalDateTime endOfWeek = startOfWeek.plusDays(5);

        TradeStats total = new TradeStats();
        List<Trade> allTrades = getTrades(startOfWeek, endOfWeek);
        total.setTrades(allTrades.size());
        total.setWon(allTrades.stream().filter(e -> e.getProfit() > 0).count());
        total.setLost(total.getTrades() - total.getWon());
        if (total.getWon() > 0) {
            total.setWinRatio((double) total.getWon() / total.getTrades());
        }
        return total;
    }

    private TradeStats calculateMonthlyTrades() {
        LocalDateTime startOfWeek = LocalDateTime.now().withDayOfMonth(1);
        LocalDateTime endOfWeek = startOfWeek.withDayOfMonth(startOfWeek.toLocalDate().lengthOfMonth());

        TradeStats total = new TradeStats();
        List<Trade> allTrades = getTrades(startOfWeek, endOfWeek);
        total.setTrades(allTrades.size());
        total.setWon(allTrades.stream().filter(e -> e.getProfit() > 0).count());
        total.setLost(total.getTrades() - total.getWon());
        total.setWinRatio((double) total.getWon() / total.getTrades());
        return total;
    }

    private double calculateWeeklyProfit() {
        LocalDateTime startOfWeek = calculateStartOfWeek();
        LocalDateTime endOfWeek = startOfWeek.plusDays(5);
        List<Trade> allTrades = getTrades(startOfWeek, endOfWeek);
        double profit = allTrades.stream().mapToDouble(Trade::getProfit).sum();
        double fees = allTrades.stream().mapToDouble(Trade::getFees).sum();
        return profit + fees;
    }

    /**
     * Hard coded from excel, using losses off first days trades
     *
     * @return
     */
    private double calculateAvailableTradesOnStart() {
        return 33968;
    }


}
