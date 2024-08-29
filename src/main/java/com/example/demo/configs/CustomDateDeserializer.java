package com.example.demo.configs;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.Date;

public class CustomDateDeserializer extends JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        int[] dateValues = jsonParser.readValueAs(int[].class);
        int year = dateValues[0];
        int month = dateValues[1] - 1; // Month is zero-based in Java Date
        int day = dateValues[2];
        int hour = dateValues[3];
        int minute = dateValues[4];
        int second = dateValues[5];

        return new Date(year, month, day, hour, minute, second);
    }
}
