package com.crowdfund.campaignService.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContributeResponse {
    private String campaignHash;
    private String status;
}
