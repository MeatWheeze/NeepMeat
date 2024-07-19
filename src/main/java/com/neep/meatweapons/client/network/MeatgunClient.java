package com.neep.meatweapons.client.network;

import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.init.MWComponents;
import com.neep.meatweapons.item.meatgun.Meatgun;
import com.neep.meatweapons.item.meatgun.MeatgunAnimationManager;
import com.neep.meatweapons.meatgun.AmmunitionType;
import com.neep.meatweapons.meatgun.RootModuleHolder;
import com.neep.meatweapons.meatgun.module.AmmunitionStoringModule;
import com.neep.meatweapons.network.MeatgunNetwork;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;

import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class MeatgunClient
{
    public static void init()
    {
        ClientPlayNetworking.registerGlobalReceiver(MeatgunNetwork.CHANNEL, (client, handler, buf, responseSender) ->
        {
            MeatgunNetwork.RecoilDirection direction = MeatgunNetwork.RecoilDirection.values()[buf.readInt()];
            float amount = buf.readFloat();
            float horAmount = buf.readFloat();
            float returnSpeed = buf.readFloat();
            float horReturnSpeed = buf.readFloat();

            client.execute(() ->
            {
                MeatgunComponent component = MWComponents.MEATGUN.getNullable(client.player.getMainHandStack());
                if (component != null)
                {
                    component.getRecoil().set(direction, amount, horAmount, returnSpeed, horReturnSpeed);
                }
            });
        });

        MeatgunNetwork.SEND_ANIMATION.receiver(MeatgunClient::receiveAnimation);

        HudRenderCallback.EVENT.register((drawContext, tickDelta) ->
        {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            if (player.getMainHandStack().getItem() instanceof Meatgun)
            {
                MeatgunComponent component1 = MWComponents.MEATGUN.getNullable(player.getMainHandStack());
//                MeatgunComponent component2 = MWComponents.MEATGUN.getNullable(player.getOffHandStack());
                if (component1 != null)
                {
                    renderHud(drawContext, tickDelta, component1.getRootHolder());
                }
//                if (component2 != null)
//                {
//                    MeatgunAnimationManager animationManager = component2.getAnimationManager();
//                    if (animationManager != null)
//                        animationManager.queue(name, buf);
//                }
            }
        });
    }

    private static void renderHud(DrawContext drawContext, float tickDelta, RootModuleHolder holder)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        Map<AmmunitionType, List<AmmunitionStoringModule>> map = holder.getAmmo();

        int lines = 0;
        int stride = textRenderer.fontHeight + 1;
        for (var value : map.values())
        {
            if (!value.isEmpty())
                lines++;
        }

        if (lines == 0)
            return;

        int height = lines * stride + 2;
        int yStart = client.getWindow().getScaledHeight() - height;

        MatrixStack matrices = drawContext.getMatrices();
        matrices.push();
        float sf = 0.8f;
        matrices.translate(0, client.getWindow().getScaledHeight() - height * sf, 0);
        matrices.scale(sf, sf, 1);
        drawContext.fill(0, 0, 100, height, 0x90000000);
        GUIUtil.renderBorderInner(drawContext, 0, 0, 100, height, PLCCols.BORDER.col, 0);

        int textY = 2;
        for (var entry : map.entrySet())
        {
            if (entry.getValue().isEmpty())
                continue;

            int amount = entry.getValue().stream().mapToInt(AmmunitionStoringModule::amount).sum();

            GUIUtil.drawText(drawContext, textRenderer, entry.getKey().name() + " " + amount, 2, textY, PLCCols.TEXT.col, true);
            textY += stride;
        }
        matrices.pop();
    }

    private static void receiveAnimation(String name, PacketByteBuf buf)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        MeatgunComponent component1 = MWComponents.MEATGUN.getNullable(client.player.getMainHandStack());
        MeatgunComponent component2 = MWComponents.MEATGUN.getNullable(client.player.getOffHandStack());
        if (component1 != null)
        {
            MeatgunAnimationManager animationManager = component1.getAnimationManager();
            if (animationManager != null)
                animationManager.queue(name, buf);
        }
        if (component2 != null)
        {
            MeatgunAnimationManager animationManager = component2.getAnimationManager();
            if (animationManager != null)
                animationManager.queue(name, buf);
        }
    }
}
