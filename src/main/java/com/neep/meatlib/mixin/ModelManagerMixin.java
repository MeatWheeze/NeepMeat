package com.neep.meatlib.mixin;

import com.neep.meatlib.client.MeatlibModelManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(BakedModelManager.class)
public class ModelManagerMixin implements MeatlibModelManager
{
    @Shadow private Map<Identifier, BakedModel> models;

    @Override
    public BakedModel meatlib$getModel(Identifier identifier)
    {
        return models.get(identifier);
    }
}
