package com.example.frauddetection.util;

import com.example.frauddetection.model.Transaction;

/**
 * RuleEngine interface for evaluating transactions against fraud detection rules.
 * 
 * This interface defines the contract for implementing a rule engine that evaluates 
 * financial transactions to determine if they are fraudulent based on predefined rules.
 * 
 * Methods:
 * - `evaluate(Transaction transaction)`: Evaluates a transaction and returns `true` if it is flagged as fraudulent.
 * 
 * Example Usage:
 * - Implement this interface in a class (e.g., `RuleEngineDrools`) to provide the logic for rule evaluation.
 * 
 * Notes:
 * - Ensure that the implementation of this interface handles transactions efficiently and accurately.
 * - The implementation should be extensible to accommodate new rules as needed.
 * 
 * Author: linjiarong
 * Date: 2025-3-28
 */
public interface RuleEngine {
    /**
     * Evaluates a transaction to determine if it is fraudulent.
     *
     * @param transaction the transaction to evaluate
     * @return true if the transaction is fraudulent, false otherwise
     */
    boolean evaluate(Transaction transaction);

}