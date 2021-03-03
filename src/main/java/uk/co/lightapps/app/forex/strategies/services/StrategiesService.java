package uk.co.lightapps.app.forex.strategies.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.lightapps.app.forex.strategies.domain.Strategies;
import uk.co.lightapps.app.forex.trades.domain.Trade;
import uk.co.lightapps.app.forex.trades.services.TradeService;

import java.util.Collections;
import java.util.List;

/**
 * @author Asif Akhtar
 * 25/12/2020 21:11
 */
@Service
@RequiredArgsConstructor
public class StrategiesService {
    private final TradeService service;

    public List<Strategies> getAllStrategies() {
        return List.of(
                createStrategy(501),
                createStrategy(502),
                createStrategy(601),
                createStrategy(602),
                createStrategy(700)
        );
    }

    private Strategies createStrategy(int strategy) {
        List<Trade> trades = service.getByStrategy(strategy);
        long won = trades.stream().filter(e -> e.getProfit() > 0).count();
        long loss = trades.size() - won;
        double totalProfit = trades.stream().mapToDouble(Trade::getProfit).sum();
        double profitWins = trades.stream().filter(e -> e.getProfit() > 0).mapToDouble(Trade::getProfit).sum();
        double pipsWins = trades.stream().filter(e -> e.getPips() > 0).mapToDouble(Trade::getPips).sum();
        double pips = trades.stream().mapToDouble(Trade::getPips).sum();
        double pipsLost = pips - pipsWins;
        double profitWin = 0;
        double profitLoss = 0;
        double pipsWonAverage = 0;
        double pipsLostAverage = 0;
        double average = totalProfit / trades.size();

        if (won > 0) {
            profitWin = profitWins / won;
            pipsWonAverage = pipsWins / won;
        }

        if (loss > 0) {
            profitLoss = (totalProfit - profitWins) / loss;
            pipsLostAverage = pipsLost / loss;
        }

        return Strategies.builder()
                .id(strategy)
                .label(label(strategy))
                .description(info(strategy))
                .trades(trades.size())
                .won(won)
                .ratio((double) won / trades.size())
                .lost(loss)
                .profit(totalProfit)
                .profitPerWin(profitWin)
                .profitPerLoss(profitLoss)
                .average(average)
                .pips(pips)
                .pipsPerWin(pipsWonAverage)
                .pipsPerLoss(pipsLostAverage)
                .build();
    }

    private String label(int label) {
        switch (label) {
            case 301:
                return "PRICE REVERSALS";
            case 302:
                return "PRICE CONTINUATION";
            case 303:
                return "TREND BREAKS";
            case 501:
            case 601:
                return "PRICE REVERSAL";
            case 502:
            case 700:
            case 602:
                return "TREND CONTINUATION";
            default:
                return "CHANNEL BREAKS";
        }
    }

    private String info(int label) {
        switch (label) {
            case 301:
                return "NO MATTER THE TREND LOOK FOR BUYS AFTER A MAJOR PRICE REVERSAL INTO LEVEL";
            case 302:
                return "COULD BE A REVERSAL BUT IS PART OF AN OVERALL UPTREND, WHERE PRICE HAS COME DOWN SO I DO A BUY ";
            case 303:
                return "THIS IS WHERE THE PRICE HAS FINISHED ABOVE THE TREND LINE. SHOULD BE A STRONG CS, WITH MAYBE A RE-TEST";
            default:
                return "THIS IS WHERE THE PRICE HAS BROKEN THE CHANNEL WITH A CLEAR CS AND MAYBE WITH A RE-TEST";
        }
    }
}
