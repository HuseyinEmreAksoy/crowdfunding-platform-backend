package com.crowdfund.campaignService.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue campaignCreatedQueue() {
        return new Queue("campaign.created", false, false, true);
    }

    @Bean
    public Queue contributeQueue() {
        return new Queue("contribution.made", false, false, true);
    }
}
