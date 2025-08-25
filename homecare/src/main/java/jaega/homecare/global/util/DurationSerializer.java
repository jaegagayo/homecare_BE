package jaega.homecare.global.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class DurationSerializer extends JsonSerializer<Integer> {

    @Override
    public void serialize(Integer value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        int hours = value / 3600;
        int minutes = (value % 3600) / 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append("h");
        if (minutes > 0) sb.append(minutes).append("m");
        if (sb.isEmpty()) sb.append("0m"); // 0초인 경우 처리

        gen.writeString(sb.toString());
    }
}
