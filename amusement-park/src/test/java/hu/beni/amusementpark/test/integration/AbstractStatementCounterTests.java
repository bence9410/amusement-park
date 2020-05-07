package hu.beni.amusementpark.test.integration;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.vladmihalcea.sql.SQLStatementCountValidator;

import hu.beni.amusementpark.AmusementParkApplication;
import hu.beni.amusementpark.config.DataSourceConfig;
import hu.beni.amusementpark.repository.AmusementParkRepository;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = { AmusementParkApplication.class,
		DataSourceConfig.class })
public abstract class AbstractStatementCounterTests {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	protected AmusementParkRepository amusementParkRepository;

	protected final Long amusementParkId = 10L;
	protected final int amusementParkCapital = 5000;
	protected final int amusementParkEntranceFee = 200;

	protected final Long machineId = 10L;
	protected final int machineTicketPrice = 20;

	protected final String testVisitorEmail = "test@gmail.com";
	protected final String inParkVisitorEmail = "inPark@gmail.com";
	protected final int visitorSpendingMoney = 1000;

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
		entityManager.flush();
		SQLStatementCountValidator.assertSelectCount(select);
		SQLStatementCountValidator.assertInsertCount(insert);
		SQLStatementCountValidator.assertUpdateCount(update);
		SQLStatementCountValidator.assertDeleteCount(delete);
	}

}
