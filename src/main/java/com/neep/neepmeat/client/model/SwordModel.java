package com.neep.neepmeat.client.model;

import com.neep.neepmeat.item.AnimatedSword;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SwordModel<T extends AnimatedSword> extends AnimatedGeoModel<T>
{
    public final String namespace;
    public final String modelPath;
    public final String texturePath;
    public final String animPath;

    public SwordModel(String namespace, String modelPath, String texturePath, String animPath)
    {
        this.namespace = namespace;
        this.modelPath = modelPath;
        this.texturePath = texturePath;
        this.animPath = animPath;
    }

    @Override
    public Identifier getModelLocation(AnimatedSword object)
    {
        return new Identifier(namespace, modelPath);
    }

    @Override
    public Identifier getTextureLocation(AnimatedSword object)
    {
        return new Identifier(namespace, texturePath);
    }

    @Override
    public Identifier getAnimationFileLocation(AnimatedSword animatable)
    {
        return new Identifier(namespace, animPath);
    }
}
