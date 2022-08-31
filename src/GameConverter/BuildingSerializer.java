package GameConverter;

import GameBuildings.Building;
import com.google.gson.*;

import java.lang.reflect.Type;

public class BuildingSerializer implements JsonSerializer<Building> {

    @Override
    public JsonElement serialize(Building building, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = (JsonObject) new Gson().toJsonTree(building);
        obj.addProperty("buildingType", building.getClass().getName());
        return obj;
    }
}
