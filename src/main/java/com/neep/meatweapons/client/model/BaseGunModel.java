package com.neep.meatweapons.client.model;

import com.neep.meatweapons.item.BaseGunItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BaseGunModel<T extends BaseGunItem> extends AnimatedGeoModel<T>
{
    private final Identifier modelPath;
    private final Identifier texturePath;
    private final Identifier animationPath;

    public BaseGunModel(Identifier modelPath, Identifier texturePath, Identifier animationPath)
    {
        this.modelPath = modelPath;
        this.texturePath = texturePath;
        this.animationPath = animationPath;
    }


    @Override
    public Identifier getModelLocation(T object)
    {
        return modelPath;
    }

    @Override
    public Identifier getTextureLocation(T object)
    {
        return texturePath;
    }

    @Override
    public Identifier getAnimationFileLocation(T animatable)
    {
        return animationPath;
    }
}
