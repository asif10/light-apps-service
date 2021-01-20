package uk.co.lightapps.app.forex.history.domain;

import lombok.Data;

/**
 * @author Asif Akhtar
 * 18/01/2021 18:53
 */
@Data
public class Snapshot {
    private double winRatio;
    private double pips;
    private double wins;
    private double losses;
    private double rr;
}
