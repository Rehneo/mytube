package com.rehneo.moderationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.jta.JtaTransactionManager;


@Configuration
public class TransactionConfiguration {
    @Bean
    public JtaTransactionManager transactionManager() {
        return new JtaTransactionManager();
    }
}