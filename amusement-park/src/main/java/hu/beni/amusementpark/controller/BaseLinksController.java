package hu.beni.amusementpark.controller;

import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static hu.beni.amusementpark.factory.LinkFactory.*;

@RestController
@RequestMapping("/api")
public class BaseLinksController {

    @GetMapping("/links")
    public Link[] getBaseLinks() {
        return new Link[]{createAmusementParkLink(), createVisitorSignUpLink(), createLoginLink(), createLogoutLink(),
                createMeLink(), createVisitorLink()};
    }

}
