package com.neep.meatlib.client;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.NotImplementedException;

public interface MeatlibModelManager
{
    default BakedModel meatlib$getModel(Identifier identifier)
    {
        throw new NotImplementedException();
    }
}
