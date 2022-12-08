package com.avra.qa.common.util.datautil.rest;

import com.fasterxml.uuid.Generators;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

@Component
public class RestRandomTestDataGenerator {

    public String generateString(String partialStringName) {
        synchronized (this) {
            return partialStringName + " " + RandomStringUtils.randomAlphanumeric(50);
        }
    }

    public String generateString() {
        return generateString(5);
    }

    public String generateString(int size) {
        synchronized (this) {
            return RandomStringUtils.randomAlphanumeric(size);
        }
    }

    public String generatePostalCode() {
        return generateNumber(2) + "-" + generateNumber(3);
    }

    public UUID generateUuid() {
        synchronized (this) {
            return Generators.randomBasedGenerator().generate();
        }
    }

    public Integer generateInteger() {
        synchronized (this) {
            Random rand = new Random();
            return rand.nextInt(100000);
        }
    }

    public Double generateDouble() {
        synchronized (this) {
            Random rand = new Random();
            return rand.nextDouble();
        }
    }

    public LocalDateTime generateDate() {
        synchronized (this) {
            return LocalDateTime.parse(LocalDateTime.now().plusMonths(1).format(ISO_LOCAL_DATE_TIME));
        }
    }

    public String generateNumber(int length) {
        synchronized (this) {
            return RandomStringUtils.randomNumeric(length);
        }
    }
}
