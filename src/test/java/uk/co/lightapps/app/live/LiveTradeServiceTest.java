package uk.co.lightapps.app.live;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.lightapps.app.forex.positions.service.PositionsService;
import uk.co.lightapps.app.forex.trades.domain.TradesGroup;
import uk.co.lightapps.app.forex.trades.services.TradeService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Asif Akhtar
 * 25/01/2021 00:17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("live")
public class LiveTradeServiceTest {
    @Autowired
    private TradeService service;

    @Test
    public void get_all_trades_grouped_weekly() throws Exception {
        List<TradesGroup> grouped = service.getTradesGrouped();
        assertThat(grouped.size(), is(3));

        Optional<TradesGroup> matched = grouped.stream().filter(e -> e.getWeek().equalsIgnoreCase("080121")).findFirst();
        assertThat(matched.isPresent(), is(true));
        matched.ifPresent(e-> assertThat(e.getTrades().size(), is(13)));

        matched = grouped.stream().filter(e -> e.getWeek().equalsIgnoreCase("150121")).findFirst();
        assertThat(matched.isPresent(), is(true));
        matched.ifPresent(e-> assertThat(e.getTrades().size(), is(13)));

        matched = grouped.stream().filter(e -> e.getWeek().equalsIgnoreCase("220121")).findFirst();
        assertThat(matched.isPresent(), is(true));
        matched.ifPresent(e-> assertThat(e.getTrades().size(), is(15)));

        matched = grouped.stream().filter(e -> e.getWeek().equalsIgnoreCase("290121")).findFirst();
        assertThat(matched.isPresent(), is(false));
//        matched.ifPresent(e-> assertThat(e.getTrades().size(), is(0)));
    }
}
