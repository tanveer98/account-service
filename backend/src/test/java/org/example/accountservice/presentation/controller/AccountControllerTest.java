package org.example.accountservice.presentation.controller;

import org.example.accountservice.BaseIntegrationTest;
import org.example.accountservice.application.AccountService;
import org.example.accountservice.domain.Currency;
import org.example.accountservice.domain.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Map;

// Only testing the unhappy path to make sure validations run correctly and exceptions are mapped correct, in prod there would be tests for the happy path as well
class AccountControllerTest extends BaseIntegrationTest {

    @Autowired
    AccountService accountService;

    @ParameterizedTest
    @ValueSource(strings = {"-1", "0"})
    void testErrorThrownOnInvalidCreditRequest(String amount) {
        restClientTest.post()
                .uri("/v1/accounts/123/credit")
                .body(Map.of("amount", amount))
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }


    @ParameterizedTest
    @ValueSource(strings = {"-1", "0"})
    void testErrorThrownOnInvalidDebitRequest(String amount) {
        restClientTest.post()
                .uri("/v1/accounts/123/credit")
                .body(Map.of("amount", amount))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "0"})
    void testErrorThrownOnInvalidExchangeRequest(String amount) {
        restClientTest.post()
                .uri("/v1/accounts/exchange")
                .body(Map.of(
                        "sourceAccountId", "222",
                        "sourceAmountToConvert", amount,
                        "targetAccountId", "444"
                ))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void testErrorThrownForMissingAccount() {
        restClientTest.get()
                .uri("/v1/accounts/123")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo("account.not.found");
    }

    @Test
    void testErrorThrownForInsufficientBalanceWhileDebiting() {
        var givenAccount = accountService.save(new UserId(123), Currency.BDT);

        restClientTest.post()
                .uri("/v1/accounts/{id}/debit", givenAccount.id().value())
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"amount\": \"100\"}")
                .exchange()
                .expectStatus()
                .isEqualTo(422)
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo("account.balance.insufficient");
    }

    @Test
    void testErrorThrownForInvalidAccountsPassedInCurrencyExchange() {
        var account1 = accountService.save(new UserId(123), Currency.BDT);
        var account2 = accountService.save(new UserId(456), Currency.EUR);

        restClientTest.post()
                .uri("/v1/accounts/exchange")
                .body(Map.of(
                        "sourceAccountId", account1.id().value(),
                        "sourceAmountToConvert", "123",
                        "targetAccountId", account2.id().value()
                ))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo("transaction.conversion.currency.invalid");

    }

}