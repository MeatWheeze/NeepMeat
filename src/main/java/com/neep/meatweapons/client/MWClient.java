package com.neep.meatweapons.client;

import com.neep.meatweapons.MWItems;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.client.model.*;
import com.neep.meatweapons.client.renderer.*;
import com.neep.meatweapons.client.sound.AirtruckSoundInstance;
import com.neep.meatweapons.network.BeamPacket;
import com.neep.meatweapons.network.ProjectileSpawnPacket;
import com.neep.meatweapons.particle.MWParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

@Environment(value= EnvType.CLIENT)
public class MWClient implements ClientModInitializer
{
    public static final EntityModelLayer MODEL_BULLET_LAYER = new EntityModelLayer(new Identifier(MeatWeapons.NAMESPACE, "bullet"), "main");
    public static final EntityModelLayer MODEL_CANNON_BULLET_LAYER = new EntityModelLayer(new Identifier(MeatWeapons.NAMESPACE, "cannon_bullet"), "main");
    public static final EntityModelLayer MODEL_PLASMA_LAYER = new EntityModelLayer(new Identifier(MeatWeapons.NAMESPACE, "plasma"), "main");
    public static final EntityModelLayer MODEL_SHELL_LAYER = new EntityModelLayer(new Identifier(MeatWeapons.NAMESPACE, "shell"), "main");

    public static void registerEntityModels()
    {
        EntityRendererRegistry.INSTANCE.register(MeatWeapons.BULLET, BulletEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_BULLET_LAYER, BulletEntityModel::getTexturedModelData);

        EntityRendererRegistry.INSTANCE.register(MeatWeapons.CANNON_BULLET, CannonBulletEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_CANNON_BULLET_LAYER, CannonBulletEntityModel::getTexturedModelData);

        EntityRendererRegistry.INSTANCE.register(MeatWeapons.PLASMA, PlasmaEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_PLASMA_LAYER, PlasmaEntityModel::getTexturedModelData);

        EntityRendererRegistry.INSTANCE.register(MeatWeapons.EXPLODING_SHELL, ShellEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_SHELL_LAYER, PlasmaEntityModel::getTexturedModelData);

        ProjectileSpawnPacket.Client.registerReceiver();
    }

    public static void registerAnimations()
    {
        GeoItemRenderer.registerItemRenderer(MWItems.HAND_CANNON, new BaseGunRenderer<>(new HandCannonItemModel()));
        GeoItemRenderer.registerItemRenderer(MWItems.FUSION_CANNON, new BaseGunRenderer<>(new FusionCannonItemModel()));
        GeoItemRenderer.registerItemRenderer(MWItems.MACHINE_PISTOL, new BaseGunRenderer<>(new PistolItemModel()));
        GeoItemRenderer.registerItemRenderer(MWItems.LMG, new BaseGunRenderer<>(new LMGItemModel()));
        GeoItemRenderer.registerItemRenderer(MWItems.HEAVY_CANNON, new BaseGunRenderer<>(new HeavyCannonItemModel()));

        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(MeatWeapons.AIRTRUCK, AirtruckEntityRenderer::new);

    }

    @Override
    public void onInitializeClient()
    {
        BeamPacket.Client.registerReceiver();
        registerEntityModels();
        registerAnimations();
        MWParticles.initClient();
        MWKeys.registerKeybinds();
        AirtruckSoundInstance.initEvent();
    }

}
