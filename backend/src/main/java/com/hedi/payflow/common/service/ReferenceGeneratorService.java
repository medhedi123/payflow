package com.hedi.payflow.common.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReferenceGeneratorService {

    private final AtomicLong counter = new AtomicLong(1);

    public String generate(String prefix) {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        long number = counter.getAndIncrement();

        return String.format("%s-%s-%06d", prefix, date, number);
    }
}