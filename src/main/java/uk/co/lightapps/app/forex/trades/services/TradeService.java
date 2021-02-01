package uk.co.lightapps.app.forex.trades.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.lightapps.app.forex.positions.domain.WeeklyPosition;
import uk.co.lightapps.app.forex.positions.repository.WeeklyPositionsRepository;
import uk.co.lightapps.app.forex.positions.service.PositionsService;
import uk.co.lightapps.app.forex.trades.domain.Trade;
import uk.co.lightapps.app.forex.trades.domain.TradesGroup;
import uk.co.lightapps.app.forex.trades.repository.TradeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Asif Akhtar
 * 26/10/2020 21:30
 */
@Service
@RequiredArgsConstructor
public class TradeService {
    private final TradeRepository repository;
    private final WeeklyPositionsRepository positionsRepository;

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

    public List<TradesGroup> getTradesGrouped() {
        List<TradesGroup> groupedTrades = new ArrayList<>();
        List<Trade> all = getAll();
        List<WeeklyPosition> weeklyPositions = positionsRepository.findAll();
        weeklyPositions.forEach(week -> groupedTrades.add(groupWeek(week.getDate(), all)));
        WeeklyPosition latest = weeklyPositions.get(weeklyPositions.size() - 1);
        groupedTrades.add(groupWeek(latest.getDate().plusDays(7), all));
        groupedTrades.sort((e1, e2) -> e2.getDate().compareTo(e1.getDate()));
        return groupedTrades;
    }

    private TradesGroup groupWeek(LocalDate date, List<Trade> trades) {
        TradesGroup group = new TradesGroup();
        group.setDate(date);
        group.setWeek(date.format(DateTimeFormatter.ofPattern("ddMMyy")));
        group.setTrades(getTrades(date, trades));
        group.getTrades().sort((e1, e2) -> e2.getDate().compareTo(e1.getDate()));
        return group;
    }

    private List<Trade> getTrades(LocalDate date, List<Trade> trades) {
        LocalDate startDate = date.minusDays(5);
        return trades.stream().filter(e -> e.getDate().toLocalDate().compareTo(startDate) >= 0 && e.getDate().toLocalDate().compareTo(date) <= 0).collect(Collectors.toList());
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

    public void closeTrade(String status, Trade trade) {
        trade.closeTrade(status, trade.getClose(), trade.getPips(), trade.getRr());
        save(trade);
    }

    public List<Trade> getAll(LocalDate date) {
        return getAll(date, date);
    }

    public List<Trade> getAll(LocalDate start, LocalDate end) {
        LocalDateTime startDate = LocalDateTime.of(start, LocalTime.MIN);
        LocalDateTime endDate = LocalDateTime.of(end, LocalTime.MAX);
        return getAll().stream().filter(e -> e.getDate().compareTo(startDate) >= 0 && e.getDate().compareTo(endDate) < 1).collect(Collectors.toList());
    }

    public void delete(String tradeId) {
        repository.deleteById(tradeId);
    }

    public List<Trade> getByStrategy(int strategy) {
        return getAll().stream().filter(e -> e.getStrategy().equals("" + strategy)).collect(Collectors.toList());
    }
}
