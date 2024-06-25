package com.neep.neepmeat.client.renderer.entity;

import com.neep.neepmeat.machine.small_compressor.SmallCompressorMinecart;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;

public class SmallCompressorMinecartRenderer extends MinecartEntityRenderer<SmallCompressorMinecart>
{
    public SmallCompressorMinecartRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer)
    {
        super(ctx, layer);
    }
}
