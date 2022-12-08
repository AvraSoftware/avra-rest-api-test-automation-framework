package com.avra.qa.common.util.assertion;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class IsLocalDateTimeMatcher extends TypeSafeMatcher<Object> {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    protected boolean matchesSafely(Object dateCandidate) {
        Objects.requireNonNull(dateCandidate);
        if (dateCandidate instanceof LocalDateTime) {
            return true;
        }
        if (dateCandidate instanceof String) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
            try {
                String dateString = (String) dateCandidate;
                if (dateString.contains(".")) {
                    dateString = dateString.substring(0, dateString.lastIndexOf("."));
                }
                LocalDateTime.parse(dateString, formatter);
            } catch (DateTimeParseException ex) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("date as string does not match " + DATE_TIME_FORMAT);
    }
}
