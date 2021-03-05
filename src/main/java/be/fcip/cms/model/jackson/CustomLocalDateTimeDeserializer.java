package be.fcip.cms.model.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class CustomLocalDateTimeDeserializer  extends StdDeserializer<LocalDateTime> {

    private static final long serialVersionUID = 1L;

    protected CustomLocalDateTimeDeserializer() {
        super(LocalDateTime.class);
    }


    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt)
            throws JsonProcessingException {
        try {
            Long dateNumber = jp.readValueAs(Long.class);
            if (dateNumber != null){
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(dateNumber),
                                TimeZone.getDefault().toZoneId());
            }
        } catch(IOException e){
            return null;
        }
        return null;
    }

}