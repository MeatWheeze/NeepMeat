package com.neep.neepmeat.block;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.fluid.BloodFluid;
import com.neep.neepmeat.item.FluidHoseItem;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockInitialiser
{
    public static FlowableFluid FLOWING_TEST;
    public static FlowableFluid STILL_TEST;
    public static Item TEST_BUCKET;

    public static Block TEST;

    public static Block registerBlock(String id, Block block)
    {
        return Registry.register(Registry.BLOCK, new Identifier(NeepMeat.NAMESPACE, id), block);
    }

    public static void registerBlocks()
    {
//        FLOWING_TEST = registerBlock("fluid_test", new FluidTestBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f)));

        STILL_TEST = Registry.register(Registry.FLUID, new Identifier(NeepMeat.NAMESPACE, "blood"), new BloodFluid.Still());
        FLOWING_TEST = Registry.register(Registry.FLUID, new Identifier(NeepMeat.NAMESPACE, "flowing_blood"), new BloodFluid.Flowing());
        TEST_BUCKET = Registry.register(Registry.ITEM, new Identifier(NeepMeat.NAMESPACE, "fluid_hose"),
                new FluidHoseItem(STILL_TEST, new Item.Settings().maxCount(1).maxDamage(16).maxDamageIfAbsent(16)));

        TEST = Registry.register(Registry.BLOCK, new Identifier(NeepMeat.NAMESPACE, "acid"), new FluidBlock(STILL_TEST, FabricBlockSettings.copy(Blocks.WATER)){});
    }


}
