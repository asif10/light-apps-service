package uk.co.lightapps.app.forex.strategies.services;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.lightapps.app.forex.strategies.domain.Strategies;
import uk.co.lightapps.app.forex.trades.services.TradeService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static uk.co.lightapps.app.trades.TradesTest.rounded;

/**
 * @author Asif Akhtar
 * 25/12/2020 21:12
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategiesServiceTest {
    @Autowired
    private StrategiesService service;

    @Before
    public void setup() {

    }

    @Test
    public void shouldFetchAllStrategies() {
        List<Strategies> strategies = service.getAllStrategies();
        assertThat(strategies.size(), is(4));

        Optional<Strategies> matched = strategies.stream().filter(e -> e.getId() == 301).findFirst();
        assertThat(matched.isPresent(), is(true));
        matched.ifPresent(s -> {
            assertThat(s.getTrades(), is(1L));
            assertThat(s.getWon(), is(0L));
            assertThat(s.getLost(), is(1L));
            assertThat(s.getRatio(), is(0.00));
            assertThat(rounded(s.getProfit()), is(-0.33));
            assertThat(rounded(s.getProfitPerWin()), is(0.00));
            assertThat(rounded(s.getProfitPerLoss()), is(-0.33));
            assertThat(rounded(s.getAverage()), is(-0.33));
        });

        matched = strategies.stream().filter(e -> e.getId() == 302).findFirst();
        assertThat(matched.isPresent(), is(true));
        matched.ifPresent(s -> {
            assertThat(s.getTrades(), is(1L));
            assertThat(s.getWon(), is(1L));
            assertThat(s.getLost(), is(0L));
            assertThat(s.getRatio(), is(1.00));
            assertThat(rounded(s.getAverage()), is(0.56));
            assertThat(rounded(s.getProfit()), is(0.56));
            assertThat(rounded(s.getProfitPerWin()), is(0.56));
            assertThat(rounded(s.getProfitPerLoss()), is(0.00));
        });

        matched = strategies.stream().filter(e -> e.getId() == 303).findFirst();
        assertThat(matched.isPresent(), is(true));
        matched.ifPresent(s -> {
            assertThat(s.getTrades(), is(1L));
            assertThat(s.getWon(), is(1L));
            assertThat(s.getLost(), is(0L));
        });

        matched = strategies.stream().filter(e -> e.getId() == 304).findFirst();
        assertThat(matched.isPresent(), is(true));
        matched.ifPresent(s -> {
            assertThat(s.getTrades(), is(1L));
            assertThat(s.getWon(), is(0L));
            assertThat(s.getLost(), is(1L));
        });
    }

    @Test
    public void shouldFetchAllStrategiesWithMixWins() {
        List<Strategies> strategies = service.getAllStrategies();
        assertThat(strategies.size(), is(4));

        Optional<Strategies> matched = strategies.stream().filter(e -> e.getId() == 301).findFirst();
        assertThat(matched.isPresent(), is(true));
        matched.ifPresent(s -> {
            assertThat(s.getTrades(), is(2L));
            assertThat(s.getWon(), is(1L));
            assertThat(s.getLost(), is(1L));
            assertThat(s.getRatio(), is(0.50));
            assertThat(rounded(s.getProfit()), is(0.64));
            assertThat(rounded(s.getAverage()), is(0.32));
            assertThat(rounded(s.getProfitPerWin()), is(0.97));
            assertThat(rounded(s.getProfitPerLoss()), is(-0.33));
            assertThat(rounded(s.getPipsPerWin()), is(55.0));
            assertThat(rounded(s.getPipsPerLoss()), is(-45.0));
            assertThat(rounded(s.getPips()), is(10.0));
        });

        matched = strategies.stream().filter(e -> e.getId() == 302).findFirst();
        assertThat(matched.isPresent(), is(true));
        matched.ifPresent(s -> {
            assertThat(s.getTrades(), is(1L));
            assertThat(s.getWon(), is(1L));
            assertThat(s.getLost(), is(0L));
            assertThat(s.getRatio(), is(1.00));
            assertThat(rounded(s.getAverage()), is(0.56));
            assertThat(rounded(s.getProfit()), is(0.56));
            assertThat(rounded(s.getProfitPerWin()), is(0.56));
            assertThat(rounded(s.getProfitPerLoss()), is(0.00));
            assertThat(rounded(s.getPipsPerWin()), is(39.0));
            assertThat(rounded(s.getPipsPerLoss()), is(0.0));
            assertThat(rounded(s.getPips()), is(39.0));
        });
    }
}
