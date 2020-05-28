package uk.co.lightapps.app.earnings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author Asif Akhtar
 * 25/05/2020 23:44
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class BasicAuthController {
    @GetMapping(path = "/basicauth")
    public AuthenticationBean basicauth() {
        return new AuthenticationBean("You are authenticated");
    }

    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }
}
