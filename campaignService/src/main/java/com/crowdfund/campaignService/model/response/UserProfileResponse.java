package com.crowdfund.campaignService.model.response;

import lombok.Data;

@Data
public class UserProfileResponse {
    private Long id;
    private String username;
    private String ethereumAddress;
    private String privateKey;
}
