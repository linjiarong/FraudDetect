package com.example.frauddetection.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private String id;
    private double amount;
    private String accountId;
    private Date timestamp;    
}