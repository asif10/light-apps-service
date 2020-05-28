package uk.co.lightapps.app.earnings;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
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
    public String message() {
        return new Gson().toJson("Hello World");
    }

    @RequestMapping("/hello")
    public String hello() {
        return new Gson().toJson("Hello World - " + System.currentTimeMillis());
    }
}
