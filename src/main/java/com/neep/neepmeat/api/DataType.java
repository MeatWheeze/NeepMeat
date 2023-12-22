package com.neep.neepmeat.api;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DataType
{
    public static final Registry<DataType> REGISTRY = FabricRegistryBuilder.createSimple(DataType.class,
            new Identifier(NeepMeat.NAMESPACE, "data_type")).buildAndRegister();

    public static final DataType BLANK = Registry.register(REGISTRY, new Identifier(NeepMeat.NAMESPACE, "blank"), new DataType());
    public static final DataType NORMAL = Registry.register(REGISTRY, new Identifier(NeepMeat.NAMESPACE, "normal"), new DataType());
    public static final DataType DIVINE = Registry.register(REGISTRY, new Identifier(NeepMeat.NAMESPACE, "divine"), new DataType());
}
