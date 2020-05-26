package uk.co.lightapps.app.earnings;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Asif Akhtar
 * 25/05/2020 21:49
 */
@Data
@AllArgsConstructor
public class AuthenticationBean {
    private String message;

    @Override
    public String toString() {
        return String.format("HelloWorldBean [message=%s]", message);
    }
}
