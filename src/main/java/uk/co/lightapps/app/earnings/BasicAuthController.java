package uk.co.lightapps.app.earnings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Asif Akhtar
 * 25/05/2020 23:44
 */
@CrossOrigin(origins = "http://localhost:4201")
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class BasicAuthController {
    @GetMapping(path = "/basicauth")
    public AuthenticationBean basicauth() {
        return new AuthenticationBean("You are authenticated");
    }

    @GetMapping(path = "/logout-success")
    public void logoutSuccess() {
        log.info("Logged Out");
    }
}
