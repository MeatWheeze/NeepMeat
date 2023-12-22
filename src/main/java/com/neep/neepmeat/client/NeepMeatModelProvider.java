package com.neep.neepmeat.client;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.model.block.ScaffoldModel;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
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
        MODELS.put(new Identifier(NeepMeat.NAMESPACE, "block/scaffold_top"), ScaffoldModel::new);
    }
}
