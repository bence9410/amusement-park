package hu.bence.amusementpark.test.integration;

import hu.bence.amusementpark.AmusementParkApplication;
import hu.bence.amusementpark.config.DataSourceInitializator;
import hu.bence.amusementpark.config.RestTemplateConfig;
import hu.bence.amusementpark.dto.response.AmusementParkSearchResponseDto;
import hu.bence.amusementpark.dto.response.GuestBookRegistrySearchResponseDto;
import hu.bence.amusementpark.dto.response.MachineSearchResponseDto;
import hu.bence.amusementpark.dto.response.UserResponseDto;
import hu.bence.amusementpark.entity.AmusementPark;
import hu.bence.amusementpark.entity.Machine;
import hu.bence.amusementpark.helper.RestResponsePage;
import hu.bence.amusementpark.repository.AmusementParkRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Example;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {AmusementParkApplication.class, RestTemplateConfig.class, DataSourceInitializator.class})
public class AbstractApplicationTests {

    protected static final ParameterizedTypeReference<RestResponsePage<AmusementParkSearchResponseDto>> PAGED_AMUSEMENT_PARK = new ParameterizedTypeReference<>() {
    };
    protected static final ParameterizedTypeReference<RestResponsePage<MachineSearchResponseDto>> PAGED_MACHINE = new ParameterizedTypeReference<>() {
    };
    protected static final ParameterizedTypeReference<RestResponsePage<GuestBookRegistrySearchResponseDto>> PAGED_GUEST_BOOK_REGISTRY = new ParameterizedTypeReference<>() {
    };
    protected static final ParameterizedTypeReference<RestResponsePage<UserResponseDto>> PAGED_USER = new ParameterizedTypeReference<>() {
    };

    @LocalServerPort
    private int port;

    @Autowired
    private AmusementParkRepository amusementParkRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    protected RestTemplate restTemplate;

    protected long amusementParkId;
    protected long machineId;
    protected long amusementParkIdBence;

    @PostConstruct
    private void setUp() {
        AmusementPark amusementPark = amusementParkRepository
                .findOne(Example.of(AmusementPark.builder().name("test park 100").build())).get();
        amusementParkId = amusementPark.getId();

        Machine machine = entityManager
                .createQuery("select m from Machine m where m.fantasyName='test Titanic'", Machine.class)
                .getSingleResult();
        machineId = machine.getId();

        amusementParkIdBence = amusementParkRepository
                .findOne(Example.of(AmusementPark.builder().name("Bence's park").build())).get().getId();
    }

    protected String getBaseUrl() {
        return "http://localhost:" + port + "/api/";
    }

    protected void login(String userName, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        ResponseEntity<UserResponseDto> response = restTemplate.exchange(getBaseUrl() + "login", HttpMethod.POST,
                new HttpEntity<>(createMap(userName, password), headers), UserResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getFirst("Set-Cookie").contains("SESSION="));
        UserResponseDto userResponseDto = response.getBody();
        assertEquals(userName, userResponseDto.getName());
    }

    private MultiValueMap<String, String> createMap(String name, String password) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", name);
        map.add("password", password);
        return map;
    }

    protected String encode(String input) {
        return URLEncoder.encode(input, StandardCharsets.UTF_8);
    }
}
