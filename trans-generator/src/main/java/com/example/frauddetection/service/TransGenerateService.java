package com.example.frauddetection.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.frauddetection.model.Transaction;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;

@Service
@Log4j2
public class TransGenerateService {

    @Value("${queue.name:test-queue}")
    private String queueName;

    @Autowired
    private AmazonSQS amazonSQSClient;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${trans-generator.enabled:true}")       
    private boolean isRunning = true;

    public TransGenerateService() {
    }

    public TransGenerateService(AmazonSQS amazonSQSClient, ObjectMapper objectMapper) {
        this.amazonSQSClient = amazonSQSClient;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void startThread() {
        if(isRunning) {
            new Thread(() -> {
                int i = 0;
                while (isRunning) {
                    for (int j = 0; j < 100; j++) {
                        String id =System.currentTimeMillis() + "-" + (i++);
                        publishMessage(id);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                }
            }).start();
        }
    }

    public void publishMessage(String id) {
        try {
            log.info("Get queue name: {}", queueName);
            GetQueueUrlResult queueUrl = amazonSQSClient.getQueueUrl(queueName);
            log.info("QueueUrl: {}", queueUrl.getQueueUrl());
            var message =generateRandomTransaction(id);
                    
            var result = amazonSQSClient.sendMessage(queueUrl.getQueueUrl(), objectMapper.writeValueAsString(message));
            log.info("Message sent with id: {}", result.getMessageId());
        } catch (Exception e) {
            log.error("Queue Exception Message: {}", e.getMessage());
        }
    }
    public Transaction generateRandomTransaction(String id) {
        return Transaction.builder()
                .id(id)
                .amount(1 + (int)(Math.random() * 10000)) 
                .accountId(Math.random() > 0.8 ? "bad" + RandomStringUtils.randomAlphabetic(7) : RandomStringUtils.randomAlphabetic(10)) 
                .timestamp(new Date()) 
                .build();
    }
}


