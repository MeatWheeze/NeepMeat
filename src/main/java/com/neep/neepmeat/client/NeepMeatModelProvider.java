package com.neep.neepmeat.client;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.model.block.ScaffoldBottomModel;
import com.neep.neepmeat.client.model.block.ScaffoldTopModel;
import com.neep.neepmeat.client.model.block.SlopeTest;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
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
        // Block
        MODELS.put(new Identifier(NeepMeat.NAMESPACE, "block/rusted_metal_scaffold_top"), () -> new ScaffoldTopModel(
//                NeepMeat.NAMESPACE, "block/rusted_metal_scaffold"));
                new Identifier(NeepMeat.NAMESPACE, "block/rusted_metal_scaffold_side"),
                new Identifier(NeepMeat.NAMESPACE, "block/rusted_metal_scaffold_top")));
        // Item
        MODELS.put(new Identifier(NeepMeat.NAMESPACE, "item/rusted_metal_scaffold"), () -> new ScaffoldTopModel(
//                NeepMeat.NAMESPACE, "rusted_metal_scaffold"));
                new Identifier(NeepMeat.NAMESPACE, "block/rusted_metal_scaffold_side"),
                new Identifier(NeepMeat.NAMESPACE, "block/rusted_metal_scaffold_top")));
        // Block
        MODELS.put(new Identifier(NeepMeat.NAMESPACE, "block/rusted_metal_scaffold_bottom"), () -> new ScaffoldBottomModel(
                new Identifier(NeepMeat.NAMESPACE, "block/rusted_metal_scaffold_side")));

        // Block
        MODELS.put(new Identifier(NeepMeat.NAMESPACE, "block/blue_metal_scaffold_top"), () -> new ScaffoldTopModel(
                new Identifier(NeepMeat.NAMESPACE, "block/blue_metal_scaffold_side"),
                new Identifier(NeepMeat.NAMESPACE, "block/blue_metal_scaffold_top")));
        // Item
        MODELS.put(new Identifier(NeepMeat.NAMESPACE, "item/blue_metal_scaffold"), () -> new ScaffoldTopModel(
                new Identifier(NeepMeat.NAMESPACE, "block/blue_metal_scaffold_side"),
                new Identifier(NeepMeat.NAMESPACE, "block/blue_metal_scaffold_top")));
        // Block
        MODELS.put(new Identifier(NeepMeat.NAMESPACE, "block/blue_metal_scaffold_bottom"), () -> new ScaffoldBottomModel(
                new Identifier(NeepMeat.NAMESPACE, "block/blue_metal_scaffold_side")));

        MODELS.put(new Identifier(NeepMeat.NAMESPACE, "block/slope_test"), SlopeTest::new);
    }
}
