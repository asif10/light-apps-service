package uk.co.lightapps.app.forex.decay.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.lightapps.app.forex.decay.domain.Decay;
import uk.co.lightapps.app.forex.decay.domain.DecayOptions;
import uk.co.lightapps.app.forex.positions.domain.WeeklyPosition;
import uk.co.lightapps.app.forex.trades.domain.Trade;

import java.util.List;
import java.util.Objects;

/**
 * @author Asif Akhtar
 * 14/01/2021 17:57
 */
@Service
@RequiredArgsConstructor
public class DecayService {

    public Decay calculateDecay(DecayOptions options) {
        Decay decay = new Decay();
        calculateAvailableTrades(decay, options);

        if (Objects.nonNull(options.getWeeklyPositions())) {
            calculateWeeklyDecay(decay, options);
            calculateYearlyDecay(decay, options);
            calculateDailyDecay(decay, options);
            calculateMonthlyDecay(decay, options);
            calculateAverageReturns(decay, options);
        }
        return decay;
    }

    private void calculateAverageReturns(Decay decay, DecayOptions options) {
        double weeklyReturn = calculateAverageWeeklyReturn(options);
        decay.setWeeklyAverageReturn(weeklyReturn);
        decay.setDailyAverageReturn(weeklyReturn / 5);
        decay.setAnnualAverageReturn(weeklyReturn * 52);
        decay.setMonthlyAverageReturn(decay.getAnnualAverageReturn() / 12);
    }

    private void calculateAvailableTrades(Decay decay, DecayOptions options) {
        double profit = options.getProfit();
        List<Trade> trades = options.getTrades();
        double returnPerTrade = profit / trades.size();
        decay.setTradesAvailable(Math.abs(options.getCurrentValue() / returnPerTrade));
    }

    private void calculateWeeklyDecay(Decay decay, DecayOptions options) {
        double currentPosition = options.getCurrentValue();
        double returnsPerWeek = calculateAverageWeeklyReturn(options);
        decay.setWeeksDecay(Math.abs(currentPosition / returnsPerWeek));
    }

    private void calculateYearlyDecay(Decay decay, DecayOptions options) {
        double currentPosition = options.getCurrentValue();
        double year = calculateAverageWeeklyReturn(options) * 52;
        decay.setYearsDecay(Math.abs(currentPosition / year));
    }

    private void calculateMonthlyDecay(Decay decay, DecayOptions options) {
        double currentPosition = options.getCurrentValue();
        double year = (calculateAverageWeeklyReturn(options) * 52) / 12;
        decay.setMonthsDecay(Math.abs(currentPosition / year));
    }

    private void calculateDailyDecay(Decay decay, DecayOptions options) {
        double currentPosition = options.getCurrentValue();
        double daily = (calculateAverageWeeklyReturn(options) * 52) / 365;
        decay.setDaysDecay(Math.abs(currentPosition / daily));
    }

    private double calculateAverageWeeklyReturn(DecayOptions options) {
        List<WeeklyPosition> weeklyPositions = options.getWeeklyPositions();
        double returns = weeklyPositions.stream().mapToDouble(e -> e.getProfit().getValue()).sum();
        return returns / weeklyPositions.size();
    }
}
