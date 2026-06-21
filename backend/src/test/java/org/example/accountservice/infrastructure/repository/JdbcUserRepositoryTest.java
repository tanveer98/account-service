package org.example.accountservice.infrastructure.repository;

import org.example.accountservice.BaseIntegrationTest;
import org.example.accountservice.domain.UserName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JdbcUserRepositoryTest extends BaseIntegrationTest {
    @Autowired
    JdbcUserRepository underTest;

    @Test
    void testInsertAndFind() {
        var name = new UserName("John Woo");

        var savedId = underTest.save(name);

        var found = underTest.findById(savedId).orElseThrow();
        assertEquals(name, found.userName());
    }

}