package uk.co.lightapps.app.earnings;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Asif Akhtar
 * 28/05/2020 01:16
 */
@RestController
@RequestMapping("/content")
@Slf4j
public class ContentController {
    @RequestMapping("/message")
    @CrossOrigin(origins = "*", maxAge = 3600,
            allowedHeaders = {"x-auth-token", "x-requested-with", "x-xsrf-token"})
    public String home() {
        return new Gson().toJson("Hello World");
    }
}
