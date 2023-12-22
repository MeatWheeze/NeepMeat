package com.neep.meatlib;

import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatlib.registry.SoundRegistry;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.impl.lookup.block.BlockApiLookupImpl;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class MeatLib implements ModInitializer
{
    public static String CURRENT_NAMESPACE;

    public static void setNamespace(String string)
    {
        CURRENT_NAMESPACE = string;
    }

    public static BlockApiLookup<Void, Void> VOID_LOOKUP =
            BlockApiLookup.get(new Identifier(NeepMeat.NAMESPACE, "sided_void"), Void.class, Void.class);

    @Override
    public void onInitialize()
    {
        BlockRegistry.init();
        ItemRegistry.init();
        SoundRegistry.init();
    }
}
