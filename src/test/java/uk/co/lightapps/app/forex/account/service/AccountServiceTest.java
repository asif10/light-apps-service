package uk.co.lightapps.app.forex.account.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.lightapps.app.forex.account.domain.Account;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Asif Akhtar
 * 07/12/2020 19:03
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountServiceTest {
    @Autowired
    private AccountService accountService;

    @Test
    public void calculateOpeningBalance() {
        Account account = accountService.getAccountInfo();
        assertThat(account.getOpening(), is(344.37));
    }

    @Test
    public void calculateDepositBalance() {
        Account account = accountService.getAccountInfo();
        assertThat(account.getDeposited(), is(600.00));
    }

    @Test
    public void calculateCurrentBalance() {
        Account account = accountService.getAccountInfo();
        assertThat(account.getCurrent(), is(354.67));
    }

    @Test
    public void calculateFees() {
        Account account = accountService.getAccountInfo();
        assertThat(round(account.getFees()), is(-0.58));
    }

    @Test
    public void calculateProfitExcFees() {
        Account account = accountService.getAccountInfo();
        assertThat(round(account.getProfitExclFees()), is(10.88));
    }

    @Test
    public void calculateStartPosition() {
        Account account = accountService.getAccountInfo();
        assertThat(round(account.getStartPosition().getValue()), is(-255.63));
        assertThat(roundP(account.getStartPosition().getPercentage()), is(-0.4261));
    }

    @Test
    public void calculateCurrentPosition() {
        Account account = accountService.getAccountInfo();
        assertThat(round(account.getCurrentPosition().getValue()), is(-245.33));
        assertThat(roundP(account.getCurrentPosition().getPercentage()), is(-0.4089));
    }

    @Test
    public void calculateAvailableTradesOnStart() {
        Account account = accountService.getAccountInfo();
        assertThat(round(account.getTradesAvailableStart()), is(34.44));
    }

    @Test
    public void calculateAvailableTrades() {
        Account account = accountService.getAccountInfo();
        assertThat(round(account.getTradesAvailableCurrent()), is(35.47));
    }

    @Test
    public void calculateReturn() {
        Account account = accountService.getAccountInfo();
        assertThat(round(account.getProfit().getValue()), is(10.30));
        assertThat(roundP(account.getProfit().getPercentage()), is(0.0299));
    }

    @Test
    public void calculateTotalTrades() {
        Account account = accountService.getAccountInfo();
        assertThat(round(account.getTotalTrades().getTrades()), is(83.0));
        assertThat(round(account.getTotalTrades().getWon()), is(56.0));
        assertThat(round(account.getTotalTrades().getLost()), is(27.0));
        assertThat(roundP(account.getTotalTrades().getWinRatio()), is(0.6747));
    }

    @Test
    public void calculateWeeklyTrades() {
        Account account = accountService.getAccountInfo();
        assertThat(round(account.getTradesThisWeek().getTrades()), is(13.0));
        assertThat(round(account.getTradesThisWeek().getWon()), is(8.0));
        assertThat(round(account.getTradesThisWeek().getLost()), is(5.0));
        assertThat(roundP(account.getTradesThisWeek().getWinRatio()), is(0.6154));
    }

    @Test
    public void calculateWeeklyEarnings() {
        Account account = accountService.getAccountInfo();
        assertThat(round(account.getProfitThisWeek()), is(5.28));
    }

    @Test
    public void calculateOpenEarnings() {
        Account account = accountService.getAccountInfo();
        assertThat(round(account.getOpenProfit().getValue()), is(-1.16));
        assertThat(round(account.getOpenProfitIncFees().getValue()), is(-1.19));
    }

    private double round(double value) {
        return new BigDecimal("" + value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private double roundP(double value) {
        return new BigDecimal("" + value).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }


    @Test
    public void calculateTradesSumValue() {
        assertThat(accountService.sumTradesReturnValue(), is(10.88));
    }


}