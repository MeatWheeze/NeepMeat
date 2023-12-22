package com.neep.neepmeat.plc.recipe;

import com.google.common.collect.Maps;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.http.auth.NTCredentials;

import java.util.List;
import java.util.Map;

public interface ManufactureStep<T>
{
    Map<String, Provider<?>> REGISTRY = Maps.newHashMap();
    
    static <T> Provider<T> register(Identifier id, Provider<T> provider)
    {
        REGISTRY.put(id.toString(), provider);
        return provider;
    }

    void mutate(T t);

    Identifier getId();

    void appendText(List<Text> tooltips);

    NbtCompound toNbt();

    interface Provider<T>
    {
        ManufactureStep<T> create(NbtCompound nbt);
    }
}
