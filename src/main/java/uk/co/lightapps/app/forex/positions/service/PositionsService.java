package uk.co.lightapps.app.forex.positions.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.lightapps.app.forex.account.domain.Account;
import uk.co.lightapps.app.forex.account.domain.Figure;
import uk.co.lightapps.app.forex.account.domain.PLSplit;
import uk.co.lightapps.app.forex.account.domain.TradeStats;
import uk.co.lightapps.app.forex.account.service.AccountService;
import uk.co.lightapps.app.forex.decay.domain.Decay;
import uk.co.lightapps.app.forex.decay.domain.DecayOptions;
import uk.co.lightapps.app.forex.decay.services.DecayService;
import uk.co.lightapps.app.forex.positions.domain.DailyPosition;
import uk.co.lightapps.app.forex.positions.domain.MonthlyPosition;
import uk.co.lightapps.app.forex.positions.domain.WeeklyPosition;
import uk.co.lightapps.app.forex.positions.repository.DailyPositionsRepository;
import uk.co.lightapps.app.forex.positions.repository.MonthlyPositionsRepository;
import uk.co.lightapps.app.forex.positions.repository.WeeklyPositionsRepository;
import uk.co.lightapps.app.forex.trades.domain.Trade;
import uk.co.lightapps.app.forex.trades.services.TradeService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoField.*;
import static uk.co.lightapps.app.shared.CommonUtils.*;

/**
 * @author Asif Akhtar
 * 10/12/2020 23:46
 */
@Service
@RequiredArgsConstructor
public class PositionsService {
    private final DailyPositionsRepository dailyRepository;
    private final WeeklyPositionsRepository weeklyRepository;
    private final MonthlyPositionsRepository monthlyPositionsRepository;
    private final AccountService accountService;
    private final DecayService decayService;
    private final TradeService tradeService;

    public void add(DailyPosition position) {
        dailyRepository.save(position);
    }

    public void add(WeeklyPosition position) {
        weeklyRepository.save(position);
    }

    public static LocalDate getLastWorkingDayOfMonth(LocalDate date) {
        LocalDate lastDayOfMonth;
        switch (DayOfWeek.of(date.get(DAY_OF_WEEK))) {
            case SATURDAY:
                lastDayOfMonth = date.minusDays(1);
                break;
            case SUNDAY:
                lastDayOfMonth = date.minusDays(2);
                break;
            default:
                lastDayOfMonth = date;
        }
        return lastDayOfMonth;
    }

    public List<DailyPosition> getDailyPositions() {
        return dailyRepository.findAll();
    }

    public List<DailyPosition> getDailyPositionsThisMonth() {
        LocalDate start = getLastWorkingDayOfMonth(LocalDate.now().withDayOfMonth(1).minusDays(1));
        LocalDate end = start.plusMonths(1);

        return getDailyPositions(start, end);
    }

    public List<DailyPosition> getDailyPositions(LocalDate start, LocalDate end) {
        return dailyRepository.findAll().stream().filter(e -> e.getDate().compareTo(start) >= 0 && e.getDate().compareTo(end) < 1).collect(Collectors.toList());
    }

    public List<WeeklyPosition> getWeeklyPositions() {
        return weeklyRepository.findAll();
    }

    public List<WeeklyPosition> getCurrentWeeklyPositions() {
        List<WeeklyPosition> all = weeklyRepository.findAll();
        Optional<WeeklyPosition> thisWeek = all.stream().filter(e -> e.getDate().equals(startOfWeek())).findFirst();
        if (thisWeek.isEmpty()) {
            WeeklyPosition weeklyPosition = logWeekly(startOfWeek(), false);
            all.add(weeklyPosition);
        }
        return all;
    }

    private LocalDate startOfWeek() {
        LocalDate now = LocalDate.now();
        switch (DayOfWeek.of(now.get(DAY_OF_WEEK))) {
            case SUNDAY:
                return now.plusDays(6);
            case MONDAY:
                return now.plusDays(5);
            case TUESDAY:
                return now.plusDays(4);
            case WEDNESDAY:
                return now.plusDays(3);
            case THURSDAY:
                return now.plusDays(2);
            case FRIDAY:
                return now.plusDays(1);
            default:
                return now;
        }
    }

    public List<MonthlyPosition> getMonthlyPositions() {
        return monthlyPositionsRepository.findAll();
    }

    public void save(DailyPosition position) {
        dailyRepository.save(position);
    }

    public void save(WeeklyPosition position) {
        position.calculate();
        weeklyRepository.save(position);
    }

    public void save(MonthlyPosition position) {
        monthlyPositionsRepository.save(position);
    }

