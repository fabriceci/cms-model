package be.fcip.cms.model.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class CustomLocalDateTimeSerializer extends StdSerializer<LocalDateTime> {

    public CustomLocalDateTimeSerializer(){
        this(null);
    }

    public CustomLocalDateTimeSerializer(Class t){
        super(t);
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        Long number = null;
        if(value != null) {
            number = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }

        gen.writeNumber(number);
    }
}
