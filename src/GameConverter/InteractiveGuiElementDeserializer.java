package GameConverter;

import GameContent.GameEvaluator;
import GameCore.GuiElements.InteractiveGuiElement;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;

public class InteractiveGuiElementDeserializer implements JsonDeserializer<InteractiveGuiElement> {

    @Override
    public InteractiveGuiElement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        for(Map.Entry<String, JsonElement> entry : jsonObject.deepCopy().entrySet()){
            if(entry.getValue().isJsonPrimitive()){
                if(entry.getValue().getAsJsonPrimitive().isString()){
                    if(!entry.getValue().getAsString().startsWith("$")){
                        continue;
                    }
                    jsonObject.remove(entry.getKey());
                    jsonObject.addProperty(entry.getKey(), GameEvaluator.evaluate(entry.getValue().getAsString()));
                }
            }
        }
        String componentType = jsonObject.get("componentType").getAsString();
        try {
            return jsonDeserializationContext.deserialize(jsonObject, Class.forName(componentType));
        }catch (ClassNotFoundException e) {
            throw new JsonParseException(e.getMessage(), e);
        }
    }
}
