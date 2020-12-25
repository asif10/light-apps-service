package uk.co.lightapps.app.forex.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.lightapps.app.forex.account.domain.Account;
import uk.co.lightapps.app.forex.account.domain.Figure;
import uk.co.lightapps.app.forex.account.domain.TradeStats;
import uk.co.lightapps.app.forex.trades.domain.Trade;
import uk.co.lightapps.app.forex.trades.services.TradeService;
import uk.co.lightapps.app.forex.transactions.services.TransactionService;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        account.setProfitThisWeek(calculateWeeklyProfit());

        setOpenProfit(account);
        return account;
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
        total.setLost(total.getTrades() - total.getWon());
        total.setWinRatio(total.getWon() / total.getTrades());
        return total;
    }

    private List<Trade> getTrades(LocalDateTime start, LocalDateTime end) {
        System.out.println(start + " - " + end);
        return tradeService.getAll().stream().filter(e -> e.getDate().compareTo(start) >= 0 && e.getDate().compareTo(end) <= 0).collect(Collectors.toList());
    }

    private LocalDateTime calculateStartOfWeek() {
        LocalDateTime firstSunday = LocalDateTime.now().with(DayOfWeek.SUNDAY);
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

    private double calculateAvailableTradesOnStart() {
        return calculateStartBalance() / 10;
    }

    private double calculateAvailableTrades() {
        return calculateCurrentBalance() / 10;
    }
}
