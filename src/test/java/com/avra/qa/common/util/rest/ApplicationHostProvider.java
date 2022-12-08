package com.avra.qa.common.util.rest;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;

@Component
public class ApplicationHostProvider {

    @Value("${applicationUrls}")
    public String[] applicationUrls;

    private Random random = new Random();

    public ApplicationHost chooseOneApplicationHost() {
        if (ArrayUtils.isEmpty(applicationUrls)) throw new RuntimeException("empty application urls");
        String applicationHostUrl = applicationUrls[random.nextInt(applicationUrls.length)];
        return buildApplicationHostFromUrl(applicationHostUrl);
    }

    public ApplicationHost[] provideAllApplicationHosts() {
        return Arrays.stream(applicationUrls).map(applicationUrl -> buildApplicationHostFromUrl(applicationUrl)).toArray(ApplicationHost[]::new);
    }

    private ApplicationHost buildApplicationHostFromUrl(String applicationHostUrl) {
        try {
            URL url = new URL(applicationHostUrl);
            return ApplicationHost.of(
                    String.format("%s://%s", url.getProtocol(), url.getHost()),
                    url.getPort()
            );
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            throw new RuntimeException(String.format("url can not be parsed %s", ex.getMessage()));
        }
    }

    @lombok.Value(staticConstructor = "of")
    public static class ApplicationHost {
        String host;
        int port;
    }
}
