package org.example.accountservice.application;

import org.example.accountservice.BaseIntegrationTest;
import org.example.accountservice.domain.Account;
import org.example.accountservice.domain.Currency;
import org.example.accountservice.domain.UserId;
import org.example.accountservice.domain.command.CreditAccountCommand;
import org.example.accountservice.domain.command.DebitAccountCommand;
import org.example.accountservice.domain.exception.InsufficientBalanceError;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransferServiceTest extends BaseIntegrationTest {
    @Autowired
    TransferService underTest;

    @Autowired
    AccountService accountService;

    @ParameterizedTest
    @MethodSource("arguments")
    void concurrentDebitRequestsShouldNotResultInOverDraft(BigDecimal debitAmount, int expectedSuccessCount, int expectedFailureCount, BigDecimal expectedCurrentAmount) throws Exception {
        var account = setupAccount(new UserId(123), new BigDecimal("100"), Currency.EUR);
        var threadCount = 10;
        try(ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            CountDownLatch readyLatch = new CountDownLatch(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch finishedLatch = new CountDownLatch(threadCount);
            AtomicInteger failureCount = new AtomicInteger();
            AtomicInteger successCount = new AtomicInteger();

            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    readyLatch.countDown(); // signals when a thread is blocking behind startLatch
                    try {
                        startLatch.await(); // all threads block here until startLatch is set to 0
                        underTest.debit(new DebitAccountCommand(account.id(), debitAmount));
                        successCount.incrementAndGet();
                    } catch (InsufficientBalanceError e) {
                        failureCount.incrementAndGet();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        finishedLatch.countDown(); // block main test runner thread until all executor threads finish
                    }
                });
            }
            readyLatch.await(); // wait until readyLatch is 0
            startLatch.countDown(); // trigger all threads
            finishedLatch.await(); // wait until all threads are finished

            assertEquals(expectedSuccessCount, successCount.get());
            assertEquals(expectedFailureCount, failureCount.get());
            assertEquals(0, accountService.findById(account.id()).currentAmount().compareTo(expectedCurrentAmount));
        }
    }

    private Account setupAccount(UserId userId, BigDecimal amount, Currency currency) {
        var account = accountService.save(userId, currency);
        underTest.credit(new CreditAccountCommand(account.id(), amount));
        return account;
    }

    static Arguments[] arguments() {
        return new Arguments[] {
                Arguments.of(new BigDecimal("30"), 3, 7, new BigDecimal("10")),
                Arguments.of(new BigDecimal("100"), 1, 9, new BigDecimal("0")),
                Arguments.of(new BigDecimal("150"), 0, 10, new BigDecimal("100")),

        };
    }

}