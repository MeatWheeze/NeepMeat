package com.neep.meatweapons.client;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.client.model.*;
import com.neep.meatweapons.client.renderer.BaseGunRenderer;
import com.neep.meatweapons.client.renderer.BulletEntityRenderer;
import com.neep.meatweapons.client.renderer.CannonBulletEntityRenderer;
import com.neep.meatweapons.client.renderer.PlasmaEntityRenderer;
import com.neep.meatweapons.network.BulletEntityPacket;
import com.neep.meatweapons.network.NetworkInitialiser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

import java.util.UUID;

public class MWClient implements ClientModInitializer
{
    public static final EntityModelLayer MODEL_BULLET_LAYER = new EntityModelLayer(new Identifier(MeatWeapons.NAMESPACE, "bullet"), "main");
    public static final EntityModelLayer MODEL_CANNON_BULLET_LAYER = new EntityModelLayer(new Identifier(MeatWeapons.NAMESPACE, "cannon_bullet"), "main");
    public static final EntityModelLayer MODEL_PLASMA_LAYER = new EntityModelLayer(new Identifier(MeatWeapons.NAMESPACE, "plasma"), "main");

    public static void registerEntityModels()
    {
        EntityRendererRegistry.INSTANCE.register(MeatWeapons.BULLET, BulletEntityRenderer::new);
        receiveEntityPacket();
        EntityModelLayerRegistry.registerModelLayer(MODEL_BULLET_LAYER, BulletEntityModel::getTexturedModelData);

        EntityRendererRegistry.INSTANCE.register(MeatWeapons.CANNON_BULLET, CannonBulletEntityRenderer::new);
        receiveEntityPacket();
        EntityModelLayerRegistry.registerModelLayer(MODEL_CANNON_BULLET_LAYER, CannonBulletEntityModel::getTexturedModelData);

        EntityRendererRegistry.INSTANCE.register(MeatWeapons.PLASMA, PlasmaEntityRenderer::new);
        receiveEntityPacket();
        EntityModelLayerRegistry.registerModelLayer(MODEL_PLASMA_LAYER, PlasmaEntityModel::getTexturedModelData);
    }

    public static void registerAnimations()
    {
        GeoItemRenderer.registerItemRenderer(MeatWeapons.HAND_CANNON, new BaseGunRenderer<>(new HandCannonItemModel()));
        GeoItemRenderer.registerItemRenderer(MeatWeapons.FUSION_CANNON, new BaseGunRenderer<>(new FusionCannonItemModel()));
        GeoItemRenderer.registerItemRenderer(MeatWeapons.MACHINE_PISTOL, new BaseGunRenderer<>(new PistolItemModel()));
    }

    @Override
    public void onInitializeClient()
    {
        registerEntityModels();
        registerAnimations();
    }

    public static void receiveEntityPacket()
    {
        ClientPlayNetworking.registerGlobalReceiver(NetworkInitialiser.SPAWN_ID, (client, handler, byteBuf, responseSender) ->
        {
            EntityType<?> et = Registry.ENTITY_TYPE.get(byteBuf.readVarInt());
            UUID uuid = byteBuf.readUuid();
            int entityId = byteBuf.readVarInt();
            Vec3d pos = BulletEntityPacket.PacketBufUtil.readVec3d(byteBuf);
            float pitch = BulletEntityPacket.PacketBufUtil.readAngle(byteBuf);
            float yaw = BulletEntityPacket.PacketBufUtil.readAngle(byteBuf);
            client.execute(() ->
            {
                if (MinecraftClient.getInstance().world == null)
                    throw new IllegalStateException("Tried to spawn entity in a null world!");
                Entity e = et.create(MinecraftClient.getInstance().world);
                if (e == null)
                    throw new IllegalStateException("Failed to create instance of entity \"" + Registry.ENTITY_TYPE.getId(et) + "\"!");
                e.updateTrackedPosition(pos);
                e.setPos(pos.x, pos.y, pos.z);
                e.setPitch(pitch);
                e.setYaw(yaw);
                e.setId(entityId);
                e.setUuid(uuid);
                MinecraftClient.getInstance().world.addEntity(entityId, e);
            });
        });
    }
}
