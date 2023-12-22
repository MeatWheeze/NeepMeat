package com.neep.neepmeat.client.renderer.entity;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.model.entity.HoundEntityModel;
import com.neep.neepmeat.entity.hound.HoundEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class HoundEntityRenderer extends LivingEntityRenderer<HoundEntity, HoundEntityModel>
{
    private static final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/entity/hound/main.png");

    public static final EntityModelLayer HOUND_LAYER = new EntityModelLayer(new Identifier(NeepMeat.NAMESPACE, "hound"), "hound");
    public HoundEntityRenderer(EntityRendererFactory.Context ctx)
    {
        super(ctx, new HoundEntityModel(ctx.getPart(HOUND_LAYER)), 0.5f);
    }

    @Override
    protected boolean hasLabel(HoundEntity livingEntity)
    {
        return super.hasLabel(livingEntity);
    }

    @Override
    public Identifier getTexture(HoundEntity entity)
    {
        return TEXTURE;
    }
}
