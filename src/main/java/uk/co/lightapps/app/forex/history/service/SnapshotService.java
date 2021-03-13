package uk.co.lightapps.app.forex.history.service;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;
import uk.co.lightapps.app.forex.account.domain.Figure;
import uk.co.lightapps.app.forex.account.service.AccountService;
import uk.co.lightapps.app.forex.history.domain.MonthlySnapshot;
import uk.co.lightapps.app.forex.history.domain.Snapshot;
import uk.co.lightapps.app.forex.positions.domain.MonthlyPosition;
import uk.co.lightapps.app.forex.positions.domain.WeeklyPosition;
import uk.co.lightapps.app.forex.positions.service.PositionsService;
import uk.co.lightapps.app.forex.trades.domain.Trade;
import uk.co.lightapps.app.forex.trades.services.TradeService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static uk.co.lightapps.app.shared.CommonUtils.TRADES_PER_DAY;
import static uk.co.lightapps.app.shared.CommonUtils.calculateBusinessDays;

/**
 * @author Asif Akhtar
 * 18/01/2021 18:54
 */
@Service
@RequiredArgsConstructor
public class SnapshotService {
    private final PositionsService positionsService;
    private final TradeService tradeService;
    private final AccountService accountService;

    public Snapshot calculateLastWeek() {
        WeeklyPosition weeklyPosition = positionsService.getWeek().orElseThrow();
        Snapshot snapshot = new Snapshot();
        List<Trade> trades = tradeService.findAll(LocalDate.of(2021, 1, 1), weeklyPosition.getDate());
        double wins = trades.stream().filter(e -> e.getProfit() > 0).mapToDouble(Trade::getProfit).count();
        double losses = trades.stream().filter(e -> e.getProfit() < 0).mapToDouble(Trade::getProfit).count();
        double pips = trades.stream().mapToDouble(Trade::getPips).sum();
        double rr = trades.stream().mapToDouble(Trade::getRr).sum();

        snapshot.setWinRatio(wins / trades.size());
        snapshot.setWins(wins);
        snapshot.setLosses(losses);
        snapshot.setPips(pips);
        snapshot.setRr(rr);
        return snapshot;
    }

    public MonthlySnapshot currentMonth() {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        return createMonthlySnapshot(start, end);
    }

    public MonthlySnapshot previousMonth() {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate previousStart = start.minusMonths(1);
        LocalDate previousEnd = previousStart.plusMonths(1).minusDays(1);
        return createMonthlySnapshot(previousStart, previousEnd);
    }

    public MonthlySnapshot createMonthlySnapshot(LocalDate start, LocalDate end) {
        MonthlySnapshot month = new MonthlySnapshot(start);
        List<Trade> trades = tradeService.findAll(start, end);
        List<Trade> wins = trades.stream().filter(e -> e.getProfit() > 0).collect(Collectors.toList());
        List<Trade> losses = trades.stream().filter(e -> e.getProfit() < 0).collect(Collectors.toList());
        double rr = trades.stream().mapToDouble(Trade::getRr).sum();
        double pips = trades.stream().mapToDouble(Trade::getPips).sum();
        double fees = trades.stream().mapToDouble(Trade::getFees).sum();
        double invested = trades.stream().mapToDouble(Trade::getOpen).sum();

        month.getStats().setTrades(trades.size());
        month.getStats().setWon(wins.size());
        month.getStats().setLost(losses.size());
        month.getStats().setRr(rr);
        month.getStats().setPips(pips);
        month.setFees(fees);
        month.setInvested(invested);

        double open = calculateBalanceTo(start);
        double closed = calculateBalanceTo(end);
        double profit = closed - open;
        month.setProfit(new Figure(profit, profit / invested));
        month.setOpen(open);
        month.setClosed(closed);

        List<Trade> manualWins = trades.stream().filter(e -> e.getProfit() > 0 && e.getStatus().equals("MANUAL")).collect(Collectors.toList());
        List<Trade> tpWins = trades.stream().filter(e -> e.getProfit() > 0 && e.getStatus().equals("TP")).collect(Collectors.toList());

        long closedWins = manualWins.size() + tpWins.size();

        month.setManualWins(new Figure(manualWins.size(), (double) manualWins.size() / closedWins));
        month.setTpWins(new Figure(tpWins.size(), (double) tpWins.size() / closedWins));

        MonthlyPosition currentMonth = positionsService.getMonth(start).orElse(new MonthlyPosition());
//        month.getPrevious().setTrades(currentMonth.getStats().getTrades());
//        month.getPrevious().setWon(currentMonth.getStats().getWon());
//        month.getPrevious().setLost(currentMonth.getStats().getLost());

        long days = calculateBusinessDays(start, LocalDate.now()) + 1;
        month.setMaxTrades(calculateBusinessDays(start, end) * TRADES_PER_DAY);

        month.setTradesPerDay((double) trades.size() / days);

        int monthValue = start.getMonthValue();

        List<WeeklyPosition> weeklies = positionsService.getWeeklyPositions().stream().filter(e -> e.getDate().getMonthValue() == monthValue).collect(Collectors.toList());
        double average = weeklies.stream().mapToLong(WeeklyPosition::getTrades).average().orElse(0);
        month.setTradesPerWeek(average);
        month.calculate();
        return month;
    }

    private double calculateBalanceTo(LocalDate start) {
        List<Trade> trades = tradeService.findAll(LocalDate.now().minusYears(2), start.minusDays(1));
        double totalProfit = trades.stream().mapToDouble(Trade::getProfit).sum();
        double fees = trades.stream().mapToDouble(Trade::getFees).sum();
        return accountService.calculateStartBalance() + totalProfit + fees;
    }
}
