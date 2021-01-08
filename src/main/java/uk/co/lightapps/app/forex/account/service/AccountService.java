package uk.co.lightapps.app.forex.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.lightapps.app.forex.Constant;
import uk.co.lightapps.app.forex.account.domain.Account;
import uk.co.lightapps.app.forex.account.domain.Figure;
import uk.co.lightapps.app.forex.account.domain.TradeStats;
import uk.co.lightapps.app.forex.trades.domain.Trade;
import uk.co.lightapps.app.forex.trades.services.TradeService;
import uk.co.lightapps.app.forex.transactions.services.TransactionService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.DayOfWeek.*;

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
        return tradeService.getAll().stream().mapToDouble(Trade::getProfit).sum();
    }

    public double sumTradesFeesValue() {
        return tradeService.getAll().stream().mapToDouble(Trade::getFees).sum();
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
        account.setTradesAvailableCurrent(calculateAvailableTrades());
        account.setTradesAvailableStart(calculateAvailableTradesOnStart());
        account.setProfitExclFees(account.getProfit().getValue() - sumTradesFeesValue());

        account.setTotalTrades(calculateTotalTrades());
        account.setTradesThisWeek(calculateWeeklyTrades());
        account.setTradesThisMonth(calculateWeeklyTrades());
        account.setProfitThisWeek(calculateWeeklyProfit());
        account.setReturnPerTrade(returnPerTrades(account));

        account.setMaxTradesThisWeek(calculateMaxTrades());
        account.setTradesPerDay(calculateTradesPerDay(account));

        List<Trade> trades = tradeService.getAll();

        account.setRrPerDay(calculateRrPerDay(account, trades));
        account.setPipsPerDay(calculatePipsPerDay(account, trades));
        account.setReturnPerDay(calculateReturnPerDay(account, trades));

        setOpenProfit(account);
        return account;
    }

    private double calculateReturnPerDay(Account account, List<Trade> trades) {
        long days = calculateBusinessDaysPassed();
        return account.getProfit().getValue() / days;
    }

    private double calculatePipsPerDay(Account account, List<Trade> trades) {
        long days = calculateBusinessDaysPassed();
        return account.getTotalTrades().getPips() / days;
    }

    private double calculateRrPerDay(Account account, List<Trade> trades) {
        long days = calculateBusinessDaysPassed();
        return account.getTotalTrades().getRr() / days;
    }

    private double calculateTradesPerDay(Account account) {
        long days = calculateBusinessDaysPassed();
        return account.getTotalTrades().getTrades() / days;

    }

    private long calculateBusinessDaysPassed() {
        return calculateBusinessDays(LocalDate.of(2021, 1, 1), LocalDate.now()) + 1;
    }

    private static long calculateBusinessDays(LocalDate startDate, LocalDate endDate) {
        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == SATURDAY || date.getDayOfWeek() == SUNDAY;

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        return Stream.iterate(startDate, date -> date.plusDays(1)).limit(daysBetween).filter(isWeekend.negate()).count();
    }

    private double calculateMaxTrades() {
        int day = LocalDate.now().getDayOfWeek().getValue();
        return Math.min(day, 5) * Constant.TRADES_PER_DAY;
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
        TradeStats total = new TradeStats();
        List<Trade> allTrades = tradeService.getAll();
        total.setTrades(allTrades.size());
        total.setWon(allTrades.stream().filter(e -> e.getProfit() > 0).count());
        total.setRr(allTrades.stream().mapToDouble(Trade::getRr).sum());
        total.setPips(allTrades.stream().mapToDouble(Trade::getPips).sum());
        total.setLost(total.getTrades() - total.getWon());
        total.setWinRatio(total.getWon() / total.getTrades());
        return total;
    }

    private List<Trade> getTrades(LocalDateTime start, LocalDateTime end) {
        System.out.println(start + " - " + end);
        return tradeService.getAll().stream().filter(e -> e.getDate().compareTo(start) >= 0 && e.getDate().compareTo(end) <= 0).collect(Collectors.toList());
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
        total.setWinRatio(total.getWon() / total.getTrades());
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

    private double calculateAvailableTrades() {
        double profit = calculateProfit().getValue();
        List<Trade> trades = tradeService.getAll();
        double returnPerTrade = profit / trades.size();
        return Math.abs(calculateCurrentBalance() / returnPerTrade);
    }
}
