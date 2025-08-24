package jaega.homecare.global.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DurationDeserializer extends JsonDeserializer<Integer> {
    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String durationStr = p.getText().trim();
        int hours = 0;
        int minutes = 0;

        if (durationStr.contains("h")) {
            String[] parts = durationStr.split("h");
            hours = Integer.parseInt(parts[0].trim());
            if (parts.length > 1 && parts[1].contains("m")) {
                minutes = Integer.parseInt(parts[1].replace("m", "").trim());
            }
        } else if (durationStr.contains("m")) {
            minutes = Integer.parseInt(durationStr.replace("m", "").trim());
        }

        return hours * 3600 + minutes * 60;
    }
}
