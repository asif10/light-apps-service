package uk.co.lightapps.app.forex.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.lightapps.app.forex.account.domain.Figure;
import uk.co.lightapps.app.forex.stats.domain.ForexStats;
import uk.co.lightapps.app.forex.trades.domain.Trade;
import uk.co.lightapps.app.forex.trades.services.TradeService;

import java.util.List;

/**
 * @author Asif Akhtar
 * 22/12/2020 17:56
 */
@Service
@RequiredArgsConstructor
public class ForexStatsService {
    private final TradeService tradeService;

    public ForexStats calculateStats() {
        ForexStats stats = new ForexStats();

        calculateWinTrades(stats, tradeService.findAll());
        calculateLossTrades(stats, tradeService.findAll());
        return stats;
    }

    private void calculateLossTrades(ForexStats stats, List<Trade> trades) {
        long manualTrades = trades.stream().filter(e -> e.getStatus().equalsIgnoreCase("SL")).mapToDouble(Trade::getProfit).count();
        stats.setStopLosses(new Figure(manualTrades, (double) manualTrades / trades.size()));
    }

    private void calculateWinTrades(ForexStats stats, List<Trade> trades) {
        long manualTrades = trades.stream().filter(e -> e.getStatus().equalsIgnoreCase("MANUAL")).mapToDouble(Trade::getProfit).count();
        long tpTrades = trades.stream().filter(e -> e.getStatus().equalsIgnoreCase("TP")).mapToDouble(Trade::getProfit).count();
        double total = manualTrades + tpTrades;
        stats.setManual(new Figure(manualTrades, (manualTrades / total)));
        stats.setTakeProfit(new Figure(tpTrades, (tpTrades / total)));
    }
}
