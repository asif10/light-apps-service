package uk.co.lightapps.app.live;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.lightapps.app.forex.history.domain.Snapshot;
import uk.co.lightapps.app.forex.history.service.SnapshotService;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.lightapps.app.trades.TradesTest.rounded;

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
// 11 	 2.36 	 11 	 15 	42.31%	-259	-3.25

    //	TRADES	WINS	LOSSES		RETURN	AVG	 PIPS 	 + 	 - 		WINS
    // 301 	 16 	 9 	 7 	56.3%	+ 0.28 	+ 0.02 	- 51.00	+ 21 	- 68		+ 0.38
    // 302 	 7 	 1 	 6 	14.3%	- 2.95	- 0.42	- 186.00	+ 14 			+ 3.44
    // 303 	 1 	 -   	 1 	0.0%	- 0.30		- 19.00	#DIV/0!			#DIV/0!
    // 304 	 2 	 1 	 1 	50.0%	- 0.14		- 3.00	+ 19 			+ 2.83
    @Test
    public void check_weekly_position() throws Exception {
        Snapshot snapshot = service.calculateLastWeek();
        assertThat(rounded(snapshot.getWinRatio()), is(0.4231));
        assertThat(rounded(snapshot.getPips()), is(-259.0));
        assertThat(rounded(snapshot.getRr()), is(-3.25));
    }

}