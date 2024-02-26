package com.neep.neepmeat.client.model;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.GeoModel;

public class GenericModel<T extends IAnimatable> extends GeoModel<T>
{
    public final String namespace;
    public final String modelPath;
    public final String texturePath;
    public final String animPath;

    public GenericModel(String namespace, String modelPath, String texturePath, String animPath)
    {
        this.namespace = namespace;
        this.modelPath = modelPath;
        this.texturePath = texturePath;
        this.animPath = animPath;
    }

    @Override
    public Identifier getModelResource(IAnimatable object)
    {
        return new Identifier(namespace, modelPath);
    }

    @Override
    public Identifier getTextureResource(IAnimatable object)
    {
        return new Identifier(namespace, texturePath);
    }

    @Override
    public Identifier getAnimationResource(IAnimatable animatable)
    {
        return new Identifier(namespace, animPath);
    }
}
