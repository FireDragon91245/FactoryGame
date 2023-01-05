package GameConverter;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class JsonExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        return fieldAttributes.getAnnotation(JSONExclude.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return aClass.getAnnotation(JSONExclude.class) != null;
    }
}
