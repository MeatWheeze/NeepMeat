package com.neep.neepmeat.client;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.model.block.ScaffoldBottomModel;
import com.neep.neepmeat.client.model.block.ScaffoldTopModel;
import com.neep.neepmeat.client.model.block.SlopeTest;
import com.neep.neepmeat.init.BlockInitialiser;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.block.Block;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NeepMeatModelProvider implements ModelResourceProvider
{

    public static final Map<Identifier, Supplier<UnbakedModel>> MODELS = new HashMap<>();

    @Override
    public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException
    {
        Supplier<UnbakedModel> model = MODELS.get(resourceId);
        return model != null ? model.get() : null;
    }

    static
    {
        Identifier RUSTED_SCAFFOLD_SIDE = new Identifier(NeepMeat.NAMESPACE, "block/rusted_metal_scaffold_side");
        Identifier RUSTED_SCAFFOLD_TOP = new Identifier(NeepMeat.NAMESPACE, "block/rusted_metal_scaffold_top");

        Identifier BLUE_SCAFFOLD_SIDE = new Identifier(NeepMeat.NAMESPACE, "block/blue_metal_scaffold_side");
        Identifier BLUE_SCAFFOLD_TOP = new Identifier(NeepMeat.NAMESPACE, "block/blue_metal_scaffold_top");

        Identifier YELLOW_SCAFFOLD_SIDE = new Identifier(NeepMeat.NAMESPACE, "block/yellow_metal_scaffold_side");
        Identifier YELLOW_SCAFFOLD_TOP = new Identifier(NeepMeat.NAMESPACE, "block/yellow_metal_scaffold_top");

                // Block
//        MODELS.put(new Identifier(NeepMeat.NAMESPACE, "block/rusted_metal_scaffold_top"), () -> new ScaffoldTopModel(
////                NeepMeat.NAMESPACE, "block/rusted_metal_scaffold"));
//                RUSTED_SCAFFOLD_SIDE, RUSTED_SCAFFOLD_TOP, BlockInitialiser.SCAFFOLD_PLATFORM));
//        // Item
//        MODELS.put(new Identifier(NeepMeat.NAMESPACE, "item/rusted_metal_scaffold"), () -> new ScaffoldTopModel(
////                NeepMeat.NAMESPACE, "rusted_metal_scaffold"));
//                RUSTED_SCAFFOLD_SIDE, RUSTED_SCAFFOLD_TOP,
//                BlockInitialiser.SCAFFOLD_PLATFORM));
//        // Block
//        MODELS.put(new Identifier(NeepMeat.NAMESPACE, "block/rusted_metal_scaffold_bottom"), () -> new ScaffoldBottomModel(
//                RUSTED_SCAFFOLD_SIDE,
//                BlockInitialiser.SCAFFOLD_PLATFORM));

        putScaffoldModels(new Identifier(NeepMeat.NAMESPACE, "block/rusted_metal_scaffold_top"),
                new Identifier(NeepMeat.NAMESPACE, "block/rusted_metal_scaffold_bottom"),
                new Identifier(NeepMeat.NAMESPACE, "item/rusted_metal_scaffold"),
                RUSTED_SCAFFOLD_SIDE,
                RUSTED_SCAFFOLD_TOP,
                BlockInitialiser.SCAFFOLD_PLATFORM);

        putScaffoldModels(new Identifier(NeepMeat.NAMESPACE, "block/blue_metal_scaffold_top"),
                new Identifier(NeepMeat.NAMESPACE, "block/blue_metal_scaffold_bottom"),
                new Identifier(NeepMeat.NAMESPACE, "item/blue_metal_scaffold"),
                BLUE_SCAFFOLD_SIDE,
                BLUE_SCAFFOLD_TOP,
                BlockInitialiser.BLUE_SCAFFOLD);

        putScaffoldModels(new Identifier(NeepMeat.NAMESPACE, "block/yellow_metal_scaffold_top"),
                new Identifier(NeepMeat.NAMESPACE, "block/yellow_metal_scaffold_bottom"),
                new Identifier(NeepMeat.NAMESPACE, "item/yellow_metal_scaffold"),
                YELLOW_SCAFFOLD_SIDE,
                YELLOW_SCAFFOLD_TOP,
                BlockInitialiser.YELLOW_SCAFFOLD);

        // Block
//        MODELS.put(new Identifier(NeepMeat.NAMESPACE, "block/blue_metal_scaffold_top"), () -> new ScaffoldTopModel(
//                BLUE_SCAFFOLD_SIDE, BLUE_SCAFFOLD_TOP,
//                BlockInitialiser.BLUE_SCAFFOLD));
//        // Item
//        MODELS.put(new Identifier(NeepMeat.NAMESPACE, "item/blue_metal_scaffold"), () -> new ScaffoldTopModel(
//                BLUE_SCAFFOLD_SIDE, BLUE_SCAFFOLD_TOP,
//                BlockInitialiser.BLUE_SCAFFOLD));
//        // Block
//        MODELS.put(new Identifier(NeepMeat.NAMESPACE, "block/blue_metal_scaffold_bottom"), () -> new ScaffoldBottomModel(
//                BLUE_SCAFFOLD_SIDE,
//                BlockInitialiser.BLUE_SCAFFOLD));

        MODELS.put(new Identifier(NeepMeat.NAMESPACE, "block/slope_test"), SlopeTest::new);
    }

    public static void putScaffoldModels(Identifier topId, Identifier bottomId, Identifier itemId, Identifier sideTexture, Identifier topTexture, Block block)
    {
        // Top block
        MODELS.put(topId, () -> new ScaffoldTopModel(sideTexture, topTexture, block));
        MODELS.put(itemId, () -> new ScaffoldTopModel(sideTexture, topTexture, block));
        MODELS.put(bottomId, () -> new ScaffoldBottomModel(sideTexture, block));
    }
}
