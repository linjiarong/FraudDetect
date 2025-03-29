package com.example.frauddetection.util;

import com.example.frauddetection.model.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

@SpringBootTest
@ActiveProfiles("test")
class RuleEngineTests {

    @Autowired
    private RuleEngine ruleEngine;

    @Test
    void testRuleEngineBeanInjection() {
        assertTrue(this.ruleEngine != null, "RuleEngine bean should be injected");
    }

    @Test
    void testEvaluateWithNormalCase() {
        Transaction transaction = Transaction.builder()
            .id("1")
            .amount(100) 
            .accountId("Normal Account") 
            .timestamp(new Date()) 
            .build();

        boolean isFraud = ruleEngine.evaluate(transaction);
        assertFalse(isFraud, "Normal transaction should not be detected as fraud");
        assertTrue(transaction.getFraudReason()==null, "Fraud reason should be empty for normal transaction");
    }

    @Test
    void testEvaluateWithHighAmount() {
        Transaction transaction = Transaction.builder()
            .id("2")
            .amount(10000)
            .accountId("Normal Account") 
            .timestamp(new Date()) 
            .build();

        boolean isFraud = ruleEngine.evaluate(transaction);
        assertTrue(isFraud, "Fraud should be detected for high transaction amount");
        assertEquals("High transaction amount", transaction.getFraudReason(), "Fraud reason should be 'High transaction amount'");

    }
    @Test
    void testEvaluateWithBadAccountId(){
        Transaction transaction =  Transaction.builder()
            .id("3")
            .amount(7000)
            .accountId("badAccount") 
            .timestamp(new Date()) 
            .build();

        boolean isFraud = ruleEngine.evaluate(transaction);
        assertTrue(isFraud, "Fraud should be detected for account with bad history");
        assertEquals("Suspicious Account", transaction.getFraudReason(), "Fraud reason should be 'Suspicious Account'");
    }
}