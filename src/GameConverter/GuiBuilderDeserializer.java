package GameConverter;

import GameContent.GameEvaluator;
import GameCore.GuiBuilder;
import GameCore.Main;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;

public class GuiBuilderDeserializer implements JsonDeserializer<GuiBuilder> {


    @Override
    public GuiBuilder deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        for(Map.Entry<String, JsonElement> entry : obj.deepCopy().entrySet()){
            if(entry.getValue().isJsonPrimitive()){
                if(entry.getValue().getAsJsonPrimitive().isString()){
                    if(!entry.getValue().getAsString().startsWith("$")){
                        continue;
                    }
                    obj.remove(entry.getKey());
                    obj.addProperty(entry.getKey(), GameEvaluator.evaluate(entry.getValue().getAsString().substring(1)));
                }
            }
        }
        return Main.getGsonMaster().fromJson(obj, type);
    }

}
