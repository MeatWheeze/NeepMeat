package com.neep.neepmeat.client.renderer.entity;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.model.entity.HoundEntityModel;
import com.neep.neepmeat.entity.hound.HoundEntity;
import com.neep.neepmeat.init.NMItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
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
    public void render(HoundEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i)
    {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player.getInventory().getArmorStack(3).isOf(NMItems.GOGGLES))
        {
            super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
        }
    }

    @Override
    protected boolean hasLabel(HoundEntity livingEntity)
    {
        // For some reason the label is rendered by default.
        return false;
    }

    @Override
    public Identifier getTexture(HoundEntity entity)
    {
        return TEXTURE;
    }
}
