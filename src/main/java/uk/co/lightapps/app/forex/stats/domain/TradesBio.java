package uk.co.lightapps.app.forex.stats.domain;

import lombok.Builder;
import lombok.Data;

/**
 * @author Asif Akhtar
 * 02/04/2021 20:58
 */
@Data
@Builder
public class TradesBio {
    private String pair;
    private String ccy;
    private String strategy;
    private long won;
    private long lost;
    private long fees;
    private double profit;
    private double position;
    private double loss;
    private double pips;
    private double rr;
    private long morning;
    private long afternoon;
    private long evening;
    private long late;
    private String day;
    private String month;
    private String time;
    private long major;
    private long minor;
    private long exotics;
}
