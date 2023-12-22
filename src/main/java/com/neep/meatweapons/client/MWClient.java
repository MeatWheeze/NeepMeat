package com.neep.meatweapons.client;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.client.model.*;
import com.neep.meatweapons.client.renderer.BaseGunRenderer;
import com.neep.meatweapons.client.renderer.BulletEntityRenderer;
import com.neep.meatweapons.client.renderer.CannonBulletEntityRenderer;
import com.neep.meatweapons.client.renderer.PlasmaEntityRenderer;
import com.neep.meatweapons.network.BeamPacket;
import com.neep.meatweapons.network.BulletEntityPacket;
import com.neep.meatweapons.particle.MWParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

@Environment(value= EnvType.CLIENT)
public class MWClient implements ClientModInitializer
{
    public static final EntityModelLayer MODEL_BULLET_LAYER = new EntityModelLayer(new Identifier(MeatWeapons.NAMESPACE, "bullet"), "main");
    public static final EntityModelLayer MODEL_CANNON_BULLET_LAYER = new EntityModelLayer(new Identifier(MeatWeapons.NAMESPACE, "cannon_bullet"), "main");
    public static final EntityModelLayer MODEL_PLASMA_LAYER = new EntityModelLayer(new Identifier(MeatWeapons.NAMESPACE, "plasma"), "main");

    public static void registerEntityModels()
    {
        EntityRendererRegistry.INSTANCE.register(MeatWeapons.BULLET, BulletEntityRenderer::new);
        BulletEntityPacket.registerReceiver();
        EntityModelLayerRegistry.registerModelLayer(MODEL_BULLET_LAYER, BulletEntityModel::getTexturedModelData);

        EntityRendererRegistry.INSTANCE.register(MeatWeapons.CANNON_BULLET, CannonBulletEntityRenderer::new);
        BulletEntityPacket.registerReceiver();
        EntityModelLayerRegistry.registerModelLayer(MODEL_CANNON_BULLET_LAYER, CannonBulletEntityModel::getTexturedModelData);

        EntityRendererRegistry.INSTANCE.register(MeatWeapons.PLASMA, PlasmaEntityRenderer::new);
        BulletEntityPacket.registerReceiver();
        EntityModelLayerRegistry.registerModelLayer(MODEL_PLASMA_LAYER, PlasmaEntityModel::getTexturedModelData);
    }

    public static void registerAnimations()
    {
        GeoItemRenderer.registerItemRenderer(MeatWeapons.HAND_CANNON, new BaseGunRenderer<>(new HandCannonItemModel()));
        GeoItemRenderer.registerItemRenderer(MeatWeapons.FUSION_CANNON, new BaseGunRenderer<>(new FusionCannonItemModel()));
        GeoItemRenderer.registerItemRenderer(MeatWeapons.MACHINE_PISTOL, new BaseGunRenderer<>(new PistolItemModel()));
        GeoItemRenderer.registerItemRenderer(MeatWeapons.LMG, new BaseGunRenderer<>(new LMGItemModel()));
    }

    @Override
    public void onInitializeClient()
    {
        BeamPacket.registerReceiver();
        registerEntityModels();
        registerAnimations();
        MWParticles.initClient();
    }

}
