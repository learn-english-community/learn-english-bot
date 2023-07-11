package bot.entity.word;

import com.google.gson.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class CachedWordDeserializer implements JsonDeserializer<CachedWord> {
    @Override
    public CachedWord deserialize(
            JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        CachedWord word = new CachedWord();

        for (Class<?> clazz = CachedWord.class; clazz != null; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                String fieldName = field.getName();
                JsonElement element = jsonObject.get(fieldName);

                if (element != null) {
                    try {
                        Object fieldValue = context.deserialize(element, field.getGenericType());
                        field.set(word, fieldValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        JsonElement pronunciationElement = json.getAsJsonObject().get("pronunciation");
        if (pronunciationElement.isJsonObject()) {
            String content = pronunciationElement.getAsJsonObject().get("all").getAsString();
            word.setPronunciation(content);
        } else if (pronunciationElement.isJsonPrimitive()) {
            String value = pronunciationElement.getAsString();
            word.setPronunciation(value);
        }

        return word;
    }
}
