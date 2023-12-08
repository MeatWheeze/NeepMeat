package com.neep.neepmeat.api.plc.recipe;

import com.google.gson.JsonObject;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.function.Function;

public interface ManufactureStep<T>
{
    Registry<Provider<?>> REGISTRY = FabricRegistryBuilder.createSimple(
        Provider.asClass(),
        new Identifier(NeepMeat.NAMESPACE, "step_provider")).buildAndRegister();

    static <T> Provider<T> register(Identifier id, Provider<T> provider)
    {
        return Registry.register(REGISTRY, id, provider);
    }

    void mutate(T t);

    Identifier getId();

    Text getName();

    void appendText(List<Text> tooltips);

    NbtCompound toNbt();

    boolean equalsOther(ManufactureStep<?> other);

    static boolean equals(ManufactureStep<?> a, ManufactureStep<?> b)
    {
        return (a == b) || (a != null && a.equalsOther(b));
    }

    interface Provider<T>
    {
        @SuppressWarnings("unchecked")
        static Class<Provider<?>> asClass()
        {
            return (Class<Provider<?>>) (Object) Provider.class;
        }

        static <T> Provider<T> of(Function<NbtCompound, ManufactureStep<T>> nbtConstructor, Function<JsonObject, ManufactureStep<T>> jsonConstructor)
        {
            return new Provider<T>()
            {
                @Override
                public ManufactureStep<T> create(NbtCompound nbt)
                {
                    return nbtConstructor.apply(nbt);
                }

                @Override
                public ManufactureStep<T> create(JsonObject json)
                {
                    return jsonConstructor.apply(json);
                }
            };
        }

        ManufactureStep<T> create(NbtCompound nbt);

        ManufactureStep<T> create(JsonObject json);
    }
}
