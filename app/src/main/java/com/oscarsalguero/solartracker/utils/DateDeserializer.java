package com.oscarsalguero.solartracker.utils;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.oscarsalguero.solartracker.Constants;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Custom deserializer to handle UTC dates received from the backend API.
 * Created by RacZo on 8/6/15.
 */
public class DateDeserializer implements JsonDeserializer<Date> {

    private static final String LOG_TAG = DateDeserializer.class.getName();

    @Override
    public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
        //String date = element.getAsString().replace("T", " ");
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.API_DATE_FORMAT);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return formatter.parse(element.getAsString());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to parse Date due to: " + e.getMessage());
            return null;
        }
    }
}