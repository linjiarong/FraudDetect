package com.example.frauddetection.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.example.frauddetection.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) 
@SpringBootTest
@ActiveProfiles("test")
@Log4j2
public class FraudDetectionServiceTest {

    @Autowired
    private FraudDetectionService fraudDetectionService;

    @Autowired
    private AmazonSQS amazonSQSClient;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${fraud-detection.transaction-queue}")
    private String transactionQueue;

    @Value("${fraud-detection.notify-queue}")
    private String notifyQueue;

    @Test
    void testFraudDetectionServiceBeanInjection() {
        assertTrue(this.fraudDetectionService != null, "FraudDetectionService bean should be injected");
    }

    @Test
    @Order(1) 
    void testDetectFraudulentTransaction() throws InterruptedException {
        try {
            publishTestTransaction();
            String transactionQueueUrl = amazonSQSClient.getQueueUrl(transactionQueue).getQueueUrl();
            String notifyQueueUrl = amazonSQSClient.getQueueUrl(notifyQueue).getQueueUrl();
            this.fraudDetectionService.detectFraudAndNotify(transactionQueueUrl, notifyQueueUrl);
            Thread.sleep(3000); // wait for seconds to let the thread finished
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    void teststartProcessingThread() {
            publishTestTransaction();
            this.fraudDetectionService.setRunning(true);
            this.fraudDetectionService.startProcessingThread();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.fraudDetectionService.stopRuning();

    }
    private void publishTestTransaction() {
        try {
            GetQueueUrlResult queueUrl = amazonSQSClient.getQueueUrl(transactionQueue);
            Transaction trans = Transaction.builder()
                .id("test1")
                .amount(10000) 
                .accountId("NormalAccount")
                .timestamp(new Date()) 
                .build();
            amazonSQSClient.sendMessage(queueUrl.getQueueUrl(), objectMapper.writeValueAsString(trans));
            
            trans = Transaction.builder()
                .id("test2")
                .amount(5000) 
                .accountId("badAccount")
                .timestamp(new Date()) 
                .build();
            amazonSQSClient.sendMessage(queueUrl.getQueueUrl(), objectMapper.writeValueAsString(trans));

            trans = Transaction.builder()
                .id("test3")
                .amount(5000) 
                .accountId("NormalAccount")
                .timestamp(new Date()) 
                .build();
            amazonSQSClient.sendMessage(queueUrl.getQueueUrl(), objectMapper.writeValueAsString(trans));

            amazonSQSClient.sendMessage(queueUrl.getQueueUrl(), "BAD_JSON_OBJECT");

        } catch (Exception e) {
            log.error("Queue Exception Message: {}", e.getMessage());
        }

    }

    
}