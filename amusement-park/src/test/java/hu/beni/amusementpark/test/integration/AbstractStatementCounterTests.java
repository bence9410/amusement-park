package hu.beni.amusementpark.test.integration;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.vladmihalcea.sql.SQLStatementCountValidator;

import hu.beni.amusementpark.AmusementParkApplication;
import hu.beni.amusementpark.config.DataSourceConfig;
import hu.beni.amusementpark.config.DataSourceInitializator;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.repository.AmusementParkRepository;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = { AmusementParkApplication.class,
		DataSourceConfig.class, DataSourceInitializator.class })
public abstract class AbstractStatementCounterTests {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	protected AmusementParkRepository amusementParkRepository;

	protected long amusementParkId;
	protected int amusementParkCapital;
	protected int amusementParkEntranceFee;

	protected long anotherAmusementParkId;

	protected long machineId;
	protected int machineTicketPrice;

	protected String testVisitorEmail = "test@gmail.com";
	protected String inParkVisitorEmail = "inPark@gmail.com";
	protected int visitorSpendingMoney = 1000;

	protected long guestBookId;

	protected long insert;
	protected long select;
	protected long update;
	protected long delete;

	@PostConstruct
	public void init() {
		AmusementPark amusementPark = amusementParkRepository
				.findOne(Example.of(AmusementPark.builder().name("test park 100").build())).get();
		amusementParkId = amusementPark.getId();
		amusementParkCapital = amusementPark.getCapital();
		amusementParkEntranceFee = amusementPark.getEntranceFee();

		anotherAmusementParkId = amusementParkRepository
				.findOne(Example.of(AmusementPark.builder().name("test park 101").build())).get().getId();

		Machine machine = entityManager
				.createQuery("select m from Machine m where m.fantasyName='test Titanic'", Machine.class)
				.getSingleResult();
		machineId = machine.getId();
		machineTicketPrice = machine.getTicketPrice();

		guestBookId = entityManager
				.createQuery("select g from GuestBookRegistry g where g.textOfRegistry='test Amazeing.'",
						GuestBookRegistry.class)
				.getSingleResult().getId();
	}

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
