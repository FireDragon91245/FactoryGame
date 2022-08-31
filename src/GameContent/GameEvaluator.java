package GameContent;

import GameCore.GameCore;
import com.fathzer.soft.javaluator.DoubleEvaluator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class GameEvaluator {

    private static final DoubleEvaluator evaluator = new DoubleEvaluator();

    private static final HashMap<String, Map.Entry<Object, Method>> aliases = new HashMap<>();

    public static double evaluate(String expression){
        String resolved = resolveAliases(expression);
        return evaluator.evaluate(resolved);
    }

    private static String resolveAliases(String expression) {
        String ex = expression;
        for(Map.Entry<String, Map.Entry<Object, Method>> entry : aliases.entrySet()){
            try {
                ex = ex.replace(entry.getKey(), entry.getValue().getValue().invoke(entry.getValue().getKey(), (Object[]) null).toString());
            } catch (IllegalAccessException | InvocationTargetException e) {
                GameCore.setErrorGameStateException(e);
            }
        }
        return ex;
    }

    public static void registerAlias(Object instance, Method getter, String alias){
        if(instance.getClass() == getter.getDeclaringClass()) {
            aliases.put(alias, new HashMap.SimpleEntry<>(instance, getter));
        }else{
            GameCore.setErrorGameStateException(new IllegalArgumentException("The method: " + getter.getName() + " declared in: " + getter.getDeclaringClass().getName() + " was tried to invoke on an instance of the class: " + instance.getClass().getName()));
        }
    }
}
