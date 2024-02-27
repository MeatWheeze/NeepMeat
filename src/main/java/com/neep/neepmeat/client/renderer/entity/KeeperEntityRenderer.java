package com.neep.neepmeat.client.renderer.entity;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.model.entity.KeeperEntityModel;
import com.neep.neepmeat.entity.keeper.KeeperEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

@Environment(value= EnvType.CLIENT)
public class KeeperEntityRenderer extends BipedEntityRenderer<KeeperEntity, KeeperEntityModel>
{
    protected static final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/entity/keeper/keeper.png");

    public static final EntityModelLayer KEEPER = new EntityModelLayer(new Identifier(NeepMeat.NAMESPACE, "keeper"), "main");
    public static final EntityModelLayer KEEPER_INNER = new EntityModelLayer(new Identifier(NeepMeat.NAMESPACE, "keeper"), "inner_armor");
    public static final EntityModelLayer KEEPER_OUTER = new EntityModelLayer(new Identifier(NeepMeat.NAMESPACE, "keeper"), "outer_armor");

    public KeeperEntityRenderer(EntityRendererFactory.Context context)
    {
        this(context, EntityModelLayers.ZOMBIE, EntityModelLayers.ZOMBIE_INNER_ARMOR, EntityModelLayers.ZOMBIE_OUTER_ARMOR);
    }

    public KeeperEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer, EntityModelLayer legsArmorLayer, EntityModelLayer bodyArmorLayer)
    {
        super(ctx, new KeeperEntityModel(ctx.getPart(layer)), 0.5f);
//        super(ctx, new KeeperEntityModel(ctx.getPart(layer)), new KeeperEntityModel(ctx.getPart(legsArmorLayer)), new KeeperEntityModel(ctx.getPart(bodyArmorLayer)));
//        this.addFeature(new ArmorFeatureRenderer<>(this, new KeeperEntityModel(ctx.getPart(legsArmorLayer)), new KeeperEntityModel(ctx.getPart(bodyArmorLayer))));
    }

    @Override
    public Identifier getTexture(KeeperEntity mobEntity)
    {
        return TEXTURE;
    }
}

