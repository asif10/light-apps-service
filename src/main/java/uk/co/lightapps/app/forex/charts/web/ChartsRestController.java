package uk.co.lightapps.app.forex.charts.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.lightapps.app.forex.charts.domain.ChartContent;
import uk.co.lightapps.app.forex.positions.service.PositionsService;
import uk.co.lightapps.app.forex.trades.domain.Trade;
import uk.co.lightapps.app.forex.trades.services.TradeService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Asif Akhtar
 * 05/01/2021 21:41
 */
@RestController
@RequestMapping("/open/charts")
@RequiredArgsConstructor
@Slf4j
public class ChartsRestController {
    private final PositionsService positionsService;
    private final TradeService service;

    @GetMapping(value = "/positions/daily")
    public ChartContent dailyChartContent() {
        ChartContent chartContent = new ChartContent();
        positionsService.getDailyPositions().forEach(day -> chartContent.add(day.getDate().format(DateTimeFormatter.ofPattern("ddMM")), "" + day.getProfit()));
        return chartContent;
    }
}
