package uk.co.lightapps.app.jwt.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Asif Akhtar
 * 28/05/2020 20:42
 */
@Data
@AllArgsConstructor
public class JwtResponse {
    private final String token;
}
