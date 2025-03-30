package com.example.frauddetection.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.example.frauddetection.model.Transaction;
import com.example.frauddetection.util.RuleEngine;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * FraudDetectionServiceImpl is responsible for detecting fraudulent transactions
 * and notifying the appropriate queue.
 */
@Service
@Log4j2
public class FraudDetectionServiceImpl implements FraudDetectionService {
    // RuleEngine is a custom class that evaluates the transaction based on defined rules
    private final RuleEngine ruleEngine;

    // The name of the queue to send notifications
    @Value("${fraud-detection.notify-queue}")
    private String notifyQueue;

    // The name of the queue to receive transactions
    @Value("${fraud-detection.transaction-queue}")
    private String transactionQueue;

    // The Amazon SQS client used to interact with AWS SQS
    private final AmazonSQS amazonSQSClient;

    // The ObjectMapper used for JSON serialization/deserialization
    private final ObjectMapper objectMapper; 

    // The batch size for receiving messages from the queue
    @Value("${fraud-detection.batch-size:5}")
    private Integer batchSize;

    // The wait time for receiving messages from the queue
    @Value("${fraud-detection.wait-time-seconds:2}")
    private Integer waitTimeSeconds;

    // The pool size for the thread pool used to process messages
    @Value("${fraud-detection.pool-size:1}")
    private Integer poolSize;

    // The executor service used for processing messages in parallel
    // This allows multiple messages to be processed concurrently
    // and improves the performance of the service
    private final ExecutorService executorService;
    
    // Flag to control whether the service is running
    // This can be used to stop the service gracefully
    // when the application is shutting down
    // The default value is true, meaning the service is running
    @Value("${fraud-detection.enabled:true}")       
    private boolean isRunning = true;

    /**
     * Constructor for FraudDetectionServiceImpl.
     *
     * @param ruleEngine       The RuleEngine instance used for fraud detection.
     * @param amazonSQSClient  The AmazonSQS client used to interact with AWS SQS.
     * @param objectMapper     The ObjectMapper used for JSON serialization/deserialization.
     * @param poolSize         The size of the thread pool for processing messages.
     */
    @Autowired
    public FraudDetectionServiceImpl(RuleEngine ruleEngine, AmazonSQS amazonSQSClient, ObjectMapper objectMapper, @Value("${fraud-detection.pool-size:1}") Integer poolSize) {
        this.ruleEngine = ruleEngine;
        this.amazonSQSClient = amazonSQSClient;
        this.objectMapper = objectMapper;
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    /**
     * Detects fraud for the given transaction.
     *
     * @param transaction The transaction to be evaluated.
     * @return true if fraud is detected, false otherwise.
     */
    private boolean detectFraud(Transaction transaction) {
        return ruleEngine.evaluate(transaction);
    }

    /**
     * Starts a background thread to continuously process messages from the queue.
     * This method is called during application startup to initiate message processing.
     */
    @PostConstruct
    public void startProcessingThread() {
        if(isRunning) {
            String transactionQueueUrl = amazonSQSClient.getQueueUrl(transactionQueue).getQueueUrl();
            String notifyQueueUrl = amazonSQSClient.getQueueUrl(notifyQueue).getQueueUrl();

            new Thread(() -> {
                while (isRunning) {
                    try {
                        detectFraudAndNotify(transactionQueueUrl, notifyQueueUrl);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        log.error("Thread interrupted: {}", e.getMessage());
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();
        }
    }

    /**
     * Detects fraudulent transactions and sends notifications for flagged transactions.
     * This method is called in the background thread to continuously monitor the queue.
     */
    @Override
    public void detectFraudAndNotify(String transactionQueueUrl, String notifyQueueUrl) {
        
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest()
                .withQueueUrl(transactionQueueUrl)
                .withMaxNumberOfMessages(10) 
                .withWaitTimeSeconds(5); 
        ReceiveMessageResult receiveMessageResult = amazonSQSClient.receiveMessage(receiveMessageRequest);
        log.info("Received {} messages from queue", receiveMessageResult.getMessages().size());
        if (!receiveMessageResult.getMessages().isEmpty()) {
            for (com.amazonaws.services.sqs.model.Message message : receiveMessageResult.getMessages()) {
                executorService.submit(() -> processMessage(transactionQueueUrl, notifyQueueUrl,  message));
            }
        }
    }

    /**
     * Processes a single message from the specified SQS queue.
     * This method is responsible for deserializing the message, detecting fraud,
     * and sending notifications if necessary.
     *
     * @param queueUrl The URL of the SQS queue from which the message was received.
     * @param message   The SQS message to be processed.
     */
    @Override
    public void processMessage(String transactionQueueUrl, String notifyQueueUrl, com.amazonaws.services.sqs.model.Message message) {
        try {
            log.debug("Read Message from queue: {}", message.getBody());

            // Deserialize the message body to Transaction object
            Transaction transaction = objectMapper.readValue(message.getBody(), Transaction.class);

            boolean isFraud = detectFraud(transaction);
            log.info("Transaction ID: {}, Fraud Detected: {}", transaction.getId(), isFraud);

            if (isFraud) {
                // Send the message to the notify queue 
                amazonSQSClient.sendMessage(notifyQueueUrl, objectMapper.writeValueAsString(transaction));
                log.info("Fraud detected. Notification sent to queue: {}", notifyQueue);
            } else {
                log.info("No fraud detected for transaction ID: {}", transaction.getId());
            }

            // Delete the message from the queue after processing
            amazonSQSClient.deleteMessage(transactionQueueUrl, message.getReceiptHandle());
        } catch (Exception e) {
            // Handle JSON parsing error or other exceptions
            // Optionally, you can log the error or send it to a dead-letter queue
            // For now, just log the error and delete the message
            log.error("Error processing message: {}", message.getBody());
            log.error("Error: {}", e.getMessage());
            amazonSQSClient.deleteMessage(transactionQueueUrl, message.getReceiptHandle());
    
        }
    }

    /**
     * Stops the fraud detection service and any associated background threads.
     * This method is called during application shutdown to clean up resources.
     */
    @Override
    @PreDestroy
    public void stopRuning() {
        log.info("Shutting down FraudDetectionService...");
        isRunning = false;
        
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                log.warn("Executor did not terminate in the specified time.");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Thread interrupted while waiting for termination: {}", e.getMessage());
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("FraudDetectionService shutdown complete.");
    }
    /**
     * Sets the running state of the service.
     * This method can be used to control the execution of background threads.
     *
     * @param running The new running state of the service.
     */
    @Override
    public void setRunning(boolean running) {
        this.isRunning = running;
    }
}