package com.neep.meatweapons.client.renderer;

import com.neep.meatweapons.client.model.HandCannonItemModel;
import com.neep.meatweapons.item.HandCannonItem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class HandCannonItemRenderer extends GeoItemRenderer<HandCannonItem>
{
    public HandCannonItemRenderer()
    {
        super(new HandCannonItemModel());
    }

    @Override
    public RenderLayer getRenderType(HandCannonItem animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick)
    {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }
}
