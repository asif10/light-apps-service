package uk.co.lightapps.app.trades;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.lightapps.app.forex.account.domain.Account;
import uk.co.lightapps.app.forex.positions.domain.DailyPosition;
import uk.co.lightapps.app.forex.positions.service.PositionsService;
import uk.co.lightapps.app.forex.trades.services.TradeService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static uk.co.lightapps.app.trades.TradesTest.rounded;

/**
 * @author Asif Akhtar
 * 13/12/2020 02:02
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PositionsTest {
    @Autowired
    private PositionsService positionsService;

    @Test
    public void totalNumberOfDailyPositions() {
        List<DailyPosition> records = positionsService.getAll();
        assertThat(records.size(), is(28));
    }

    @Test
    public void get_current_position() {
        Optional<DailyPosition> current = positionsService.getCurrent();
        assertThat(current.isPresent(), is(true));
        current.ifPresent(pos -> {
            assertThat(formattedDate(pos.getDate()), is("14122020"));
        });
    }

    private String formattedDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
    }

    @Test
    public void logNewDailyPosition_new_day() {
        DailyPosition dailyPosition = positionsService.logDaily(LocalDate.of(2020, 12, 18));
        assertThat(rounded(dailyPosition.getOpening()), is(356.26));
        assertThat(rounded(dailyPosition.getChange()), is(-3.51));
        assertThat(rounded(dailyPosition.getProfit()), is(13.26));
        assertThat(rounded(dailyPosition.getTotalProfit()), is(14.17));

        assertThat(rounded(dailyPosition.getFees()), is(0.00));
        assertThat(rounded(dailyPosition.getPosition().getValue()), is(-242.37));
        assertThat(rounded(dailyPosition.getPosition().getPercentage()), is(-0.404));
        assertThat(rounded(dailyPosition.getTrade()), is(35.763));
        assertThat(rounded(dailyPosition.getAccount()), is(0.0372));
    }

    @Test
    public void logNewDailyPosition_existing_day() {
        Optional<DailyPosition> current = positionsService.getCurrent();
        assertThat(current.isPresent(), is(true));
        int total = positionsService.getAll().size();

        DailyPosition dailyPosition = positionsService.logDaily(LocalDate.of(2020, 12, 18));
        assertThat(rounded(dailyPosition.getOpening()), is(356.26));
        assertThat(rounded(dailyPosition.getChange()), is(-3.51));
        assertThat(rounded(dailyPosition.getProfit()), is(13.26));
        assertThat(rounded(dailyPosition.getTotalProfit()), is(14.17));

        assertThat(rounded(dailyPosition.getFees()), is(0.00));
        assertThat(rounded(dailyPosition.getPosition().getValue()), is(-242.37));
        assertThat(rounded(dailyPosition.getPosition().getPercentage()), is(-0.404));
        assertThat(rounded(dailyPosition.getTrade()), is(35.763));
        assertThat(rounded(dailyPosition.getAccount()), is(0.0372));
        assertThat(positionsService.getAll().size(), is(total));

        Optional<DailyPosition> afterUpdate = positionsService.getCurrent();
        assertThat(afterUpdate.isPresent(), is(true));
        assertThat(afterUpdate.get().getPositionId(), is(not(current.get().getPositionId())));
    }
}
