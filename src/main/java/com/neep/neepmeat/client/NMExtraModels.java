package com.neep.neepmeat.client;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.client.model.ExtraModelProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class NMExtraModels implements ExtraModelProvider
{
    public static NMExtraModels EXTRA_MODELS = new NMExtraModels();
//    public static ResourceManager MANAGER;

    public static Identifier BIG_LEVER_HANDLE = new Identifier(NeepMeat.NAMESPACE, "block/big_lever_handle");
    public static Identifier ITEM_PUMP_SHUTTLE = new Identifier(NeepMeat.NAMESPACE, "block/item_pump_shuttle");
    public static Identifier ITEM_PUMP_CHEST = new Identifier(NeepMeat.NAMESPACE, "block/item_pump_chest");

    @Override
    public void provideExtraModels(ResourceManager manager, Consumer<Identifier> out)
    {
        out.accept(BIG_LEVER_HANDLE);
        out.accept(ITEM_PUMP_SHUTTLE);
        out.accept(ITEM_PUMP_CHEST);
    }
}
