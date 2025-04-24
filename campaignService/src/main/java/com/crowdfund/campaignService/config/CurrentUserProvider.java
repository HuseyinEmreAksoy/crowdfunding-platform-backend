package com.crowdfund.campaignService.config;

import com.crowdfund.campaignService.model.response.UserProfileResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final UserClient userClient;

    @Autowired
    private HttpServletRequest request;

    public UserProfileResponse getCurrentUser() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        return userClient.getCurrentUser(token);
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public String getCurrentUserAddress() {
        return getCurrentUser().getEthereumAddress();
    }

    public String getCurrentPrivateKey() {
        return getCurrentUser().getPrivateKey();
    }
}
