package com.neep.neepmeat.api.live_machine;

import com.neep.neepmeat.NeepMeat;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public interface ComponentType<T extends LivingMachineComponent>
{
    @SuppressWarnings("unchecked")
    SimpleRegistry<ComponentType<?>> REGISTRY = FabricRegistryBuilder.createSimple(
            (Class<ComponentType<?>>) (Object) ComponentType.class, new Identifier(NeepMeat.NAMESPACE, "lm_component_type")).buildAndRegister();

    Int2ObjectMap<ComponentType<?>> ID_TO_TYPE = new Int2ObjectArrayMap<>();

    int getBitIdx();

    class Simple<T extends LivingMachineComponent> implements ComponentType<T>
    {
        static int NEXT_ID = 0;

        private final int id;

        public static int size()
        {
            return NEXT_ID;
        }

        public Simple()
        {
            id = NEXT_ID++;
        }

        public int getBitIdx()
        {
            return id;
        }
    }
}
