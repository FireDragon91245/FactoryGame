package GameConverter;

import GameCore.GuiElements.*;
import com.google.gson.*;

import java.lang.reflect.Type;

public class StaticGuiElementDeserializer implements JsonDeserializer<StaticGuiElement> {
    @Override
    public StaticGuiElement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String componentType = jsonObject.get("componentType").getAsString();
        if (type == null) {
            throw new JsonParseException("Not able to parse enum \"ComponentTypes\" from value " + jsonObject.get("componentType").getAsString());
        }
        try {
            return jsonDeserializationContext.deserialize(jsonObject, Class.forName(componentType));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e.getMessage(), e);
        }
    }
}
