package hu.beni.amusementpark.test.ui;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileCopyUtils;

import hu.beni.amusementpark.helper.DriverFacade;
import hu.beni.amusementpark.helper.po.AmusementParkPageObject;
import hu.beni.amusementpark.helper.po.LoginAndSignUpPageObject;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SignUp {

	static {
		System.setProperty("webdriver.gecko.driver", "/home/jenifer/Downloads/geckodriver");
		// System.setProperty("webdriver.gecko.driver",
		// "C:\\geckodriver\\geckodriver.exe");
	}
	@LocalServerPort
	private int port;

	private DriverFacade driverFacade;

	@Rule
	public TestWatcher watcher = new TestWatcher() {
		@Override
		protected void failed(Throwable e, Description description) {
			try {
				File f = new File("/home/jenifer/Documents/kepek/" + description.getMethodName()
						+ LocalDateTime.now().toString().replace(':', '-') + ".png");
				// File f = new File("C:\\kepek\\" + description.getMethodName()
				// + LocalDateTime.now().toString().replace(':', '-') + ".jpg");
				File a = driverFacade.takeScreenshot();

				FileCopyUtils.copy(a, f);
				driverFacade.quit();
			} catch (IOException e1) {
				throw new RuntimeException(e1);

			}

		}

		@Override
		protected void succeeded(Description description) {
			driverFacade.quit();
		}
	};

	@Before
	public void setUp() {
		FirefoxBinary firefoxBinary = new FirefoxBinary();
		firefoxBinary.addCommandLineOptions("--headless");
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setBinary(firefoxBinary);
		driverFacade = new DriverFacade(new FirefoxDriver(firefoxOptions));
	}

}
