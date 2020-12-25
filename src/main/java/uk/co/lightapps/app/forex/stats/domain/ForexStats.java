package uk.co.lightapps.app.forex.stats.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.lightapps.app.forex.account.domain.Figure;

/**
 * @author Asif Akhtar
 * 22/12/2020 17:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForexStats {
    private long trades;
    private long pips;
    private long tradingDays;
    private long daysPassed;
    private long daysTraded;
    private long daysNotTraded;
    private double timesPerDay;
    private Figure manuallyClosedTrades;
}
