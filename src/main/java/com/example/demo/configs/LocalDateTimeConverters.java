package com.example.demo.configs;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class LocalDateTimeConverters {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final DateTimeFormatter FORMATTER_2 = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");

    @ReadingConverter
    public static class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

        @Override
        public LocalDateTime convert(String source) {
        	
        	if (source.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$")) {
                // If the source matches the first format, parse using the first formatter
                return LocalDateTime.parse(source, FORMATTER);
            } else if (source.matches("^\\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{2}:\\d{2} [AP]M$")) {
                // If the source matches the second format, parse using the second formatter
                return LocalDateTime.parse(source, FORMATTER_2);
            } else {
                // If the source doesn't match any of the formats, throw an exception
                throw new IllegalArgumentException("Unsupported date format: " + source);
            }
        	
        //	log.info("convert String Date :: "+source);
         //   return LocalDateTime.parse(source, FORMATTER);
        }
    }

    @WritingConverter
    public static class LocalDateTimeToStringConverter implements Converter<LocalDateTime, String> {

        @Override
        public String convert(LocalDateTime source) {
            return source.format(FORMATTER);
        }
    }
}

