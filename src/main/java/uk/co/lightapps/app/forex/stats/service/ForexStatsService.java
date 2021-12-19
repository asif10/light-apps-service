package uk.co.lightapps.app.forex.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.lightapps.app.forex.account.domain.Figure;
import uk.co.lightapps.app.forex.stats.domain.ForexStats;
import uk.co.lightapps.app.forex.stats.domain.TradesBio;
import uk.co.lightapps.app.forex.trades.domain.Trade;
import uk.co.lightapps.app.forex.trades.services.TradeService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<TradesBio> calculateTradeStats() {
        return tradeService.findAll().stream().map(this::tradeStats).collect(Collectors.toList());
    }

    private TradesBio tradeStats(Trade trade) {
        return TradesBio.builder()
                .pair(trade.getPair().name())
                .ccy(getCcy(trade.getPair().name()))
                .month(month(trade.getDate()))
                .day(day(trade.getDate()))
                .time(time(trade.getDate()))
                .won(trade.getProfit() >= 0 ? 1 : 0)
                .lost(trade.getProfit() < 0 ? 1 : 0)
                .profit(trade.getProfit() >= 0 ? trade.getProfit() : 0)
                .loss(trade.getProfit() < 0 ? trade.getProfit() : 0)
                .position(trade.getProfit())
                .pips(trade.getPips())
                .rr(trade.getRr())
                .strategy(strategyLabel(Integer.parseInt(trade.getStrategy())))
                .build();
    }

    private String month(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("MMMM"));
    }

    private String day(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("EEE"));
    }

    private String time(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("a"));
    }

    public static String strategyLabel(int label) {
        switch (label) {
            case 301:
                return "301 PRICE REVERSALS";
            case 302:
                return "302 PRICE CONTINUATION";
            case 303:
                return "303 TREND BREAKS";
            case 501:
            case 601:
                return "601 PRICE REVERSAL";
            case 502:
            case 700:
            case 602:
                return "700 TREND CONTINUATION";
            case 800:
                return "800 LT TREND CONTINUATION";
            case 900:
                return "900 ST TREND CONTINUATION";
            case 1000:
                return "1000 PRICE REVERSALS";
            default:
                return "CHANNEL BREAKS";
        }
    }

    private String getCcy(String name) {
        if (name.equalsIgnoreCase("GOLD")) {
            return name;
        } else if (name.equalsIgnoreCase("SILVER")) {
            return name;
        } else if (name.equalsIgnoreCase("OIL")) {
            return name;
        } else {
            return name.substring(3);
        }
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
