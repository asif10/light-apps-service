package uk.co.lightapps.app.forex.positions.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.lightapps.app.forex.account.domain.Account;
import uk.co.lightapps.app.forex.account.domain.Figure;
import uk.co.lightapps.app.forex.account.service.AccountService;
import uk.co.lightapps.app.forex.positions.domain.DailyPosition;
import uk.co.lightapps.app.forex.positions.domain.WeeklyPosition;
import uk.co.lightapps.app.forex.positions.repository.DailyPositionsRepository;
import uk.co.lightapps.app.forex.positions.repository.WeeklyPositionsRepository;
import uk.co.lightapps.app.forex.trades.domain.Trade;
import uk.co.lightapps.app.forex.trades.services.TradeService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
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
        List<Trade> trades = tradeService.getAll(position.getDate());

        double fees = trades.stream().mapToDouble(Trade::getFees).sum();
        double changed;

        if (current.isPresent()) {
            position.setOpening(current.get().getOpening() + current.get().getChange() + current.get().getFees());
            changed = account.getCurrent() - position.getOpening();
        } else {
            position.setOpening(account.getOpening());
            changed = trades.stream().mapToDouble(Trade::getProfit).sum();
        }

        position.setChange(changed);
        position.setProfit(account.getProfit().getValue());
        position.setTotalProfit(changed - fees);
        position.setFees(fees);
        position.setPerTrade(position.getProfit() / account.getTotalTrades().getTrades());
        position.setPosition(new Figure(account.getCurrentPosition().getValue(), account.getCurrentPosition().getPercentage()));
        position.setTrade(account.getTradesAvailableCurrent());
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
        List<Trade> trades = tradeService.getAll(start, position.getDate().plusDays(6));
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
        calculateTradesAvailable(position);
        position.setInvested(invested);

        save(position);
        return position;
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
}
