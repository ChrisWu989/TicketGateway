package com.synex.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Enables @Async processing so emails are sent in background threads.
 * Essentially means we can create more tickets while emails sent in background
 * means wont be blocked
 */
@Configuration
@EnableAsync
public class AsyncConfig {

}