package uk.co.lightapps.app;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @RequestMapping("/open/hello")
    public String streamed() {
        return new Gson().toJson("Hello World - " + System.currentTimeMillis());
    }

    @RequestMapping("/detail")
    public Content detail() {
        Content content = new Content();
        content.setMessage("Hello World - " + System.currentTimeMillis());
        content.setDate(LocalDate.now());
        content.setDateTime(LocalDateTime.now());
        return content;
    }
}
