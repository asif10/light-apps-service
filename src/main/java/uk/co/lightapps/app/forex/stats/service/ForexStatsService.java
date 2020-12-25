package uk.co.lightapps.app.forex.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.lightapps.app.forex.stats.domain.ForexStats;
import uk.co.lightapps.app.forex.trades.domain.Trade;
import uk.co.lightapps.app.forex.trades.services.TradeService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import static java.time.format.DateTimeFormatter.*;

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
        calculateDaysTraded(stats);
        calculateTrades(stats);
        calculateTotalPips(stats);
        calculateTimesPerDay(stats);
        calculateTradingDays(stats);
        calculateDaysPassed(stats);
        return stats;
    }

    long bankHolidays = 10;

    private void calculateTradingDays(ForexStats stats) {
        LocalDate now = LocalDate.now();
        LocalDate start = LocalDate.of(now.getYear(), 1, 1);
        LocalDate end = start.plusYears(1).minusDays(1);
        long weeks = ChronoUnit.WEEKS.between(start, now);
        stats.setTradingDays(ChronoUnit.DAYS.between(start, end) - weeks - bankHolidays);
    }

    private void calculateTimesPerDay(ForexStats stats) {
        stats.setTimesPerDay((double) stats.getTrades() / stats.getDaysPassed());
    }

    private void calculateTotalPips(ForexStats stats) {
        stats.setPips((long) tradeService.getAll().stream().mapToDouble(Trade::getPips).sum());
    }

    private void calculateTrades(ForexStats stats) {
        stats.setTrades(tradeService.getAll().size());
    }

    private void calculateDaysTraded(ForexStats stats) {
        Set<String> days = new HashSet<>();
        tradeService.getAll().forEach(trade -> days.add(trade.getDate().format(ofPattern("ddMMyyyy"))));
        stats.setDaysTraded(days.size());
        stats.setDaysNotTraded(stats.getDaysPassed() - days.size());
    }

    private void calculateDaysPassed(ForexStats stats) {
        LocalDate now = LocalDate.now();

        long tradingDays = stats.getTradingDays();
        double done = (double) now.getDayOfYear() / 365;

        stats.setDaysPassed((long) (tradingDays * done));
    }
}
