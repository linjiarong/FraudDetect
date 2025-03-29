package com.example.frauddetection.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.frauddetection.service.TransGenerateService;

@RestController
@RequestMapping("/api")
public class TransGeneratorController {
    
    @Autowired
    private TransGenerateService transGenerateService;

    @RequestMapping("/generate")
    public String generate(@RequestParam(value = "count", defaultValue = "10") Integer count) {
        if (count <= 0) {
            return "Count must be greater than 0";
        }

        for (int i = 0; i < count; i++) {
            String id =System.currentTimeMillis() + "-" + i;
            this.transGenerateService.publishMessage(id);
        }

        return count + " messages generated successfully.";
    } 
}
