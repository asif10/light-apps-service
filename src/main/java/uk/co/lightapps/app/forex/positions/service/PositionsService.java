package uk.co.lightapps.app.forex.positions.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.lightapps.app.forex.account.domain.Account;
import uk.co.lightapps.app.forex.account.domain.Figure;
import uk.co.lightapps.app.forex.account.service.AccountService;
import uk.co.lightapps.app.forex.positions.domain.DailyPosition;
import uk.co.lightapps.app.forex.positions.repository.DailyPositionsRepository;
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
    private final AccountService accountService;
    private final TradeService tradeService;

    public void add(DailyPosition position) {
        dailyRepository.save(position);
    }

    public List<DailyPosition> getAll() {
        return dailyRepository.findAll();
    }

    public void save(DailyPosition position) {
        dailyRepository.save(position);
    }

    public DailyPosition logDaily(LocalDate date) {
        DailyPosition position = new DailyPosition();
        position.setDate(date);

        Optional<DailyPosition> current = getCurrent();
        if (current.isPresent() && current.get().getDate().equals(date)) {
            dailyRepository.deleteById(current.get().getPositionId());
            current = getCurrent();
        }

        Account account = accountService.getAccountInfo();
        List<Trade> trades = tradeService.getAll(position.getDate());

        double fees = trades.stream().mapToDouble(Trade::getFees).sum();

        current.ifPresent(e -> {
            position.setOpening(e.getOpening() + e.getChange() + e.getFees());
            position.setChange(account.getProfitThisWeek());
            position.setProfit(account.getProfit().getValue());
            position.setTotalProfit(account.getProfitExclFees());
            position.setFees(fees);
            position.setPosition(new Figure(account.getCurrentPosition().getValue(), account.getCurrentPosition().getPercentage()));
            position.setTrade(account.getTradesAvailableCurrent());
            position.setAccount(account.getProfitThisWeek() / position.getOpening());
        });

        save(position);
        return position;
    }

    public void deleteAll() {
        dailyRepository.deleteAll();
    }

    public Optional<DailyPosition> getCurrent() {
        List<DailyPosition> all = getAll();
        if (all.size() > 0) {
            return all.stream().sorted((e, f) -> f.getDate().compareTo(e.getDate())).findAny();
        } else {
            return Optional.empty();
        }
    }
}
