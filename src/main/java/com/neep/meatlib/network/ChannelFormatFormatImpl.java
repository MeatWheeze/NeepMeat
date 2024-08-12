package com.neep.meatlib.network;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.api.network.ParamCodec;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class ChannelFormatFormatImpl<T> implements ChannelFormat<T>
{
    private final List<ParamCodec<Object>> codecs;
    private final List<Class<?>> invokeParameters;
    private final Emitter<T> emitter;
    private final Method method;

    private ChannelFormatFormatImpl(Class<T> clazz, List<ParamCodec<?>> codecs)
    {
        // ParamCodecs need to be <Object> because... erm... Because it won't compile otherwise!
        this.codecs = codecs.stream().map(c -> (ParamCodec<Object>) c).toList();

        // Get a handy list of method parameters.
        invokeParameters = codecs.stream().<Class<?>>map(ParamCodec::clazz).toList();

        this.method = findMethod(clazz, invokeParameters.toArray(new Class[0]));


        // This implementation automatically forwards the type-erased arguments of the emitter to Sender::send without
        // the user having to implement a lambda themselves. This should be safer since it is impossible for the user
        // to pass a Cow instead of an int in the emitter. It also means less boilerplate and hides the jank.

        // This will produce an IllegalArgumentException if the class is not an interface.
        @SuppressWarnings("unchecked")
        Emitter<T> emitter = sender -> (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (proxy, method, args) ->
        {
            // This has to be outside the switch as the method name is not constant
            String methodName = method.getName();
            if (methodName.equals(this.method.getName()))
            {
                send(sender, args);
                return false;
            }

            return switch (methodName)
            {
                case "toString" -> "proxy of " + this.method;
                case "equals" -> false;
                case "hashCode" -> 0;
                default -> throw new IllegalStateException("Unexpected method name: " + methodName);
            };
        });
        this.emitter = emitter;
    }

    private static Method findMethod(Class<?> clazz, Class<?>[] invokeParameters)
    {
        Method[] methods = clazz.getMethods();
        if (methods.length == 1)
        {
            Method method = methods[0];
            if (parametersMatch(method, invokeParameters))
                return method;
        }
        else
        {
            for (Method method : clazz.getMethods())
            {
                if (parametersMatch(method, invokeParameters))
                    return method;
            }
        }

        throw new IllegalArgumentException("Method not found in given class. Is it called 'apply' and do its parameters match those specified in the builder?");
    }

    private static boolean parametersMatch(Method method, Class<?>[] invokeParameters)
    {
        Class<?>[] methodParameters = method.getParameterTypes();

        if (methodParameters.length != invokeParameters.length)
            return false;

        for (int i = 0; i < methodParameters.length; i++)
        {
            if (!methodParameters[i].isAssignableFrom(invokeParameters[i]))
                return false;
        }
        return true;
    }

    public static <T> Builder<T> builder(Class<T> clazz)
    {
        return new Builder<>(clazz);
    }

    public T emitter(Sender<T> sender)
    {
        return emitter.create(sender);
    }

    public void send(Sender<T> sender, Object... objects)
    {
        // These errors will only arise from an incorrect lambda being passed on instantiation.
        // Under normal use, a type-safe interface (T) is be provided.
        if (objects.length != invokeParameters.size())
            throw new IllegalStateException("Incorrect number of parameters");

        PacketByteBuf buf = PacketByteBufs.create();
        for (int i = 0; i < objects.length; ++i)
        {
            ParamCodec<Object> codec = codecs.get(i);
            Object object = objects[i];

            codec.encode(object, buf);
        }

        sender.send(buf);
    }

    @Override
    public void receive(T listener, PacketByteBuf buf, Executor executor)
    {
        Object[] arguments = new Object[invokeParameters.size()];
        for (int i = 0; i < codecs.size(); ++i)
        {
            ParamCodec<Object> codec = codecs.get(i);
            Object object = codec.decode(buf);
            arguments[i] = object;
        }

        executor.execute(() ->
        {
            try
            {
                method.invoke(listener, arguments);
            }
            catch (IllegalAccessException e)
            {
                throw new IllegalArgumentException("This should probably not happen.");
            }
            catch (InvocationTargetException e)
            {
                throw new IllegalStateException(e.getCause());
            }
        });
    }

    public static class Builder<T>
    {
        private final Class<T> clazz;
        List<ParamCodec<?>> codecs = new ArrayList<>();

        public Builder(Class<T> clazz)
        {
            this.clazz = clazz;
        }

        public <V> Builder<T> param(ParamCodec<V> codec)
        {
            codecs.add(codec);
            return this;
        }

        public ChannelFormat<T> build()
        {
            return new ChannelFormatFormatImpl<>(clazz, codecs);
        }
    }

    public interface Emitter<T>
    {
        T create(Sender<T> sender);
    }
}
