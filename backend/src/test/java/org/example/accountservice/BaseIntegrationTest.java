package org.example.accountservice;

import org.junit.jupiter.api.AfterEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
public abstract class BaseIntegrationTest {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	protected JdbcClient jdbcClient;

	@Autowired
	protected RestTestClient restClientTest;

	@AfterEach
	void cleanup() {
		jdbcClient.sql("DELETE FROM ledger").update();
		jdbcClient.sql("DELETE FROM account").update();
		jdbcClient.sql("DELETE FROM bank_user").update();
	}
}
