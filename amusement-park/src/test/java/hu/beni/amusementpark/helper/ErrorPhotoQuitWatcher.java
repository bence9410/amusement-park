package hu.beni.amusementpark.helper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.springframework.util.FileCopyUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ErrorPhotoQuitWatcher extends TestWatcher {

    private final DriverFacade driverFacade;

    @Override
    protected void failed(Throwable e, Description description) {
        try {
            File f = new File("/home/jenifer/Documents/kepek/" + description.getMethodName()
                    + LocalDateTime.now().toString().replace(':', '-') + ".png");
            File a = driverFacade.takeScreenshot();

            FileCopyUtils.copy(a, f);
        } catch (IOException e1) {
            throw new RuntimeException(e1);

        } finally {
            driverFacade.quit();
        }

    }

    @Override
    protected void succeeded(Description description) {
        driverFacade.quit();
    }

}
