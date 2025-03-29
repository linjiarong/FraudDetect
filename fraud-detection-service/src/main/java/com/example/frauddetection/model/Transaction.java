package com.example.frauddetection.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
/**
 * Model class representing a financial transaction.
 * 
 * This class is used to encapsulate the details of a transaction, including its ID, amount, 
 * associated account, timestamp, and any fraud-related information.
 * 
 * Features:
 * - Uses Lombok annotations for boilerplate code reduction (e.g., getters, setters, constructors, and builder pattern).
 * - Includes a `fraudReason` field to store the reason if the transaction is flagged as fraudulent.
 * 
 * Fields:
 * - `id` (String): Unique identifier for the transaction.
 * - `amount` (double): The monetary amount of the transaction.
 * - `accountId` (String): The ID of the account associated with the transaction.
 * - `timestamp` (Date): The date and time when the transaction occurred.
 * - `fraudReason` (String): The reason why the transaction was flagged as fraudulent (if applicable).
 * 
 * Annotations:
 * - `@Builder`: Enables the builder pattern for creating instances of the class.
 * - `@Data`: Generates getters, setters, `toString`, `equals`, and `hashCode` methods.
 * - `@NoArgsConstructor`: Generates a no-argument constructor.
 * - `@AllArgsConstructor`: Generates a constructor with all fields as arguments.
 * 
 * Example Usage:
 * ```java
 * Transaction transaction = Transaction.builder()
 *     .id("txn123")
 *     .amount(1000.50)
 *     .accountId("acc456")
 *     .timestamp(new Date())
 *     .fraudReason("Suspicious activity detected")
 *     .build();
 * ```
 * 
 * Author: linjiarong
 * Date: 2025-3-28
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private String id;
    private double amount;
    private String accountId;
    private Date timestamp;
    private String fraudReason;
}