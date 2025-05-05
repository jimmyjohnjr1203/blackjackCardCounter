package client;

import com.google.gson.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonUtil {
    
    public static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            public LocalDateTime deserialize(JsonElement json, java.lang.reflect.Type type,
                                             JsonDeserializationContext context) throws JsonParseException {
                return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        })
        .create();
}
