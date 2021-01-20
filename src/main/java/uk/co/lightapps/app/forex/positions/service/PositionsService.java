package uk.co.lightapps.app.forex.positions.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.lightapps.app.forex.account.domain.Account;
import uk.co.lightapps.app.forex.account.domain.Figure;
import uk.co.lightapps.app.forex.account.service.AccountService;
import uk.co.lightapps.app.forex.decay.domain.Decay;
import uk.co.lightapps.app.forex.decay.domain.DecayOptions;
import uk.co.lightapps.app.forex.decay.services.DecayService;
import uk.co.lightapps.app.forex.positions.domain.DailyPosition;
import uk.co.lightapps.app.forex.positions.domain.WeeklyPosition;
import uk.co.lightapps.app.forex.positions.repository.DailyPositionsRepository;
import uk.co.lightapps.app.forex.positions.repository.WeeklyPositionsRepository;
import uk.co.lightapps.app.forex.trades.domain.Trade;
import uk.co.lightapps.app.forex.trades.services.TradeService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author Asif Akhtar
 * 10/12/2020 23:46
 */
@Service
@RequiredArgsConstructor
public class PositionsService {
    private final DailyPositionsRepository dailyRepository;
    private final WeeklyPositionsRepository weeklyRepository;
    private final AccountService accountService;
    private final DecayService decayService;
    private final TradeService tradeService;

    public void add(DailyPosition position) {
        dailyRepository.save(position);
    }

    public void add(WeeklyPosition position) {
        weeklyRepository.save(position);
    }

    public List<DailyPosition> getDailyPositions() {
        return dailyRepository.findAll();
    }

    public List<WeeklyPosition> getWeeklyPositions() {
        return weeklyRepository.findAll();
    }

    public void save(DailyPosition position) {
        dailyRepository.save(position);
    }

    public void save(WeeklyPosition position) {
        position.calculate();
        weeklyRepository.save(position);
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
                .trades(tradeService.getAll())
                .build();
        Decay decay = decayService.calculateDecay(options);

        List<Trade> trades = tradeService.getAll(position.getDate());

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

    public WeeklyPosition logWeekly(LocalDate date) {
        WeeklyPosition position = new WeeklyPosition();
        position.setDate(date);

        Optional<WeeklyPosition> current = getWeek();
        if (current.isPresent() && current.get().getDate().equals(date)) {
            weeklyRepository.deleteById(current.get().getPositionId());
            current = getWeek();
        }

        Account account = accountService.getAccountInfo();
        LocalDate start = position.getDate();
        List<Trade> trades = tradeService.getAll(start.minusDays(5), start);
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
        save(position);
        return position;
    }

    private void calculateWeeklyTradesAvailable(Account account, WeeklyPosition position) {
        double currentPosition = account.getCurrent();
        List<WeeklyPosition> weeklyPositions = getWeeklyPositions();
        double returns = weeklyPositions.stream().mapToDouble(e -> e.getProfit().getValue()).sum() + position.getTotal();
        double returnsPerWeek = returns / (weeklyPositions.size()+1);
        position.setTradesAvailablePerWeek(Math.abs(currentPosition / returnsPerWeek));
    }

    private void calculateTradesAvailable(WeeklyPosition position) {
        List<Trade> trades = tradeService.getAll();
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

    public void deleteWeek(LocalDate week) {
        List<WeeklyPosition> all = getWeeklyPositions();
        WeeklyPosition matched = all.stream().filter(e -> e.getDate().equals(week)).findFirst().orElseThrow();
        weeklyRepository.delete(matched);
    }
}
