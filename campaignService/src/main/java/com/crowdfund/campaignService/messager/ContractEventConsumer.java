package com.crowdfund.campaignService.messager;

import com.crowdfund.campaignService.config.CurrentUserContext;
import com.crowdfund.campaignService.config.CurrentUserProvider;
import com.crowdfund.campaignService.entity.Campaign;
import com.crowdfund.campaignService.entity.Contribution;
import com.crowdfund.campaignService.model.CampaignContributeEventPayload;
import com.crowdfund.campaignService.model.CampaignCreatedEventPayload;
import com.crowdfund.campaignService.repository.CampaignRepository;
import com.crowdfund.campaignService.repository.ContributionRepository;
import com.crowdfund.campaignService.states.CampaignStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Component
public class ContractEventConsumer {
    private final CampaignRepository campaignRepository;
    private final ContributionRepository contributionRepository;
    private final CurrentUserContext currentUserContext;

    public ContractEventConsumer(CampaignRepository campaignRepository, ContributionRepository contributionRepository, CurrentUserContext currentUserContext) {
        this.campaignRepository = campaignRepository;
        this.contributionRepository = contributionRepository;
        this.currentUserContext = currentUserContext;
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

    @RabbitListener(queues = "contribution.made")
    public void handleCampaignContribute(CampaignContributeEventPayload payload) {

        campaignRepository.findById(payload.getCampaignId()).ifPresent(campaign -> {
            Contribution contribution = Contribution.builder()
                    .campaign(campaign)
                    .userId(currentUserContext.getUserId())
                    .contributorAddress(payload.getContributor())
                    .amount(payload.getAmount())
                    .timestamp(LocalDateTime.now())
                    .build();

            contributionRepository.save(contribution);

            campaign.setRaisedAmount(campaign.getRaisedAmount().add(payload.getAmount()));
            campaignRepository.save(campaign);

            System.out.println("Contribution saved to DB: " + contribution.getAmount());
        });
    }

}
