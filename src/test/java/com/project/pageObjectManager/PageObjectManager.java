package com.project.pageObjectManager;

import com.project.pages.CommonSteps;
import org.junit.jupiter.api.Assertions;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
public class PageObjectManager {
    private static final Map<String, CommonSteps> instances = new HashMap<>();

    public static <T extends CommonSteps> T get(Class<T> c) {
        String key = c.toString();
        CommonSteps instance = instances.get(key);
        if (instance == null) {
            try {
                instance = c.getDeclaredConstructor().newInstance();
                instances.put(key, instance);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                Assertions.fail(e.getMessage());
            }
        }
        return c.cast(instance);
    }
}
