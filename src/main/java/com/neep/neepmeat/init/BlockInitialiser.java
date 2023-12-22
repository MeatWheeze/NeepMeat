package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.block.FluidMeter;
import com.neep.neepmeat.block.PipeBlock;
import com.neep.neepmeat.block.PumpBlock;
import com.neep.neepmeat.fluid.BloodFluid;
import com.neep.neepmeat.item.FluidHoseItem;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Material;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockInitialiser
{
    public static FlowableFluid FLOWING_BLOOD;
    public static FlowableFluid STILL_BLOOD;
    public static Item BLOOD_BUCKET;

    public static Block TEST;
    public static Block PIPE;
    public static Block PUMP;
    public static Block FLUID_METER;

    public static Block registerBlock(String id, Block block)
    {
        return Registry.register(Registry.BLOCK, new Identifier(NeepMeat.NAMESPACE, id), block);
    }

    public static void registerBlocks()
    {
//        FLOWING_TEST = registerBlock("fluid_test", new FluidTestBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f)));

        PIPE = registerBlock("pipe", new PipeBlock("pipe", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        PUMP = registerBlock("pump", new PumpBlock("pump", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        FLUID_METER = registerBlock("fluid_meter", new FluidMeter("fluid_meter", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.LANTERN)));

        STILL_BLOOD = Registry.register(Registry.FLUID, new Identifier(NeepMeat.NAMESPACE, "blood"), new BloodFluid.Still());
        FLOWING_BLOOD = Registry.register(Registry.FLUID, new Identifier(NeepMeat.NAMESPACE, "flowing_blood"), new BloodFluid.Flowing());
        BLOOD_BUCKET = Registry.register(Registry.ITEM, new Identifier(NeepMeat.NAMESPACE, "fluid_hose"),
                new FluidHoseItem(STILL_BLOOD, new Item.Settings().maxCount(1).maxDamage(16).maxDamageIfAbsent(16)));

        TEST = Registry.register(Registry.BLOCK, new Identifier(NeepMeat.NAMESPACE, "acid"), new FluidBlock(STILL_BLOOD, FabricBlockSettings.copy(Blocks.WATER)){});
    }


}
