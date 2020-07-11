package uk.co.lightapps.app.earnings;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Asif Akhtar
 * 15/06/2020 02:00
 */
@Data
public class Content {
    private String message;
    private LocalDate date;
    private LocalDateTime dateTime;
}
