package com.avra.qa.modulename.subscription.util.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionDatabaseService {

    private final SubscriptionRepository subscriptionRepository;

    public void deleteAllSubscriptions() {
        subscriptionRepository.deleteAllSubscriptions();
    }
}
