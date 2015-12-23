package com.moc.smartmeterapp.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by philipp on 23.12.2015.
 */
public class DateConverter implements JsonDeserializer<Date>
{
    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    {
        String s = json.getAsJsonPrimitive().getAsString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate = null;
        try {
            convertedDate = sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertedDate;
    }
}