package tech.loga.ui;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("intelligent-service")
public class Controller {

    @Autowired
    private UIAgent uiAgent;

    @PostMapping(path = "process", produces = MediaType.APPLICATION_JSON_VALUE)
    public void process(@RequestParam String dysfunction, HttpServletResponse response){
        uiAgent.sendMessage(dysfunction, response);
    }
}
