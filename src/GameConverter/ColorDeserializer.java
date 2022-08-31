package GameConverter;

import com.google.gson.*;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ColorDeserializer implements JsonDeserializer<Color> {
    @Override
    public Color deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        if(object.has("alias")){
            ArrayList<Field> fields = new ArrayList<>(List.of(Color.class.getDeclaredFields()));
            for(Field f : fields){
                if(f.getName().equalsIgnoreCase(object.get("alias").getAsString())){
                    f.setAccessible(true);
                    Color instance = new Color(255);
                    try {
                        return (Color) f.get(instance);
                    } catch (IllegalAccessException e) {
                        throw new JsonParseException(e.getMessage(), e);
                    }
                }
            }
            throw new JsonParseException("No match for color alias! NULL");
        }
        else if(object.has("hex")){
            return Color.decode(object.get("hex").getAsString());
        }
        else {
            int r = object.get("r").getAsInt();
            int g = object.get("g").getAsInt();
            int b = object.get("b").getAsInt();
            int a = object.get("a").getAsInt();
            return new Color(r, g, b, a);
        }
    }
}
