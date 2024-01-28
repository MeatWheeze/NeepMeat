package com.neep.neepmeat.init;

import com.neep.meatweapons.MWItems;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.enlightenment.EnlightenmentManager;
import com.neep.neepmeat.enlightenment.PlayerEnlightenmentManager;
import com.neep.neepmeat.implant.item.ItemImplantManager;
import com.neep.neepmeat.implant.player.ImplantManager;
import com.neep.neepmeat.implant.player.PlayerImplantManager;
import com.neep.neepmeat.plc.recipe.ItemWorkpiece;
import com.neep.neepmeat.api.plc.recipe.Workpiece;
import com.neep.neepmeat.plc.recipe.MobWorkpiece;
import com.neep.neepmeat.plc.recipe.PlayerWorkpiece;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class NMComponents implements EntityComponentInitializer, ItemComponentInitializer
{
    public static final ComponentKey<ImplantManager> IMPLANT_MANAGER =
            ComponentRegistry.getOrCreate(
                    new Identifier(NeepMeat.NAMESPACE, "implant_manager"),
                    ImplantManager.class);

    public static final ComponentKey<Workpiece> WORKPIECE =
            ComponentRegistry.getOrCreate(
                    new Identifier(NeepMeat.NAMESPACE, "workpiece"),
                    Workpiece.class);

    public static final ComponentKey<EnlightenmentManager> ENLIGHTENMENT_MANAGER =
            ComponentRegistry.getOrCreate(
                    new Identifier(NeepMeat.NAMESPACE, "enlightenment_manager"),
                    EnlightenmentManager.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry)
    {
        registry.beginRegistration(PlayerEntity.class, IMPLANT_MANAGER).impl(PlayerImplantManager.class).respawnStrategy(RespawnCopyStrategy.LOSSLESS_ONLY).end(PlayerImplantManager::new);
        registry.beginRegistration(PlayerEntity.class, ENLIGHTENMENT_MANAGER).impl(PlayerEnlightenmentManager.class).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(PlayerEnlightenmentManager::new);
        registry.registerFor(CowEntity.class, WORKPIECE, MobWorkpiece::new);
        registry.registerFor(PlayerEntity.class, WORKPIECE, PlayerWorkpiece::new);
    }

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry)
    {
        registry.register(Items.IRON_BLOCK, WORKPIECE, ItemWorkpiece::new);
        registry.register(NMItems.OPEN_EYE, WORKPIECE, ItemWorkpiece::new);
        registry.register(NMItems.WHISPER_FLOUR, WORKPIECE, ItemWorkpiece::new);
        registry.register(NMBlocks.MOTOR.asItem(), WORKPIECE, ItemWorkpiece::new);
        registry.register(NMBlocks.MEAT_STEEL_BLOCK.asItem(), WORKPIECE, ItemWorkpiece::new);
        registry.register(NMItems.MEAT_STEEL, WORKPIECE, ItemWorkpiece::new);
        registry.register(NMItems.MEAT_STEEL_COMPONENT, WORKPIECE, ItemWorkpiece::new);
        registry.register(NMItems.TRANSFORMING_TOOL_BASE, WORKPIECE, ItemWorkpiece::new);
        registry.register(NMItems.INTERNAL_COMPONENTS, WORKPIECE, ItemWorkpiece::new);
        registry.register(Items.MINECART, WORKPIECE, ItemWorkpiece::new);

        registry.register(MWItems.ASSAULT_DRILL, IMPLANT_MANAGER, ItemImplantManager::new);
    }
}
