package com.avra.qa.common.util.datautil;

import com.fasterxml.uuid.Generators;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

@Component
public class RandomTestDataGenerator {

    public String generateString(String partialStringName) {
        AtomicReference<String> generatedString = new AtomicReference<>(partialStringName + " " + RandomStringUtils.randomAlphanumeric(50));
        return generatedString.get();
    }

    public String generateString() {
        AtomicReference<String> generatedString = new AtomicReference<>(RandomStringUtils.randomAlphanumeric(5));
        return generatedString.get();
    }

    public String generateString(int size) {
        AtomicReference<String> generatedString = new AtomicReference<>(RandomStringUtils.randomAlphanumeric(size));
        return generatedString.get();
    }

    public UUID generateUuid() {
        AtomicReference<UUID> generatedUuid = new AtomicReference<>(Generators.randomBasedGenerator().generate());
        return generatedUuid.get();
    }

    public Integer generateInteger() {
        return ThreadLocalRandom.current().nextInt(100000, 999999);
    }

    public Double generateDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    public LocalDateTime generateDatePlusMonth(int monthCount) {
        AtomicReference<LocalDateTime> localDateTime = new AtomicReference<>(LocalDateTime.parse(LocalDateTime.now().plusMonths(monthCount).format(ISO_LOCAL_DATE_TIME)));
        return localDateTime.get();
    }

    public String generateNumberAsString(int length) {
        AtomicReference<String> number = new AtomicReference<>(RandomStringUtils.random(length));
        return number.get();
    }

    public String generateRandomNumberAsString(long from, long to) {
        return String.valueOf(generateRandomNumber(from, to));
    }

    public long generateRandomNumber(long from, long to) {
        return ThreadLocalRandom.current().nextLong(from, to);
    }

    public LocalDateTime generateDate() {
        synchronized (this) {
            return LocalDateTime.parse(LocalDateTime.now().plusMonths(1).format(ISO_LOCAL_DATE_TIME));
        }
    }
}
