package com.neep.meatweapons.client.renderer;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.entity.BulletEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class BulletEntityRenderer extends ProjectileEntityRenderer<BulletEntity>
{
    public BulletEntityRenderer(EntityRendererFactory.Context context)
    {
        super(context);
    }

    @Override
    public Identifier getTexture(BulletEntity entity) {
        return new Identifier(MeatWeapons.NAMESPACE, "textures/block/composite.png");
    }
}
