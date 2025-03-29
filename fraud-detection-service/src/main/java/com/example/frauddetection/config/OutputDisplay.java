package com.example.frauddetection.config;

import java.time.LocalDate;

/**
 * Utility class for displaying output in the Drools engine.
 * 
 * This class is used to test and demonstrate the use of global variables in the Drools rule engine.
 * It provides a method to display text along with the current date in a formatted manner.
 * 
 * Features:
 * - Prints a formatted message to the console.
 * - Includes the current date for context.
 * 
 * Methods:
 * - `showText(String text)`: Displays the provided text along with the current date in a formatted output.
 * 
 * Example Usage:
 * - Use this class as a global variable in the Drools engine to display messages during rule execution.
 * 
 * Notes:
 * - This class is primarily intended for testing and debugging purposes.
 * - Ensure that the Drools configuration properly sets this class as a global variable if used in rules.
 * 
 * Author: linjiarong
 * Date: 2025-3-28
 */
public class OutputDisplay {

    public void showText(String text) {
        System.out.println("==================================================");
        System.out.println("Text: " + text + ",  date: " + LocalDate.now());
        System.out.println("==================================================");
    }

}
