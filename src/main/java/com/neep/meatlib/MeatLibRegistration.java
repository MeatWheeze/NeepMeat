package com.neep.meatlib;

import com.neep.meatlib.registry.RegistrationContext;
import com.neep.meatlib.registry.annotation.RegisterMe;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeatLibRegistration
{
    private static final List<Pair<Class<?>, RegistrationContext>> LIST = new ArrayList<>();

    public static <T> void forContext(Class<T> tClass, RegistrationContext context)
    {
    }

    public static void flush()
    {
        for (var pair : LIST)
        {
            Class<?> clazz = pair.key();
            @Nullable RegisterMe annotation = clazz.getAnnotation(RegisterMe.class);
            if (annotation == null)
                throw new IllegalStateException(String.format("Class %s is not annotated with %s", clazz, RegisterMe.class.getSimpleName()));

            String namespace = annotation.namespace();

            Arrays.stream(clazz.getDeclaredFields())
                    .filter(f -> Modifier.isStatic(f.getModifiers()))
                    .forEach(f -> processField(namespace, pair.value(), f));
        }
    }

    private static void processField(String namespace, RegistrationContext ctx, Field field)
    {
        try
        {
            Object o = field.get(null);
            ctx.collectField(namespace, o, field);
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalStateException(e);
        }
    }
}
