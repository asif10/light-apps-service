package uk.co.lightapps.app.forex.trades.domain;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Asif Akhtar
 * 25/01/2021 00:12
 */
@Data
public class TradesGroup {
    private LocalDate date;
    private String week;
    private List<Trade> trades;
}
