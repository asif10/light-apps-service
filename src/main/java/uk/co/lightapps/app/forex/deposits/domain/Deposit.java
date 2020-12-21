package uk.co.lightapps.app.forex.deposits.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * @author Asif Akhtar
 * 06/12/2020 01:27
 */
@AllArgsConstructor
@NoArgsConstructor
@Document
@Data
public class Deposit {
    @Id
    private String depositId;
    private LocalDateTime date;
    private double amount;
}
