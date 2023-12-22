package com.neep.neepmeat.client.renderer.entity;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.model.entity.WormEntityModel;
import com.neep.neepmeat.entity.worm.WormEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class WormEntityRenderer extends GeoEntityRenderer<WormEntity>
{
    protected static final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "block/duat_stone.png");

    public WormEntityRenderer(EntityRendererFactory.Context ctx)
    {
        super(ctx, new WormEntityModel());
    }

    @Override
    public Identifier getTexture(WormEntity entity)
    {
        return TEXTURE;
    }
}
