package uk.co.lightapps.app;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Asif Akhtar
 * 11/07/2020 21:33
 */
@RestController
@RequestMapping("/sockets")
@Slf4j
public class SocketsController {
    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/open/send")
    @SendTo("/topic/greeting")
    public String topic() {
        return new Gson().toJson(message());
    }

    private String message() {
        return "Hello World - " + System.currentTimeMillis();
    }

    @PostMapping("/open/publish")
    public void publishUpdates() {
        System.out.println("Message: " + message());
        template.convertAndSend("/topic/greeting", message());
    }

    @PostMapping("/open/error")
    public void publishError() {
        System.out.println("Message: " + message());
        template.convertAndSend("/topic/fake", message());
    }

    @MessageExceptionHandler
    public String handleException(Throwable exception) {
        template.convertAndSend("/errors", exception.getLocalizedMessage());
        return exception.getMessage();
    }
}
