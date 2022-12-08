package com.avra.qa.modulename.subscription.assertion;

import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

@UtilityClass
public class ProductAssert {

    private static void assertProduct(Map<String, String> product, UUID subscriptionId, Integer id, String productId, boolean visible) {

        assertEquals(product.size(), 5);
        assertEquals(product.get("id"), id.toString());
        assertEquals(product.get("subscription_id"), subscriptionId.toString());
        assertEquals(product.get("product_id"), productId);
        assertEquals(product.get("visible"), Boolean.toString(visible));
        assertEquals(product.get("type_limit"), "1");
    }
}
