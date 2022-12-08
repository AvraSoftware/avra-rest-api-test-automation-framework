package com.avra.qa.modulename.subscription.util.jpa;

import com.avra.qa.common.util.datautil.postgres.QueryHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepository {

    private static final String DELETE_SUBSCRIPTION = "DELETE FROM schema_name.subscription;";

    private final QueryHelper queryHelper;

    public void deleteAllSubscriptions() {
        queryHelper.executeUpdate(DELETE_SUBSCRIPTION);
    }
}
