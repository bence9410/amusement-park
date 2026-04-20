package hu.bence.amusementpark.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebContextController {

    @GetMapping(value = "{path:[^\\.]*}")
    public String redirect() {
        return "forward:/index.html";
    }
}