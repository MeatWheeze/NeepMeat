package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.entity.MobPlatformRidingEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class MobPlatformEntityRenderer extends EntityRenderer<MobPlatformRidingEntity>
{
    public MobPlatformEntityRenderer(EntityRendererFactory.Context ctx)
    {
        super(ctx);
    }

    @Override
    public Identifier getTexture(MobPlatformRidingEntity entity)
    {
        return new Identifier("missing");
    }

    @Override
    public boolean shouldRender(MobPlatformRidingEntity entity, Frustum frustum, double x, double y, double z)
    {
        return false;
    }
}
