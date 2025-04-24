package com.crowdfund.campaignService.controller;


import com.crowdfund.campaignService.config.CurrentUserContext;
import com.crowdfund.campaignService.config.CurrentUserProvider;
import com.crowdfund.campaignService.config.UserClient;
import com.crowdfund.campaignService.model.request.ContributeRequest;
import com.crowdfund.campaignService.model.request.CreateCampaignRequest;
import com.crowdfund.campaignService.model.response.ContributeResponse;
import com.crowdfund.campaignService.model.response.CreateCampaignResponse;
import com.crowdfund.campaignService.model.response.UserProfileResponse;
import com.crowdfund.campaignService.service.CampaignService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;
    private final UserClient userClient;
    private final CurrentUserProvider currentUserProvider;
    private final CurrentUserContext currentUserContext;

    @PostMapping
    public ResponseEntity<?> createCampaign(@RequestBody CreateCampaignRequest request,
                                            HttpServletRequest servletRequest) {
        try {
            String authHeader = servletRequest.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Missing or invalid token");
            }

            String token = authHeader.substring(7);
            UserProfileResponse user = userClient.getCurrentUser(token);

            CreateCampaignResponse response = campaignService.createCampaign(
                    request,
                    user.getPrivateKey()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/contribute")
    public ResponseEntity<?> contributeCampaign(@RequestBody ContributeRequest request,
                                            HttpServletRequest servletRequest) {
        try {

            String privateKey = currentUserProvider.getCurrentPrivateKey();
            Long userId = currentUserProvider.getCurrentUserId();
            currentUserContext.setUserId(userId);
            ContributeResponse response = campaignService.contributeCampaign(
                    request,
                    privateKey
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}