    public DailyPosition logDaily(LocalDate date) {
        DailyPosition position = new DailyPosition();
        position.setDate(date);

        Optional<DailyPosition> current = getDay();
        if (current.isPresent() && current.get().getDate().equals(date)) {
            dailyRepository.deleteById(current.get().getPositionId());
            current = getDay();
        }

        Account account = accountService.getAccountInfo();
        DecayOptions options = DecayOptions.builder()
                .account(account)
                .trades(tradeService.findAll())
                .build();
        Decay decay = decayService.calculateDecay(options);

        List<Trade> trades = tradeService.findAll(position.getDate());

        double fees = trades.stream().mapToDouble(Trade::getFees).sum();
        double changed;

        if (current.isPresent()) {
            position.setOpening(current.get().getOpening() + current.get().getDifference() + current.get().getFees());
            changed = account.getCurrent() - position.getOpening() - fees;
        } else {
            position.setOpening(account.getOpening());
            changed = trades.stream().mapToDouble(Trade::getProfit).sum();
        }

        position.setDifference(changed);
        position.setTotalDifference(changed + fees);
        position.setProfit(account.getProfit().getValue());
        position.setTotalProfit(account.getProfit().getValue() + fees);
        position.setFees(fees);
        position.setPerTrade(position.getProfit() / account.getTotalTrades().getTrades());
        position.setPosition(new Figure(account.getCurrentPosition().getValue(), account.getCurrentPosition().getPercentage()));
        position.setTrade(decay.getTradesAvailable());
        position.setAccount(account.getProfitThisWeek() / position.getOpening());

        save(position);
        return position;
    }

    public MonthlyPosition logMonthly(LocalDate date) {
        MonthlyPosition position = new MonthlyPosition();
        position.setDate(date);

        LocalDate start = position.getDate();
        LocalDate endDate = start.plusMonths(1).minusDays(1);
        Account account = accountService.getAccountInfo(date);

        List<Trade> trades = tradeService.findAll(start, endDate);
        Optional<MonthlyPosition> current = getMonth();
        if (current.isPresent() && current.get().getDate().equals(date)) {
            monthlyPositionsRepository.deleteById(current.get().getPositionId());
            current = getMonth();
        }

        long won = trades.stream().filter(e -> e.getProfit() > 0).count();
        double returnForWins = trades.stream().filter(e -> e.getProfit() > 0).mapToDouble(Trade::getProfit).sum();
        double returnForLoss = trades.stream().filter(e -> e.getProfit() < 0).mapToDouble(Trade::getProfit).sum();

        double fees = trades.stream().mapToDouble(Trade::getFees).sum();
        double profit = trades.stream().mapToDouble(Trade::getProfit).sum();
        double pips = trades.stream().mapToDouble(Trade::getPips).sum();
        double rr = trades.stream().mapToDouble(Trade::getRr).sum();
        double totalProfit = profit + fees;

        if (current.isPresent()) {
            position.setStart(current.get().getEnd());
        } else {
            position.setStart(account.getOpening());
        }

        position.setEnd(position.getStart() + totalProfit);
        position.setProfit(new Figure(totalProfit, (totalProfit / position.getStart())));
        position.setTotal(profit);


        double invested = trades.stream().mapToDouble(Trade::getOpen).sum();
        double roi = totalProfit / invested;
        double ratio = (double) won / trades.size();

        TradeStats stats = new TradeStats();
        stats.setTrades(trades.size());
        stats.setWon(won);
        stats.setLost(trades.size() - won);
        stats.setPips(pips);
        stats.setRr(rr);
        if (won > 0) {
            stats.setWinRatio((double) won / trades.size());
        }
        position.setStats(stats);
        position.setRoi(roi);
        position.setFees(fees);
        position.setInvested(invested);
        calculateWinSplit(position, won, returnForWins);
        calculateLossSplit(position, (trades.size() - won), returnForLoss);
        position.setTradesPerWeek(calculateTradesPerWeek(start, endDate, trades.size()));
        position.setTradesPerDay(calculateTradesPerDay(start, endDate, trades.size()));
        save(position);

        return position;
    }

    private double calculateTradesPerDay(LocalDate start, LocalDate end, int trades) {
        long days = calculateBusinessDays(start, end);
        return (double) trades / days;
    }

    private double calculateTradesPerWeek(LocalDate start, LocalDate end, int trades) {
        long days = calculateBusinessDays(start, end);
        return (double) trades / (double) (days / TRADING_DAYS);
    }

    private void calculateWinSplit(MonthlyPosition position, double wins, double profit) {
        PLSplit split = new PLSplit();
        split.setReturnPerTrade(profit / wins);
        split.setTotalReturn(profit);
        split.setPercentage(split.getReturnPerTrade() / TRADE_AMOUNT);
        position.setWinsSplit(split);
    }

    private void calculateLossSplit(MonthlyPosition position, double wins, double profit) {
        PLSplit split = new PLSplit();
        split.setReturnPerTrade(profit / wins);
        split.setTotalReturn(profit);
        split.setPercentage(split.getReturnPerTrade() / TRADE_AMOUNT);
        position.setLossesSplit(split);
    }

