package uk.co.lightapps.app.live;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.lightapps.app.forex.account.service.AccountService;
import uk.co.lightapps.app.forex.decay.domain.Decay;
import uk.co.lightapps.app.forex.decay.domain.DecayOptions;
import uk.co.lightapps.app.forex.decay.services.DecayService;
import uk.co.lightapps.app.forex.positions.service.PositionsService;
import uk.co.lightapps.app.forex.trades.services.TradeService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.lightapps.app.trades.TradesTest.rounded2dp;

/**
 * @author Asif Akhtar
 * 14/01/2021 19:14
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("live")
public class LiveDecayServiceTest {
    @Autowired
    private DecayService service;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private PositionsService positionsService;

    @Test
    public void calculate_current_decay_stats() {
        DecayOptions options = DecayOptions.builder()
                .account(accountService.getAccountInfo())
                .weeklyPositions(positionsService.getWeeklyPositions())
                .trades(tradeService.getAll())
                .build();

        Decay decay = service.calculateDecay(options);

        assertThat(rounded2dp(decay.getTradesAvailable()), is(4916.47));
        assertThat(rounded2dp(decay.getWeeksDecay()), is(378.19));
        assertThat(rounded2dp(decay.getMonthsDecay()), is(87.27));
        assertThat(rounded2dp(decay.getYearsDecay()), is(7.27));

        assertThat(rounded2dp(decay.getWeeklyAverageReturn()), is(-1.58));
        assertThat(rounded2dp(decay.getDailyAverageReturn()), is(-0.32));
        assertThat(rounded2dp(decay.getMonthlyAverageReturn()), is(-6.85));
        assertThat(rounded2dp(decay.getAnnualAverageReturn()), is(-82.16));
    }


}