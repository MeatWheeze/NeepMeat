package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.model.GenericModel;
import com.neep.neepmeat.entity.bovine_horror.BovineHorrorEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class BovineHorrorRenderer extends GeoEntityRenderer<BovineHorrorEntity>
{
    public BovineHorrorRenderer(EntityRendererFactory.Context renderManager)
    {
        super(renderManager, new GenericModel<>(NeepMeat.NAMESPACE,
                "geo/bovine_horror.geo.json",
                "textures/entity/bovine_horror.png",
                "animations/bovine_horror.animation.json"
                ));
    }
}
