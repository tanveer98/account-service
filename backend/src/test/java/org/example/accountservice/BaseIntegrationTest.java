package org.example.accountservice;

import org.junit.jupiter.api.AfterEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest
public abstract class BaseIntegrationTest {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	protected JdbcClient jdbcClient;



	@AfterEach
	void cleanup() {
		jdbcClient.sql("DELETE FROM ledger").update();
		jdbcClient.sql("DELETE FROM account").update();
		jdbcClient.sql("DELETE FROM bank_user").update();
	}
}
