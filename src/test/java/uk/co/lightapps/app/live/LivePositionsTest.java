package uk.co.lightapps.app.live;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.lightapps.app.forex.positions.domain.WeeklyPosition;
import uk.co.lightapps.app.forex.positions.service.PositionsService;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.lightapps.app.trades.TradesTest.rounded;
import static uk.co.lightapps.app.trades.TradesTest.rounded2dp;

/**
 * @author Asif Akhtar
 * 08/01/2021 22:12
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("live")
public class LivePositionsTest {
    @Autowired
    private PositionsService positionsService;

    @Test
    public void check_weekly_position() throws Exception {
//        positionsService.deleteAllWeekly();
        WeeklyPosition saved = positionsService.logWeekly(LocalDate.of(2021, 1, 8));

        assertThat(rounded(saved.getStart()), is(600.70));
        assertThat(rounded(saved.getEnd()), is(598.83));
        assertThat(rounded(saved.getProfit().getValue()), is(-1.87));
        assertThat(rounded(saved.getProfit().getPercentage()) * 100, is(-0.31));
        assertThat(rounded(saved.getFees()), is(-0.01));
        assertThat(rounded(saved.getTotal()), is(-1.87));
        assertThat(saved.getTrades(), is(13L));
        assertThat(saved.getWon(), is(4L));
        assertThat(saved.getLost(), is(9L));
        assertThat(rounded(saved.getRatio()), is(0.3077));
        assertThat(rounded(saved.getRr()), is(-3.15));
        assertThat(rounded(saved.getInvested()), is(78.00));
        assertThat(rounded(saved.getRoi()), is(-0.0238));
        assertThat(rounded(saved.getReturnPerTrade()), is(-0.1438));
        assertThat(rounded(saved.getTotalPosition()), is(-306.98));
        assertThat(rounded2dp(saved.getCurrentProfit()), is(-1.87));
        assertThat(rounded2dp(saved.getTradesAvailable()), is(4162.99));
        assertThat(rounded2dp(saved.getTradesAvailablePerWeek()), is(320.23));

        assertThat(saved.getPositionId(), notNullValue());

        assertThat(positionsService.getWeeklyPositions().size(), is(1));
    }

    @Test
    public void test_current_week() throws Exception {
        LocalDate week = LocalDate.of(2021, 1, 15);
        positionsService.deleteWeek(week);
        WeeklyPosition saved = positionsService.logWeekly(week);
//
//        assertThat(rounded(saved.getStart()), is(600.70));
//        assertThat(rounded(saved.getEnd()), is(598.83));
//        assertThat(rounded(saved.getProfit().getValue()), is(-1.87));
//        assertThat(rounded(saved.getProfit().getPercentage()) * 100, is(-0.31));
//        assertThat(rounded(saved.getFees()), is(-0.01));
//        assertThat(rounded(saved.getTotal()), is(-1.87));
//        assertThat(saved.getTrades(), is(13L));
//        assertThat(saved.getWon(), is(4L));
//        assertThat(saved.getLost(), is(9L));
//        assertThat(rounded(saved.getRatio()), is(0.3077));
//        assertThat(rounded(saved.getRr()), is(-3.15));
//        assertThat(rounded(saved.getInvested()), is(78.00));
//        assertThat(rounded(saved.getRoi()), is(-0.0238));
//        assertThat(rounded(saved.getReturnPerTrade()), is(-0.1438));
//        assertThat(rounded(saved.getTotalPosition()), is(-306.98));
//        assertThat(rounded2dp(saved.getCurrentProfit()), is(-1.87));
//        assertThat(rounded2dp(saved.getTradesAvailable()), is(4162.99));
        assertThat(rounded2dp(saved.getTradesAvailablePerWeek()), is(378.19));
//
//        assertThat(saved.getPositionId(), notNullValue());
//
//        assertThat(positionsService.getWeeklyPositions().size(), is(1));
    }
}
