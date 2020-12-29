package uk.co.lightapps.app.forex.strategies.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Asif Akhtar
 * 25/12/2020 21:06
 */
@Data
@Builder
public class Strategies {
    private int id;
    private String label;
    private String description;
    private long trades;
    private long won;
    private long lost;
    private double ratio;
    private double profit;
    private double average;
    private double pips;
    private double profitPerWin;
    private double profitPerLoss;
    private double pipsPerWin;
    private double pipsPerLoss;

}
