package hu.beni.amusementpark.config;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DataSourceInitializator implements ApplicationRunner {

	private final DataSource dataSource;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		try (Connection conn = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("test-data.sql"));
		}
	}

}
