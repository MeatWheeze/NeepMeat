package com.neep.meatweapons.client.renderer;

import com.neep.meatweapons.client.model.HandCannonItemModel;
import com.neep.meatweapons.item.HandCannonItem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class HandCannonItemRenderer extends GeoItemRenderer<HandCannonItem>
{
    public HandCannonItemRenderer()
    {
        super(new HandCannonItemModel());
    }

    @Override
    public RenderLayer getRenderType(HandCannonItem animatable, float partialTicks, MatrixStack stack,
                                     VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                     Identifier textureLocation)
    {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }
}
