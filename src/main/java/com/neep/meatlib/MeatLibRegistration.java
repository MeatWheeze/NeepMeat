package com.neep.meatlib;

import com.neep.meatlib.registry.RegistrationContext;
import com.neep.meatlib.registry.annotation.Ignore;
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
        LIST.add(Pair.of(tClass, context));
    }

    public static void flush()
    {
        var it = LIST.iterator();
        while (it.hasNext())
        {
            var pair = it.next();

            Class<?> clazz = pair.key();
            @Nullable RegisterMe annotation = clazz.getAnnotation(RegisterMe.class);
            if (annotation == null)
                throw new IllegalStateException(String.format("Class %s is not annotated with %s", clazz, RegisterMe.class.getSimpleName()));

            RegistrationContext ctx = pair.value();
            String namespace = annotation.value();

            Arrays.stream(clazz.getDeclaredFields())
                    .filter(f -> Modifier.isStatic(f.getModifiers()))
                    .filter(f -> !f.isAnnotationPresent(Ignore.class))
                    .filter(f -> ctx.isValidClass(f.getType()))
                    .forEach(f -> processField(namespace, ctx, f));

            try
            {
                ctx.registerAll();
            }
            catch (Exception e)
            {
                MeatLib.LOGGER.error(e);
            }
            finally
            {
                it.remove();
            }
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
