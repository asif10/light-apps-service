package uk.co.lightapps.app.forex.account.domain;

import lombok.Data;

/**
 * @author Asif Akhtar
 * 06/12/2020 01:53
 */
@Data
public class TradeStats {
    private double trades;
    private double won;
    private double lost;
    private double winRatio;
    private double rr;
    private double pips;
}
