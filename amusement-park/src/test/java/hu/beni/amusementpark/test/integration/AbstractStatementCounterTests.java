package hu.beni.amusementpark.test.integration;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.vladmihalcea.sql.SQLStatementCountValidator;

import hu.beni.amusementpark.AmusementParkApplication;
import hu.beni.amusementpark.config.DataSourceConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = { AmusementParkApplication.class,
		DataSourceConfig.class })
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@ActiveProfiles("stmtCount")
public abstract class AbstractStatementCounterTests {

	protected long insert;
	protected long select;
	protected long update;
	protected long delete;

	@Before
	public void setUp() {
		reset();
	}

	protected void reset() {
		SQLStatementCountValidator.reset();
	}

	protected void assertStatements() {
		SQLStatementCountValidator.assertSelectCount(select);
		SQLStatementCountValidator.assertInsertCount(insert);
		SQLStatementCountValidator.assertUpdateCount(update);
		SQLStatementCountValidator.assertDeleteCount(delete);
	}

}
