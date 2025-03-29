package com.example.frauddetection.util;

import com.example.frauddetection.model.Transaction;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RuleEngine implementation using Drools.
 * 
 * This class is responsible for evaluating transactions against the rules defined in the Drools rule engine.
 * It uses a `KieSession` to insert transactions and fire rules, determining whether a transaction is fraudulent.
 * 
 * Features:
 * - Integrates with the Drools rule engine for rule-based evaluation.
 * - Evaluates transactions by inserting them into the `KieSession` and firing all applicable rules.
 * - Returns a boolean indicating whether any rules were triggered, marking the transaction as fraudulent if true.
 * 
 * Dependencies:
 * - `KieSession`: The Drools session used for rule evaluation.
 * - `Transaction`: The model class representing a financial transaction.
 * 
 * Methods:
 * - `evaluate(Transaction transaction)`: Evaluates a transaction and returns `true` if it is flagged as fraudulent.
 * 
 * Example Usage:
 * ```java
 * Transaction transaction = new Transaction("txn123", 1000.50, "acc456", new Date(), null);
 * boolean isFraudulent = ruleEngineDrools.evaluate(transaction);
 * System.out.println("Is fraudulent: " + isFraudulent);
 * ```
 * 
 * Notes:
 * - Ensure that the Drools `.drl` files are correctly configured and loaded into the `KieSession`.
 * - The `KieSession` should be properly managed to avoid memory leaks (e.g., dispose of the session when no longer needed).
 * 
 * Author: linjiarong
 * Date: 2025-3-28
 */
@Component
public class RuleEngineDrools implements RuleEngine {

    private final KieSession kieSession;
    /**
     * Constructor for RuleEngineDrools
     * @param kieSession the KieSession used for rule evaluation
     */
    @Autowired
    public RuleEngineDrools(KieSession kieSession) {
        this.kieSession = kieSession;
    }

    /**
     * Evaluates a transaction to determine if it is fraudulent.
     *
     * @param transaction the transaction to evaluate
     * @return true if the transaction is fraudulent, false otherwise
     */
    @Override
    public boolean evaluate(Transaction transaction) {
        kieSession.insert(transaction);
        int firedRules = kieSession.fireAllRules();
        return firedRules > 0; // if any rule is fired, then it is a fraudulent transaction
    }
}