package com.example.frauddetection.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.example.frauddetection.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.jupiter.api.Test;

class TransGenerateServiceTests {

    private TransGenerateService transGenerateService;
    private AmazonSQS amazonSQSClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        amazonSQSClient = mock(AmazonSQS.class);
        objectMapper = new ObjectMapper();
        transGenerateService = new TransGenerateService();
    }
    
    @Test
    void testGenerateRandomTransaction() {
        String id = "test-id";
        Transaction transaction = transGenerateService.generateRandomTransaction(id);

        assertNotNull(transaction);
        assertEquals(id, transaction.getId());
        assertTrue(transaction.getAmount() >= 100 && transaction.getAmount() <= 10000);
        assertNotNull(transaction.getAccountId());
        assertEquals(10, transaction.getAccountId().length());
        assertNotNull(transaction.getTimestamp());
        assertTrue(transaction.getTimestamp().before(new Date()) || transaction.getTimestamp().equals(new Date()));
    }

}