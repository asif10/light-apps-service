package uk.co.lightapps.app.live;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.lightapps.app.forex.history.domain.MonthlySnapshot;
import uk.co.lightapps.app.forex.history.domain.Snapshot;
import uk.co.lightapps.app.forex.history.service.SnapshotService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.lightapps.app.trades.TradesTest.rounded;
import static uk.co.lightapps.app.trades.TradesTest.rounded2dp;

/**
 * @author Asif Akhtar
 * 18/01/2021 19:26
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("live")
public class LiveSnapshotServiceTest {
    @Autowired
    private SnapshotService service;

    @Test
    public void check_weekly_position() throws Exception {
        Snapshot snapshot = service.calculateLastWeek();
        assertThat(rounded(snapshot.getWinRatio()), is(0.4231));
        assertThat(rounded(snapshot.getPips()), is(-259.0));
        assertThat(rounded(snapshot.getRr()), is(-3.25));
    }

    @Test
    public void check_current_month() throws Exception {
        MonthlySnapshot month = service.currentMonth();
        assertThat(month.getMonth().format(ofPattern("ddMMyyyy")), is(LocalDate.now().format(ofPattern("01022021"))));

        assertThat(month.getStats().getTrades(), is(13L));
        assertThat(month.getStats().getWon(), is(8L));
        assertThat(month.getStats().getLost(), is(5L));
        assertThat(rounded2dp(month.getStats().getWinRatio()), is(0.62));
        assertThat(rounded2dp(month.getStats().getRr()), is(4.58));
        assertThat(rounded2dp(month.getStats().getPips()), is(-4.00));
        assertThat(rounded2dp(month.getFees()), is(-0.10));

        assertThat(rounded2dp(month.getInvested()), is(78.00));
        assertThat(rounded2dp(month.getProfit().getValue()), is(-0.70));
        assertThat(rounded(month.getProfit().getPercentage()), is(-0.0090));
        assertThat(rounded(month.getOpen()), is(594.14));
        assertThat(rounded(month.getClosed()), is(593.44));

        assertThat(rounded2dp(month.getManualWins().getValue()), is(6.0));
        assertThat(rounded2dp(month.getManualWins().getPercentage()), is(1.00));

        assertThat(rounded2dp(month.getTpWins().getValue()), is(0.0));
        assertThat(rounded2dp(month.getTpWins().getPercentage()), is(0.0));

        assertThat(month.getPrevious().getTrades(), is(63L));
        assertThat(month.getPrevious().getWon(), is(30L));
        assertThat(month.getPrevious().getLost(), is(33L));
        assertThat(rounded(month.getPrevious().getWinRatio()), is(0.4762));

        assertThat(month.getMaxTrades(), is(60L));

        assertThat(rounded2dp(month.getTradesPerDay()), is(2.17));
        assertThat(rounded2dp(month.getTradesPerWeek()), is(9.00));


    }
}
