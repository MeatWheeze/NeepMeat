package com.neepmeat.realistic_fluid.block;

import com.neepmeat.realistic_fluid.RealisticFluid;
import com.neepmeat.realistic_fluid.fluid.TestFluid;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Material;
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
        return Registry.register(Registry.BLOCK, new Identifier(RealisticFluid.NAMESPACE, id), block);
    }

    public static void registerBlocks()
    {
//        FLOWING_TEST = registerBlock("fluid_test", new FluidTestBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f)));

        STILL_TEST = Registry.register(Registry.FLUID, new Identifier(RealisticFluid.NAMESPACE, "acid"), new TestFluid.Still());
        FLOWING_TEST = Registry.register(Registry.FLUID, new Identifier(RealisticFluid.NAMESPACE, "flowing_acid"), new TestFluid.Flowing());
        TEST_BUCKET = Registry.register(Registry.ITEM, new Identifier(RealisticFluid.NAMESPACE, "acid_bucket"),
                new BucketItem(STILL_TEST, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));

        TEST = Registry.register(Registry.BLOCK, new Identifier(RealisticFluid.NAMESPACE, "acid"), new FluidBlock(STILL_TEST, FabricBlockSettings.copy(Blocks.WATER)){});
    }


}
