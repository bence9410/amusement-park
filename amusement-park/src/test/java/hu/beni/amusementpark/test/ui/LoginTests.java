package hu.beni.amusementpark.test.ui;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.helper.DriverFacade;
import hu.beni.amusementpark.helper.ErrorPhotoQuitWatcher;
import hu.beni.amusementpark.helper.po.LoginPageObject;
import hu.beni.amusementpark.helper.po.MessagePageObject;
import hu.beni.amusementpark.helper.po.NavbarPageObject;
import hu.beni.amusementpark.repository.VisitorRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LoginTests {

    private static final FirefoxOptions FO;
    private static final String PASS = "Pass1234";
    private static final String ADMIN_EMAIL = "nembence1994@gmail.com";
    private static final String VISITOR_EMAIL = "fenicser85@gmail.com";

    static {
        System.setProperty("webdriver.gecko.driver", "/home/jenifer/Downloads/geckodriver");
        FirefoxBinary firefoxBinary = new FirefoxBinary();
        firefoxBinary.addCommandLineOptions("--headless");
        FO = new FirefoxOptions();
        FO.setBinary(firefoxBinary);
    }
    @LocalServerPort
    private int port;

    @Autowired
    private VisitorRepository visitorRepository;

    private DriverFacade driverFacade = new DriverFacade(new FirefoxDriver(FO));

    @Rule
    public TestWatcher watcher = new ErrorPhotoQuitWatcher(driverFacade);

    private LoginPageObject loginPageObject = new LoginPageObject(driverFacade);
    private MessagePageObject messagePageObject = new MessagePageObject(driverFacade);
    private NavbarPageObject navbarPageObject = new NavbarPageObject(driverFacade);
    private Visitor user;

    @Before
    public void setUp() {
        driverFacade.get("http://localhost:" + port);
    }

    @Test
    public void positiveLoginAdminTest() {
        user = visitorRepository.findById(ADMIN_EMAIL).get();
        loginPageObject.attemptToLogin(user.getEmail(), PASS);
        messagePageObject.messagesShowAndClickHide("Successfull login.");
        navbarPageObject.navbarCorrectPersonControl(user);
        logout(8);

    }

    @Test
    public void positiveLoginVisitorTest() {
        Visitor visitor = visitorRepository.findById(VISITOR_EMAIL).get();
        driverFacade.get("http://localhost:" + port);

        loginPageObject.attemptToLogin(visitor.getEmail(), PASS);
        messagePageObject.messagesShowAndClickHide("Successfull login.");
        navbarPageObject.navbarCorrectPersonControl(visitor);
        logout(7);

    }

    @Test
    public void negativeLoginInputValidationTest() {
        driverFacade.get("http://localhost:" + port);
        LoginPageObject loginPageObject = new LoginPageObject(driverFacade);
        loginPageObject.attemptToLogin("nembence1994@gmailcom", PASS);
        driverFacade.text(".v-input__control .v-messages__message",
                "Email must be well-formed, for example: somebody@example.com");
        loginPageObject.attemptToLogin("nembence1994gmail.com", PASS);
        driverFacade.text(".v-input__control .v-messages__message",
                "Email must be well-formed, for example: somebody@example.com");
        loginPageObject.attemptToLogin("nembence1994gmailcom", "Pass123");
        driverFacade.waitUntil(driver -> {
            List<WebElement> errorMessage = driver
                    .findElements(By.cssSelector(".v-input__control .v-messages__message"));
            List<String> errorM = new ArrayList<>();
            for (int i = 0; i < errorMessage.size(); i++) {
                errorM.add(errorMessage.get(i).getText());
            }

            boolean zero = errorM.get(0).equals("Email must be well-formed, for example: somebody@example.com");
            boolean one = errorM.get(1).equals(
                    "Must contain at least one upper and lowercase characters and number and the length must be between 8-25.");

            return zero && one;
        });

    }

    @Test
    public void negativeLoginBusinessValidationTest() {
        driverFacade.get("http://localhost:" + port);
        LoginPageObject loginPageObject = new LoginPageObject(driverFacade);
        loginPageObject.attemptToLogin(ADMIN_EMAIL, "Pass123f");
        messagePageObject.messagesShowAndClickHide("Bad credentials");
        loginPageObject.attemptToLogin("apple@gmail.com", "Pass1234");
        messagePageObject.messagesShowAndClickHide("Could not find user with email: apple@gmail.com.");
    }

    public void logout(int buttons) {
        driverFacade.click(".v-toolbar__content>:nth-child(" + buttons + ")");
        messagePageObject.messagesShowAndClickHide("Successfull logout.");
    }

}
