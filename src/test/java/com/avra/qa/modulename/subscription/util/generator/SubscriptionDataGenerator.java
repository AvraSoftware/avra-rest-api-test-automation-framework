package com.avra.qa.modulename.subscription.util.generator;

import com.avra.qa.common.util.datautil.RandomTestDataGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubscriptionDataGenerator {

    @Autowired
    private RandomTestDataGenerator generator;

    public UUID generateSubscriptionId() {
        return generator.generateUuid();
    }
}
