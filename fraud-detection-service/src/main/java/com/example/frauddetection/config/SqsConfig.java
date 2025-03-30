package com.example.frauddetection.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.WebIdentityTokenCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.log4j.Log4j2;

/**
 * Configuration class for setting up Amazon SQS in the application.
 * 
 * This class initializes and configures the AmazonSQS client, which is used to interact with AWS Simple Queue Service (SQS).
 * 
 * Features:
 * - Reads AWS credentials (access key, secret key) and region from the application properties.
 * - Configures an `AmazonSQS` bean for dependency injection throughout the application.
 * 
 * Properties:
 * - `aws.accessKey`: The AWS access key for authentication.
 * - `aws.secretKey`: The AWS secret key for authentication.
 * - `aws.region`: The AWS region where the SQS queues are located (default: `ap_east_1`).
 * 
 * Beans:
 * - `AmazonSQS`: Configured SQS client for sending and receiving messages from SQS queues.
 * 
 * Dependencies:
 * - AWS SDK for Java for interacting with SQS.
 * - Spring Framework for dependency injection and configuration.
 * 
 * Example Usage:
 * - Inject the `AmazonSQS` bean into your service or component to send and receive messages from SQS queues.
 * 
 * Notes:
 * - Ensure that the AWS credentials and region are correctly configured in the `application.yml` or environment variables.
 * - Avoid hardcoding sensitive credentials in the source code; use secure methods to manage them (e.g., AWS Secrets Manager or environment variables).
 * 
 * Author: linjiarong
 * Date: 2025-3-28
 */
@Configuration
@Log4j2
public class SqsConfig {

    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.region:ap_east_1}")
    private String region;

    /**
     * Creates an AmazonSQS client bean for interacting with AWS SQS.
     * 
     * @return AmazonSQS client configured with the specified credentials and region.
     */
    @Bean
    public AmazonSQS amazonSQSClient() {
        // Check if accessKey is null or empty
        // If it is, create the client without credentials
        // Otherwise, create the client with the provided credentials
        // This allows for flexibility in using either default credentials or specified ones
        // based on the environment (e.g., local development vs. production)
        if (accessKey == null || accessKey.isEmpty()) {
                log.info("Build client with IRSA, region {}", region);
                return AmazonSQSClientBuilder.standard()
                .withCredentials(WebIdentityTokenCredentialsProvider.create())
                .withRegion(region) 
                .build();
        }else{
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            return AmazonSQSClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(region)
                    .build();
        }
    }

}