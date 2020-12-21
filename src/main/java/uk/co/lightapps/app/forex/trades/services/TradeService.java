package uk.co.lightapps.app.forex.trades.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.lightapps.app.forex.trades.domain.Trade;
import uk.co.lightapps.app.forex.trades.repository.TradeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Asif Akhtar
 * 26/10/2020 21:30
 */
@Service
@RequiredArgsConstructor
public class TradeService {
    private final TradeRepository repository;

    public Trade save(Trade trade) {
        trade.setClose(trade.getOpen() + trade.getProfit());
        trade.setProfitP(trade.getProfit() / trade.getOpen());
        trade.setAccountP(trade.getOpen() / trade.getAccount());
        if (Objects.nonNull(trade.getStatus()) && trade.getStatus().equalsIgnoreCase("SL")) {
            trade.setRr(-1);
        }
        return repository.save(trade);
    }

    private static BigDecimal bd(double value) {
        return new BigDecimal("" + value);
    }

    public Optional<Trade> findById(String id) {
        return repository.findById(id);
    }

    public List<Trade> getAll() {
        return repository.findAll();
    }

    public List<Trade> getOpenTrades() {
        return getAll().stream().filter(this::isOpen).collect(Collectors.toList());
    }

    private boolean isOpen(Trade trade) {
        return Objects.isNull(trade.getStatus()) || trade.getStatus().equals("-");
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void closeTrade(Trade trade, double close, int pips, double rr) {
        closeTrade("MANUAL", trade, close, pips, rr);
    }

    public void lostTrade(Trade trade, double close, int pips, double rr) {
        closeTrade("SL", trade, close, pips, rr);
    }

    public void closeTrade(String status, Trade trade, double close, int pips, double rr) {
        trade.closeTrade(status, close, pips, rr);
        save(trade);
    }

    public List<Trade> getAll(LocalDate date) {
        LocalDateTime start = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);
        return getAll().stream().filter(e -> e.getDate().compareTo(start) >= 0 && e.getDate().compareTo(end) <= 1).collect(Collectors.toList());
    }
}
