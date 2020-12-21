package uk.co.lightapps.app.forex.trades.web;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.co.lightapps.app.forex.account.domain.Account;
import uk.co.lightapps.app.forex.positions.domain.DailyPosition;
import uk.co.lightapps.app.forex.positions.service.PositionsService;
import uk.co.lightapps.app.forex.trades.services.TradeService;
import uk.co.lightapps.app.forex.trades.domain.Trade;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * @author Asif Akhtar
 * 28/05/2020 01:16
 */
@RestController
@RequestMapping("/forex")
@RequiredArgsConstructor
@Slf4j
public class ForexController {
    private final TradeService service;
    private final PositionsService positionsService;

    @GetMapping(value = "/trades")
    public List<Trade> trades() {
        List<Trade> trades = service.getAll();
        Collections.reverse(trades);
        return trades;
    }

    @GetMapping(value = "/trades/open")
    public List<Trade> getOpenTrades() {
        List<Trade> trades = service.getOpenTrades();
        Collections.reverse(trades);
        return trades;
    }

    @RequestMapping("/info")
    public String info() {
        return new Gson().toJson("Hello World - " + System.currentTimeMillis());
    }

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Trade save(@RequestBody Trade trade) {
        log.info("Trade to be saved - " + trade.getTradeId());
        log.info("Request - " + new Gson().toJson(trade));
        return service.save(trade);
    }

    @PostMapping(value = "/positions/daily/save")
    public DailyPosition saveDailyPosition() {
        return positionsService.logDaily(LocalDate.now());
    }

    @GetMapping(value = "/positions/daily")
    public List<DailyPosition> getDailyPositions() {
        List<DailyPosition> positions = positionsService.getAll();
        Collections.reverse(positions);
        return positions;
    }
}
