package com.example.frauddetection.service;

/**
 * Service interface for fraud detection operations.
 * 
 * This interface defines the core methods required for detecting fraudulent transactions,
 * processing messages from the queue, and managing the lifecycle of the fraud detection service.
 * 
 * Methods:
 * - `detectFraudAndNotify()`: Detects fraudulent transactions and sends notifications for flagged transactions.
 * - `startProcessingThread()`: Starts a background thread to continuously process messages from the queue.
 * - `processMessage(String queueUrl, com.amazonaws.services.sqs.model.Message message)`: Processes a single message from the specified SQS queue.
 * - `stopRuning()`: Stops the fraud detection service and any associated background threads.
 * - `setRunning(boolean running)`: Sets the running state of the service.
 * 
 * Example Usage:
 * - Implement this interface in a service class to provide the actual logic for fraud detection and message processing.
 * 
 * Notes:
 * - Ensure proper exception handling when implementing these methods to avoid disruptions in message processing.
 * - The `processMessage` method should handle invalid or unknown messages gracefully.
 * 
 * Author: linjiarong
 * Date: 2025-3-28
 */
public interface FraudDetectionService {
    /**
     * Detects fraudulent transactions and sends notifications for flagged transactions.
     * This method should be called in the background thread to continuously monitor the queue.
     */
    void detectFraudAndNotify();

    /**
     * Starts a background thread to continuously process messages from the queue.
     * This method should be called during application startup to initiate message processing.
     */
    void startProcessingThread();

    /**
     * Processes a single message from the specified SQS queue.
     * This method is responsible for deserializing the message, detecting fraud,
     * and sending notifications if necessary.
     *
     * @param queueUrl The URL of the SQS queue from which the message was received.
     * @param message   The SQS message to be processed.
     */
    void processMessage(String queueUrl, com.amazonaws.services.sqs.model.Message message);

    /**
     * Stops the fraud detection service and any associated background threads.
     * This method should be called during application shutdown to clean up resources.
     */
    void stopRuning();

    /**
     * Sets the running state of the service.
     * This method can be used to control the execution of background threads.
     *
     * @param running The new running state of the service.
     */
    void setRunning(boolean running);

}