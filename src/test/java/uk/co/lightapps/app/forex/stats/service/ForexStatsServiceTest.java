package uk.co.lightapps.app.forex.stats.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.lightapps.app.forex.stats.domain.ForexStats;

import java.time.LocalDate;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.lightapps.app.forex.account.service.AccountServiceTest.round;

/**
 * @author Asif Akhtar
 * 22/12/2020 17:56
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ForexStatsServiceTest {
    @Autowired
    private ForexStatsService service;

    @Test
    public void calculateDaysPassed() {
        long days = LocalDate.now().getDayOfYear();
        int weekends = (int) days / 7;
        ForexStats stats = service.calculateStats();
//        assertThat(stats.getDaysPassed(), is(days - weekends));
    }

    @Test
    public void calculateDaysTraded() {
        ForexStats stats = service.calculateStats();
//        assertThat(stats.getDaysTraded(), is(34L));
    }

    @Test
    public void calculateDaysNotTraded() {
        ForexStats stats = service.calculateStats();
//        assertThat(stats.getDaysNotTraded(), is(272L));
    }

    @Test
    public void calculateTrades() {
        ForexStats stats = service.calculateStats();
//        assertThat(stats.getTrades(), is(108L));
    }

    @Test
    public void calculateTotalPips() {
        ForexStats stats = service.calculateStats();
//        assertThat(stats.getPips(), is(202L));
    }

    @Test
    public void calculateTimesPerDay() {
        ForexStats stats = service.calculateStats();
//        assertThat(round(stats.getTimesPerDay()), is(0.35));
    }

    @Test
    public void calculateTradingDays() {
        ForexStats stats = service.calculateStats();
//        assertThat(stats.getTradingDays(), is(304L));
    }
}