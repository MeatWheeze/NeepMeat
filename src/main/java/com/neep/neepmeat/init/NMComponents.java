package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.player.implant.PlayerImplantManager;
import com.neep.neepmeat.plc.recipe.ItemWorkpiece;
import com.neep.neepmeat.plc.recipe.Workpiece;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class NMComponents implements EntityComponentInitializer, ItemComponentInitializer
{
    public static final ComponentKey<PlayerImplantManager> IMPLANT_MANAGER =
            ComponentRegistry.getOrCreate(
                    new Identifier(NeepMeat.NAMESPACE, "implant_manager"),
                    PlayerImplantManager.class);

    public static final ComponentKey<Workpiece> WORKPIECE =
            ComponentRegistry.getOrCreate(
                    new Identifier(NeepMeat.NAMESPACE, "workpiece"),
                    Workpiece.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry)
    {
        registry.registerForPlayers(IMPLANT_MANAGER, PlayerImplantManager::new, RespawnCopyStrategy.LOSSLESS_ONLY);
    }

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry)
    {
        registry.register(Items.IRON_BLOCK, WORKPIECE, ItemWorkpiece::new);
    }
}