    public WeeklyPosition logWeekly(LocalDate date, boolean save) {
        WeeklyPosition position = new WeeklyPosition();
        position.setDate(date);

        Optional<WeeklyPosition> current = getWeek();
        if (current.isPresent() && current.get().getDate().equals(date)) {
            weeklyRepository.deleteById(current.get().getPositionId());
            current = getWeek();
        }

        Account account = accountService.getAccountInfo();
        LocalDate start = position.getDate();
        List<Trade> trades = tradeService.findAll(start.minusDays(5), start);
        long won = trades.stream().filter(e -> e.getProfit() > 0).count();

        double fees = trades.stream().mapToDouble(Trade::getFees).sum();
        double profit = trades.stream().mapToDouble(Trade::getProfit).sum();
        double rr = trades.stream().mapToDouble(Trade::getRr).sum();
        double invested = trades.stream().mapToDouble(Trade::getOpen).sum();
        double roi = profit / invested;
        double ratio = (double) won / trades.size();
        if (current.isPresent()) {
            position.setStart(current.get().getEnd());
        } else {
            position.setStart(account.getOpening());
        }

        position.setEnd(position.getStart() + profit + fees);
        position.setProfit(new Figure(profit, (profit / position.getStart())));
        position.setTotal(position.getProfit().getValue() + fees);
        position.setTrades(trades.size());
        position.setWon(won);
        position.setLost(position.getTrades() - won);
        position.setRr(rr);
        position.setRatio(ratio);
        position.setRoi(roi);
        position.setFees(fees);
        calculateTradesAvailable(position);
        position.setInvested(invested);
        position.setTotalPosition(account.getCurrentPosition().getValue());
        position.setCurrentProfit(account.getProfit().getValue());
        calculateWeeklyTradesAvailable(account, position);
        if (save) {
            save(position);
        }
        return position;
    }

    private void calculateWeeklyTradesAvailable(Account account, WeeklyPosition position) {
        double currentPosition = account.getCurrent();
        List<WeeklyPosition> weeklyPositions = getWeeklyPositions();
        double returns = weeklyPositions.stream().mapToDouble(e -> e.getProfit().getValue()).sum() + position.getTotal();
        double returnsPerWeek = returns / (weeklyPositions.size() + 1);
        position.setTradesAvailablePerWeek(Math.abs(currentPosition / returnsPerWeek));
    }

    private void calculateTradesAvailable(WeeklyPosition position) {
        List<Trade> trades = tradeService.findAll();
        Optional<WeeklyPosition> firstWeek = getFirstWeek();
        double startOfWeek = position.getStart();
        if (firstWeek.isPresent()) {
            startOfWeek = firstWeek.get().getStart();
        }
        double returnPerTrade = (position.getEnd() - startOfWeek) / trades.size();
        position.setTradesAvailable(Math.abs(position.getEnd() / returnPerTrade));
        position.setReturnPerTrade(returnPerTrade);
    }

    private Optional<WeeklyPosition> getFirstWeek() {
        List<WeeklyPosition> weeklyPositions = getWeeklyPositions();
        if (!weeklyPositions.isEmpty()) {
            return Optional.of(weeklyPositions.get(0));
        }
        return Optional.empty();
    }

    public void deleteAllDaily() {
        dailyRepository.deleteAll();
    }

    public void deleteAllWeekly() {
        weeklyRepository.deleteAll();
    }

    public Optional<DailyPosition> getDay() {
        List<DailyPosition> all = getDailyPositions();
        if (all.size() > 0) {
            return all.stream().sorted((e, f) -> f.getDate().compareTo(e.getDate())).findAny();
        } else {
            return Optional.empty();
        }
    }

    public Optional<WeeklyPosition> getWeek() {
        List<WeeklyPosition> all = getWeeklyPositions();
        if (all.size() > 0) {
            return all.stream().sorted((e, f) -> f.getDate().compareTo(e.getDate())).findAny();
        } else {
            return Optional.empty();
        }
    }

    public Optional<MonthlyPosition> getMonth() {
        List<MonthlyPosition> all = getMonthlyPositions();
        if (all.size() > 0) {
            return all.stream().sorted((e, f) -> f.getDate().compareTo(e.getDate())).findAny();
        } else {
            return Optional.empty();
        }
    }

    public Optional<MonthlyPosition> getMonth(LocalDate date) {
        List<MonthlyPosition> all = getMonthlyPositions();
        return all.stream().filter(e -> isMatch(e.getDate(), date)).collect(Collectors.toList()).stream().findFirst();
    }

    private boolean isMatch(LocalDate date1, LocalDate date2) {
        return date1.format(DateTimeFormatter.ofPattern("ddMMyyyy")).equals(date2.format(DateTimeFormatter.ofPattern("ddMMyyyy")));
    }

    public void deleteWeek(LocalDate week) {
        List<WeeklyPosition> all = getWeeklyPositions();
        WeeklyPosition matched = all.stream().filter(e -> e.getDate().equals(week)).findFirst().orElseThrow();
        weeklyRepository.delete(matched);
    }
}
