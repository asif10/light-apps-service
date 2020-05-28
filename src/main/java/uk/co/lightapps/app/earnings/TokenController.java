package uk.co.lightapps.app.earnings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;

/**
 * @author Asif Akhtar
 * 28/05/2020 01:15
 */
@RestController
@RequestMapping("/tokens")
@Slf4j
public class TokenController {

    @RequestMapping("/token")
    public Map<String, String> token(HttpSession session) {
        return Collections.singletonMap("token", session.getId());
    }
}
