package com.crowdfund.campaignService.model.request;

import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class CreateCampaignRequest {
    private String title;
    private String description;
    private BigInteger targetAmount;
    private LocalDateTime deadline;
}