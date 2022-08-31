package GameConverter;

import com.google.gson.*;

import java.awt.*;
import java.lang.reflect.Type;

public class FontDeserializer implements JsonDeserializer<Font> {

    @SuppressWarnings("MagicConstant")
    @Override
    public Font deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        return new Font(object.get("fontName").getAsString(), convertNameToStyle(object.get("fontStyle").getAsString()), object.get("fontSize").getAsInt());
    }

    private int convertNameToStyle(String fontStyle) {
        return switch(fontStyle.toLowerCase()){
            case "plain" -> Font.PLAIN;
            case "bold" -> Font.BOLD;
            case "italic" -> Font.ITALIC;
            default -> throw new JsonParseException("Unexpected value for font Style: " + fontStyle);
        };
    }

}
