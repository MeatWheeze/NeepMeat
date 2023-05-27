package com.neep.meatweapons.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.meatlib.api.event.RenderItemGuiEvent;
import com.neep.meatlib.graphics.client.GraphicsEffectClient;
import com.neep.meatlib.attachment.player.PlayerAttachmentManager;
import com.neep.meatweapons.MWItems;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.client.model.*;
import com.neep.meatweapons.client.renderer.*;
import com.neep.meatweapons.client.sound.AirtruckSoundInstance;
import com.neep.meatweapons.client.sound.DrillSoundInstance;
import com.neep.meatweapons.item.BaseGunItem;
import com.neep.meatweapons.network.ProjectileSpawnPacket;
import com.neep.meatweapons.particle.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

@Environment(value= EnvType.CLIENT)
public class MWClient implements ClientModInitializer
{
    public static final EntityModelLayer MODEL_BULLET_LAYER = new EntityModelLayer(new Identifier(MeatWeapons.NAMESPACE, "bullet"), "main");
    public static final EntityModelLayer MODEL_CANNON_BULLET_LAYER = new EntityModelLayer(new Identifier(MeatWeapons.NAMESPACE, "cannon_bullet"), "main");

    public static final EntityModelLayer ZAP_LAYER = new EntityModelLayer(new Identifier(MeatWeapons.NAMESPACE, "zap"), "main");
    public static final EntityModelLayer FUSION_BLAST_LAYER = new EntityModelLayer(new Identifier(MeatWeapons.NAMESPACE, "fusion_blast"), "main");
    public static final EntityModelLayer MODEL_PLASMA_LAYER = new EntityModelLayer(new Identifier(MeatWeapons.NAMESPACE, "plasma"), "main");
    public static final EntityModelLayer MODEL_SHELL_LAYER = new EntityModelLayer(new Identifier(MeatWeapons.NAMESPACE, "shell"), "main");

    public static void registerEntityModels()
    {
        EntityRendererRegistry.INSTANCE.register(MeatWeapons.BULLET, BulletEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_BULLET_LAYER, BulletEntityModel::getTexturedModelData);

        EntityRendererRegistry.INSTANCE.register(MeatWeapons.CANNON_BULLET, CannonBulletEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_CANNON_BULLET_LAYER, CannonBulletEntityModel::getTexturedModelData);

        EntityRendererRegistry.INSTANCE.register(MeatWeapons.ZAP, ZapEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ZAP_LAYER, CannonBulletEntityModel::getTexturedModelData);

        EntityRendererRegistry.INSTANCE.register(MeatWeapons.FUSION_BLAST, PlasmaEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(FUSION_BLAST_LAYER, PlasmaEntityModel::getTexturedModelData);

        EntityRendererRegistry.INSTANCE.register(MeatWeapons.FUSION_BLAST, PlasmaEntityRenderer::new);
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
        GeoItemRenderer.registerItemRenderer(MWItems.MA75, new BaseGunRenderer<>(new BaseGunModel<>(new Identifier(MeatWeapons.NAMESPACE, "geo/ma75.geo.json"), new Identifier(MeatWeapons.NAMESPACE, "textures/general/ma75.png"), new Identifier(MeatWeapons.NAMESPACE, "animations/ma75.animation.json"))));
//        GeoItemRenderer.registerItemRenderer(MWItems.BLASTER, new BaseGunRenderer<>(new BaseGunModel<>(new Identifier(MeatWeapons.NAMESPACE, "geo/blaster.geo.json"), new Identifier(MeatWeapons.NAMESPACE, "textures/general/thingy.png"), new Identifier(MeatWeapons.NAMESPACE, "animations/blaster.animation.json"))));

        GeoItemRenderer.registerItemRenderer(MWItems.ASSAULT_DRILL, new DrillItemRenderer(new DrillItemModel()));

        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(MeatWeapons.AIRTRUCK, AirtruckEntityRenderer::new);

    }

    @Override
    public void onInitializeClient()
    {
        registerEntityModels();
        registerAnimations();
        MWParticles.initClient();
        MWKeys.registerKeybinds();
        AirtruckSoundInstance.initEvent();

        GraphicsEffectClient.registerEffect(MWGraphicsEffects.BEAM, BeamEffect::new);
        GraphicsEffectClient.registerEffect(MWGraphicsEffects.BULLET_TRAIL, BulletTrailEffect::new);
        GraphicsEffectClient.registerEffect(MWGraphicsEffects.ZAP, ZapBeamEffect::new);


        RenderItemGuiEvent.EVENT.register((textRenderer, stack, x, y, countLabel) ->
        {
            if (stack.getItem() instanceof BaseGunItem baseGunItem && baseGunItem.getShots(stack, 1) >= 0)
            {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferBuilder = tessellator.getBuffer();
                int i = stack.getItemBarStep();
                int j = stack.getItemBarColor();
                RenderItemGuiEvent.renderGuiQuad(bufferBuilder, x + 2, y + 15, 13, 1, 0, 0, 0, 255);
                RenderItemGuiEvent.renderGuiQuad(bufferBuilder, x + 2, y + 15, i, 1, j >> 16 & 0xFF, j >> 8 & 0xFF, j & 0xFF, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }
        });

        PlayerAttachmentManager.registerAttachment(DrillSoundInstance.ATTACHMENT_ID, DrillSoundInstance::new);
    }
}
