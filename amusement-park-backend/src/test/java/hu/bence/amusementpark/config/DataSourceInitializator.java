package hu.bence.amusementpark.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Arrays;

@RequiredArgsConstructor
public class DataSourceInitializator implements ApplicationRunner {

    private final DataSource dataSource;
    private final ConfigurableEnvironment environment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            if (Arrays.asList(environment.getActiveProfiles()).contains("postgres")) {
                ScriptUtils.executeSqlScript(conn, new ClassPathResource("test-data-postgres.sql"));
            } else {
                ScriptUtils.executeSqlScript(conn, new ClassPathResource("test-data.sql"));
            }
        }
    }

}