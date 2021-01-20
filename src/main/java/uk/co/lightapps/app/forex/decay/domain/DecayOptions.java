package uk.co.lightapps.app.forex.decay.domain;

import lombok.Builder;
import lombok.Data;
import uk.co.lightapps.app.forex.account.domain.Account;
import uk.co.lightapps.app.forex.positions.domain.WeeklyPosition;
import uk.co.lightapps.app.forex.trades.domain.Trade;

import java.util.List;

/**
 * @author Asif Akhtar
 * 16/01/2021 23:01
 */
@Data
@Builder
public class DecayOptions {
    private Account account;
    private List<Trade> trades;
    private List<WeeklyPosition> weeklyPositions;

    public double getProfit() {
        return account.getProfit().getValue();
    }

    public double getCurrentValue() {
        return account.getCurrent();
    }
}
