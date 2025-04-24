package com.crowdfund.campaignService.config;

import org.springframework.stereotype.Component;

@Component
public class CurrentUserContext {

    private static final ThreadLocal<Long> currentUserId = ThreadLocal.withInitial(() -> 0L);

    public void setUserId(Long userId) {
        currentUserId.set(userId);
    }

    public Long getUserId() {
        return currentUserId.get();
    }

    public void clear() {
        currentUserId.remove();
    }
}
