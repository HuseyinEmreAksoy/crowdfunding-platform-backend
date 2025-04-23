package com.crowdfund.campaignService.messager;

import com.crowdfund.campaignService.entity.Campaign;
import com.crowdfund.campaignService.model.CampaignCreatedEventPayload;
import com.crowdfund.campaignService.repository.CampaignRepository;
import com.crowdfund.campaignService.states.CampaignStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Component
public class ContractEventConsumer {
    private final CampaignRepository campaignRepository;

    public ContractEventConsumer(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    @RabbitListener(queues = "campaign.created")
    public void handleCampaignCreated(CampaignCreatedEventPayload payload) {
        if (campaignRepository.existsById(payload.getCampaignId())) {
            System.out.println("Campaign already exists: " + payload.getCampaignId());
            return;
        }
        System.out.println("Received targetAmount: " + payload.getTargetAmount());

        Campaign campaign = Campaign.builder()
                .ownerAddress(payload.getOwner())
                .targetAmount(payload.getTargetAmount())
                .raisedAmount(BigInteger.ZERO)
                .status(CampaignStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        campaignRepository.save(campaign);
    }

}
