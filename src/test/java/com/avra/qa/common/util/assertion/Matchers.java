package com.avra.qa.common.util.assertion;

public class Matchers {

    public static IsLocalDateTimeMatcher isLocalDateTime() {
        return new IsLocalDateTimeMatcher();
    }
}
