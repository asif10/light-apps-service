package uk.co.lightapps.app.trades;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.lightapps.app.forex.Trade;
import uk.co.lightapps.app.forex.TradeService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.lightapps.app.forex.Client.IQ;
import static uk.co.lightapps.app.forex.Client.P500;
import static uk.co.lightapps.app.forex.Pair.EURUSD;
import static uk.co.lightapps.app.forex.TradeType.LONG;
import static uk.co.lightapps.app.forex.TradeType.SHORT;

/**
 * @author Asif Akhtar
 * 25/10/2020 20:38
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TradesTest {
    @Autowired
    private TradeService service;

    @Before
    public void setup() {
        service.deleteAll();
    }

//    @Test
//    public void shouldFetchAllTrades() {
//        assertThat(service.getAll().size(), is(0));
//        Trade trade = Trade.of(P500, EURUSD, "1", SHORT, 100, 0.5, 100, 1.25);
//        service.save(trade);
//        assertThat(service.getAll().size(), is(1));
//    }
//
//    @Test
//    public void shouldReturnASingleRecord() {
//        Trade trade = Trade.of(P500, EURUSD, "1", SHORT, 100, 0.5, 100, 1.25);
//        trade.setDate(LocalDateTime.of(2020, 10, 01, 10, 11));
//        Trade saved = service.save(trade);
//        assertThat(saved.getTradeId(), notNullValue());
//        assertThat(saved.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), is("01/10/2020 10:11"));
//    }
//
//    @Test
//    public void shouldUpdateTradeWhenShort() {
//        Trade trade = Trade.of(P500, EURUSD, "1", SHORT, 100, 0.5, 100, 1.25);
//        Trade saved = service.save(trade);
//        String traderId = saved.getTradeId();
//        assertThat(saved.getClose(), is(100.0));
//        assertThat(saved.calculatePips(), is(0.0));
//        trade.setClose(90);
//        saved = service.save(trade);
//        assertThat(saved.getTradeId(), is(traderId));
//        assertThat(saved.getClose(), is(90.0));
//        assertThat(saved.calculatePips(), is(10.0));
//    }

    @Test
    public void shouldOpenATrade() {
        Trade trade = Trade.opened(IQ, EURUSD, "1", LONG, 344.37, 5.00, 0.02, 2.21);
        Trade saved = service.save(trade);
        assertThat(saved.getClose(), is(5.0));
    }

    @Test
    public void shouldCloseAnOpenTrade() {
        Trade trade = Trade.opened(IQ, EURUSD, "1", LONG, 344.37, 5.00, 0.02, 2.21);
        Trade saved = service.save(trade);
        String traderId = saved.getTradeId();
        assertThat(saved.getTradeId(), is(traderId));
        assertThat(saved.getClose(), is(5.0));
        assertThat(saved.isActive(), is(true));

        service.closeTrade(saved, 5.15, 5, 1.0);

        Optional<Trade> matchingTrade = service.findById(traderId);
        assertThat(matchingTrade.isPresent(), is(true));
        matchingTrade.ifPresent(t -> {
            assertThat(t.getTradeId(), is(traderId));
            assertThat(rounded(t.getProfit()), is(0.1500));
            assertThat(rounded(t.getProfitP()), is((0.0300)));
            assertThat(t.isActive(), is(false));
        });
    }

    public static double rounded(double amount) {
        return new BigDecimal("" + amount).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }
}
