package com.neep.neepmeat.client;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.model.block.*;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.transport.client.model.VascularConduitModel;
import net.fabricmc.fabric.api.client.model.ExtraModelProvider;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.block.Block;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NeepMeatModelProvider implements ModelResourceProvider, ExtraModelProvider
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

        Identifier CLEAR_TANK_SIDE = new Identifier(NeepMeat.NAMESPACE, "block/clear_tank_wall");
        Identifier CLEAR_TANK_TOP = new Identifier(NeepMeat.NAMESPACE, "block/clear_tank_wall");

        putScaffoldModels(new Identifier(NeepMeat.NAMESPACE, "block/rusted_metal_scaffold_top"),
                new Identifier(NeepMeat.NAMESPACE, "block/rusted_metal_scaffold_bottom"),
                new Identifier(NeepMeat.NAMESPACE, "item/rusted_metal_scaffold"),
                RUSTED_SCAFFOLD_SIDE,
                RUSTED_SCAFFOLD_TOP,
                NMBlocks.SCAFFOLD_PLATFORM);

        putScaffoldModels(new Identifier(NeepMeat.NAMESPACE, "block/blue_metal_scaffold_top"),
                new Identifier(NeepMeat.NAMESPACE, "block/blue_metal_scaffold_bottom"),
                new Identifier(NeepMeat.NAMESPACE, "item/blue_metal_scaffold"),
                BLUE_SCAFFOLD_SIDE,
                BLUE_SCAFFOLD_TOP,
                NMBlocks.BLUE_SCAFFOLD);

        putScaffoldModels(new Identifier(NeepMeat.NAMESPACE, "block/yellow_metal_scaffold_top"),
                new Identifier(NeepMeat.NAMESPACE, "block/yellow_metal_scaffold_bottom"),
                new Identifier(NeepMeat.NAMESPACE, "item/yellow_metal_scaffold"),
                YELLOW_SCAFFOLD_SIDE,
                YELLOW_SCAFFOLD_TOP,
                NMBlocks.YELLOW_SCAFFOLD);

        putScaffoldModels(new Identifier(NeepMeat.NAMESPACE, "block/clear_tank_wall"),
                new Identifier(NeepMeat.NAMESPACE, "block/clear_tank_wall"),
                new Identifier(NeepMeat.NAMESPACE, "item/clear_tank_wall"),
                CLEAR_TANK_SIDE,
                CLEAR_TANK_TOP,
                NMBlocks.YELLOW_SCAFFOLD);

        MODELS.put(new Identifier(NeepMeat.NAMESPACE, "block/slope_test"), SlopeTest::new);
        MODELS.put(new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_sides"), FluidPipeModel::new);
        MODELS.put(new Identifier(NeepMeat.NAMESPACE, "block/vascular_conduit/pipe_sides"), VascularConduitModel::new);

        MODELS.put(new Identifier(NeepMeat.NAMESPACE, "block/encased_conduit"), EncasedConduitModel::new);
    }

    public static void putScaffoldModels(Identifier topId, Identifier bottomId, Identifier itemId, Identifier sideTexture, Identifier topTexture, Block block)
    {
        // Top block
        MODELS.put(topId, () -> new ScaffoldTopModel(sideTexture, topTexture, block));
        MODELS.put(itemId, () -> new ScaffoldTopModel(sideTexture, topTexture, block));
        MODELS.put(bottomId, () -> new ScaffoldBottomModel(sideTexture, block));
    }

    @Override
    public void provideExtraModels(ResourceManager manager, Consumer<Identifier> out)
    {

    }
}
