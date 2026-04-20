package hu.bence.amusementpark.test.integration;

import com.vladmihalcea.sql.SQLStatementCountValidator;
import hu.bence.amusementpark.AmusementParkApplication;
import hu.bence.amusementpark.config.DataSourceConfig;
import hu.bence.amusementpark.config.DataSourceInitializator;
import hu.bence.amusementpark.entity.AmusementPark;
import hu.bence.amusementpark.entity.GuestBookRegistry;
import hu.bence.amusementpark.entity.Machine;
import hu.bence.amusementpark.repository.AmusementParkRepository;
import hu.bence.amusementpark.repository.VisitorRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.domain.Example;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = {AmusementParkApplication.class,
        DataSourceConfig.class, DataSourceInitializator.class})
public abstract class AbstractStatementCounterTests {

    @Autowired
    protected AmusementParkRepository amusementParkRepository;
    @Autowired
    protected VisitorRepository visitorRepository;
    protected long amusementParkId;
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
    @Autowired
    private EntityManager entityManager;

    @PostConstruct
    public void init() {
        AmusementPark amusementPark = amusementParkRepository
                .findOne(Example.of(AmusementPark.builder().name("test park 100").build())).get();
        amusementParkId = amusementPark.getId();
        amusementParkEntranceFee = amusementPark.getEntranceFee();

        anotherAmusementParkId = amusementParkRepository
                .findOne(Example.of(AmusementPark.builder().name("test park 101").build())).get().getId();

        Machine machine = entityManager
                .createQuery("select m from Machine m where m.fantasyName='test Titanic'", Machine.class)
                .getSingleResult();
        machineId = machine.getId();
        machineTicketPrice = machine.getTicketPrice();

        guestBookId = entityManager
                .createQuery("select g from GuestBookRegistry g where g.textOfRegistry='test Amazing.'",
                        GuestBookRegistry.class)
                .getSingleResult().getId();
    }

    @BeforeEach
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
