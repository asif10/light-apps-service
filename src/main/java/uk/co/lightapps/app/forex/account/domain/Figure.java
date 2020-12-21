package uk.co.lightapps.app.forex.account.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Asif Akhtar
 * 06/12/2020 01:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Figure {
    private double value;
    private double percentage;
}
