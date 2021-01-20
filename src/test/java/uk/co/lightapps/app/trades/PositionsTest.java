package uk.co.lightapps.app.trades;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.lightapps.app.forex.positions.domain.DailyPosition;
import uk.co.lightapps.app.forex.positions.domain.WeeklyPosition;
import uk.co.lightapps.app.forex.positions.service.PositionsService;
import uk.co.lightapps.app.forex.trades.domain.Pair;
import uk.co.lightapps.app.forex.trades.domain.Trade;
import uk.co.lightapps.app.forex.trades.domain.TradeType;
import uk.co.lightapps.app.forex.trades.services.TradeService;
import uk.co.lightapps.app.forex.transactions.services.TransactionService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.lightapps.app.forex.trades.domain.Client.IQ;
import static uk.co.lightapps.app.forex.transactions.domain.Transaction.deposit;
import static uk.co.lightapps.app.forex.transactions.domain.Transaction.opening;
import static uk.co.lightapps.app.live.LiveContentTest.db;
import static uk.co.lightapps.app.live.LiveContentTest.formatDate;
import static uk.co.lightapps.app.trades.TradesTest.rounded;
import static uk.co.lightapps.app.trades.TradesTest.rounded2dp;

/**
 * @author Asif Akhtar
 * 13/12/2020 02:02
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PositionsTest {
    @Autowired
    private PositionsService positionsService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private TransactionService transactionService;

    @Test
    public void totalNumberOfDailyPositions() {
        List<DailyPosition> records = positionsService.getDailyPositions();
        assertThat(records.size(), is(28));
    }

    @Test
    public void get_current_position() {
        Optional<DailyPosition> current = positionsService.getDay();
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
        clear();
        transactionService.save(deposit(LocalDate.of(2020, 8, 1), 905.81));
        transactionService.save(opening(LocalDate.of(2021, 1, 4), 600.7));
        importTrade("Mon 04 Jan\t09:33\tGBPUSD\tSHORT\t304\t344.09\t1.74%\t 0.01 \t6.00\t4.89\t-1.11\t-18.5%\t-0.3%\t19\t-1\t-1.0\t-\t-");
        DailyPosition dailyPosition = positionsService.logDaily(LocalDate.of(2021, 1, 4));
        assertThat(rounded(dailyPosition.getOpening()), is(600.7));
        assertThat(rounded(dailyPosition.getDifference()), is(-1.11));
        assertThat(rounded(dailyPosition.getProfit()), is(-1.11));
        assertThat(rounded(dailyPosition.getTotalProfit()), is(-1.11));

        assertThat(rounded(dailyPosition.getFees()), is(0.00));
        assertThat(rounded(dailyPosition.getPosition().getValue()), is(-306.22));
        assertThat(rounded(dailyPosition.getPosition().getPercentage()), is(-0.3381));
        assertThat(rounded(dailyPosition.getTrade()), is(540.1712));
        assertThat(rounded(dailyPosition.getAccount()), is(-0.0018));
    }

    @Test
    public void logNewDailyPosition_existing_day() {
        Optional<DailyPosition> current = positionsService.getDay();
        assertThat(current.isPresent(), is(true));
        int total = positionsService.getDailyPositions().size();

        DailyPosition dailyPosition = positionsService.logDaily(LocalDate.of(2020, 12, 18));
        assertThat(rounded(dailyPosition.getOpening()), is(356.26));
        assertThat(rounded(dailyPosition.getDifference()), is(-3.51));
        assertThat(rounded(dailyPosition.getProfit()), is(13.26));
        assertThat(rounded(dailyPosition.getTotalProfit()), is(14.17));

        assertThat(rounded(dailyPosition.getFees()), is(0.00));
        assertThat(rounded(dailyPosition.getPosition().getValue()), is(-242.37));
        assertThat(rounded(dailyPosition.getPosition().getPercentage()), is(-0.404));
        assertThat(rounded(dailyPosition.getTrade()), is(35.763));
        assertThat(rounded(dailyPosition.getAccount()), is(0.0372));
        assertThat(positionsService.getDailyPositions().size(), is(total));

        Optional<DailyPosition> afterUpdate = positionsService.getDay();
        assertThat(afterUpdate.isPresent(), is(true));
        assertThat(afterUpdate.get().getPositionId(), is(not(current.get().getPositionId())));
    }

    @Test
    public void create_new_weekly_position() {
        positionsService.deleteAllWeekly();

        WeeklyPosition weekly = new WeeklyPosition();
        weekly.setDate(LocalDate.of(2020, 11, 5));
        weekly.setStart(344.37);
        weekly.setEnd(344.34);
        positionsService.save(weekly);

        assertThat(weekly.getPositionId(), notNullValue());
        assertThat(weekly.getProfit(), notNullValue());
        assertThat(rounded(weekly.getProfit().getValue()), is(-0.03));
        assertThat(rounded(weekly.getProfit().getPercentage()) * 100, is(-0.01));
    }

//    private WeeklyPosition addInitialWeek() {
//        WeeklyPosition weekly = new WeeklyPosition();
//        weekly.setDate(LocalDate.of(2020, 12, 6));
//        weekly.setStart(344.37);
//        weekly.setEnd(344.34);
//        positionsService.save(weekly);
//        return weekly;
//    }

    private WeeklyPosition addWeek(LocalDate date, Runnable run) throws Exception {
        run.run();
        return positionsService.logWeekly(date);
    }

    private void clear() {
        positionsService.deleteAllWeekly();
        positionsService.deleteAllDaily();
        tradeService.deleteAll();
        transactionService.deleteAll();
    }

    @Test
    public void add_new_week_single_no_fees() throws Exception {
        clear();
        WeeklyPosition saved = week1(LocalDate.of(2021, 1, 3));

        assertThat(rounded(saved.getStart()), is(600.70));
        assertThat(rounded(saved.getEnd()), is(599.59));
        assertThat(saved.getProfit(), notNullValue());
        assertThat(rounded(saved.getProfit().getValue()), is(-1.11));
        assertThat(rounded(saved.getProfit().getPercentage()) * 100, is(-0.18));
        assertThat(rounded(saved.getFees()), is(0.0));
        assertThat(rounded(saved.getTotal()), is(-1.11));
        assertThat(rounded(saved.getTradesAvailable()), is(540.1712));
        assertThat(saved.getTrades(), is(1L));
        assertThat(saved.getWon(), is(0L));
        assertThat(saved.getLost(), is(1L));
        assertThat(rounded(saved.getRatio()), is(0.0));
        assertThat(rounded(saved.getRr()), is(-1.0));
        assertThat(rounded(saved.getInvested()), is(6.00));
        assertThat(rounded(saved.getRoi()), is(-0.185));
        assertThat(saved.getPositionId(), notNullValue());

        assertThat(positionsService.getWeeklyPositions().size(), is(1));
    }

    @Test
    public void add_new_week_multiple_no_fees() throws Exception {
        clear();
        WeeklyPosition saved = week2(LocalDate.of(2021, 1, 10));

        assertThat(rounded(saved.getStart()), is(599.59));
        assertThat(rounded(saved.getEnd()), is(600.04));
        assertThat(saved.getProfit(), notNullValue());
        assertThat(rounded(saved.getProfit().getValue()), is(0.45));
        assertThat(rounded(saved.getProfit().getPercentage()) * 100, is(0.08));
        assertThat(rounded(saved.getFees()), is(0.0));
        assertThat(rounded(saved.getTotal()), is(0.45));
        assertThat(saved.getTrades(), is(3L));
        assertThat(saved.getWon(), is(2L));
        assertThat(saved.getLost(), is(1L));
        assertThat(rounded(saved.getRatio()), is(0.6667));
        assertThat(rounded(saved.getRr()), is(1.0));
        assertThat(rounded(saved.getInvested()), is(18.00));
        assertThat(rounded(saved.getRoi()), is(0.025));
        assertThat(rounded2dp(saved.getTradesAvailable()), is(3636.61));

        assertThat(saved.getPositionId(), notNullValue());

        assertThat(positionsService.getWeeklyPositions().size(), is(2));
    }

    @Test
    public void add_new_week_multiple_no_fees_2() throws Exception {
        clear();
        WeeklyPosition saved = week3(LocalDate.of(2021, 1, 17));

        assertThat(rounded(saved.getStart()), is(600.04));
        assertThat(rounded(saved.getEnd()), is(582.04));
        assertThat(saved.getProfit(), notNullValue());
        assertThat(rounded(saved.getProfit().getValue()), is(-18.00));
        assertThat(rounded(saved.getProfit().getPercentage()) * 100, is(-3.0));
        assertThat(rounded(saved.getFees()), is(0.0));
        assertThat(rounded(saved.getTotal()), is(-18.0));
        assertThat(saved.getTrades(), is(2L));
        assertThat(saved.getWon(), is(0L));
        assertThat(saved.getLost(), is(2L));
        assertThat(rounded(saved.getRatio()), is(0.0));
        assertThat(rounded(saved.getRr()), is(-2.0));
        assertThat(rounded(saved.getInvested()), is(120.0));
        assertThat(rounded(saved.getRoi()), is(-0.15));
        assertThat(rounded(saved.getReturnPerTrade()), is(-3.11));
        assertThat(rounded(saved.getTotalPosition()), is(-18.66));
        assertThat(rounded2dp(saved.getTradesAvailable()), is(187.15));

        assertThat(saved.getPositionId(), notNullValue());

        assertThat(positionsService.getWeeklyPositions().size(), is(3));
    }

    private WeeklyPosition week1(LocalDate date) throws Exception {
        transactionService.save(opening(LocalDate.of(2021, 1, 1), 600.7));
        return addWeek(date, () -> importTrade("Tue 05 Jan\t09:33\tGBPUSD\tSHORT\t304\t344.09\t1.74%\t 0.01 \t6.00\t4.89\t-1.11\t-18.5%\t-0.3%\t19\t-1\t-1.0\t-\t-"));
    }

    private WeeklyPosition week2(LocalDate date) throws Exception {
        WeeklyPosition weekly = week1(date.minusWeeks(1));

        return addWeek(date, () -> {
            importTrade("Wed 13 Jan\t09:33\tGBPUSD\tSHORT\t304\t344.09\t1.74%\t 0.01 \t6.00\t6.55\t-1.11\t-18.5%\t-0.3%\t33\t-1\t1.0\t-\t-");
            importTrade("Wed 13 Jan\t09:33\tEURUSD\tLONG\t302\t338.09\t1.80%\t 0.02 \t6.00\t5.79\t-1.11\t-18.5%\t-0.3%\t22\t-1\t-1.0\t-\t-");
            importTrade("Thu 14 Jan\t09:33\tGBPUSD\tSHORT\t303\t332.09\t1.86%\t 0.01 \t6.00\t6.11\t-1.11\t-18.5%\t-0.3%\t11\t-1\t1.0\t-\t-");
        });
    }

    private WeeklyPosition week3(LocalDate date) throws Exception {
        week2(date.minusWeeks(1));

        return addWeek(date, () -> {
            importTrade("Mon 18 Jan\t09:33\tGBPUSD\tSHORT\t304\t344.09\t1.74%\t 0.01 \t60.00\t54.00\t-6.00\t-18.5%\t-0.3%\t33\t-1\t-1.0\t-\t-");
            importTrade("Wed 20 Jan\t09:33\tEURUSD\tLONG\t302\t338.09\t1.80%\t 0.02 \t60.00\t48.00\t-12.00\t-18.5%\t-0.3%\t22\t-1\t-1.0\t-\t-");
        });
    }

    private void importTrade(String line) {
        String[] split = line.split("\t");
        Trade trade = Trade.builder()
                .client(IQ)
                .date(formatDate(split[0], split[1]))
                .pair(Pair.valueOf(split[2]))
                .type(TradeType.valueOf(split[3]))
                .strategy(split[4])
                .account(db(split[5]))
                .lot(db(split[7]))
                .open(db(split[8]))
                .close(db(split[9]))
                .pips(db(split[13]))
                .rr(db(split[15]))
                .fees(db(split[16]))
                .status(split[17])
                .build();
        tradeService.closeTrade(trade.getStatus(), trade);
    }
}
