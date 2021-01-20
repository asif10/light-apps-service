package uk.co.lightapps.app.forex.history.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.lightapps.app.forex.history.domain.Snapshot;
import uk.co.lightapps.app.forex.positions.domain.WeeklyPosition;
import uk.co.lightapps.app.forex.positions.service.PositionsService;
import uk.co.lightapps.app.forex.trades.domain.Trade;
import uk.co.lightapps.app.forex.trades.services.TradeService;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Asif Akhtar
 * 18/01/2021 18:54
 */
@Service
@RequiredArgsConstructor
public class SnapshotService {
    private final PositionsService positionsService;
    private final TradeService tradeService;

    public Snapshot calculateLastWeek() {
        WeeklyPosition weeklyPosition = positionsService.getWeek().orElseThrow();
        Snapshot snapshot = new Snapshot();
        List<Trade> trades = tradeService.getAll(LocalDate.of(2021, 1, 1), weeklyPosition.getDate());
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
}
