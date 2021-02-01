package uk.co.lightapps.app.forex.account.domain;

import lombok.Data;

/**
 * @author Asif Akhtar
 * 31/01/2021 02:21
 */
@Data
public class PLSplit {
    private double returnPerTrade;
    private double totalReturn;
    private double percentage;
}
