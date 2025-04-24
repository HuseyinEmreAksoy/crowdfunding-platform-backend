package com.crowdfund.campaignService.model.request;

import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class ContributeRequest {
    private BigInteger contractId;
    private BigInteger amount;

}