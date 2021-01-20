package uk.co.lightapps.app.forex.decay.domain;

import lombok.Data;

/**
 * @author Asif Akhtar
 * 14/01/2021 17:11
 */
@Data
public class Decay {
    private double tradesAvailable;
    private double weeklyAverageReturn;
    private double dailyAverageReturn;
    private double monthlyAverageReturn;
    private double annualAverageReturn;
    private double daysDecay;
    private double weeksDecay;
    private double monthsDecay;
    private double yearsDecay;
}
