package com.crowdfund.campaignService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampaignCreatedEventPayload {
    private Long campaignId;
    private String owner;
    private BigInteger targetAmount;
}
