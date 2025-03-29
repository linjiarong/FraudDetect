package com.example.frauddetection.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest()
// Test the transGenerateService with spring context, sending message to AWS SQS
class TransGenerateServiceWithSpringContextTests {

    
    @Autowired
    private TransGenerateService transGenerateService;

    @Test
    void testBeanInjection() {
        assertNotNull(transGenerateService);
    }   
    
    @Test
    void testPublishMessage() throws Exception {
        String id = "test-id";
        // Call the method
        transGenerateService.publishMessage(id);

    }
}


