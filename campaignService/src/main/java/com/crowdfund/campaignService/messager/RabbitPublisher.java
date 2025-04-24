package com.crowdfund.campaignService.messager;

import com.crowdfund.campaignService.model.CampaignContributeEventPayload;
import com.crowdfund.campaignService.model.CampaignCreatedEventPayload;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitPublisher {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.campaign-created}")
    private String campaignQueue;

    @Value("${rabbitmq.queue.contribution-made}")
    private String contributeQueue;

    public RabbitPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCampaignCreated(CampaignCreatedEventPayload payload) {
        rabbitTemplate.convertAndSend(campaignQueue, payload);
    }

    public void publishCampaignContributed(CampaignContributeEventPayload payload) {
        rabbitTemplate.convertAndSend(contributeQueue, payload);
    }
}
