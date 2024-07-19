package com.neep.meatweapons.client.network;

import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.init.MWComponents;
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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;

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
            Hand hand = Hand.values()[buf.readVarInt()];

            client.execute(() ->
            {
                MeatgunComponent component;
                if (hand == Hand.MAIN_HAND)
                    component = MWComponents.MEATGUN.getNullable(client.player.getMainHandStack());
                else
                    component = MWComponents.MEATGUN.getNullable(client.player.getOffHandStack());

                if (component != null)
                {
                    component.getRecoil().set(direction, amount, horAmount, returnSpeed, horReturnSpeed);
                }
            });
        });

        MeatgunNetwork.SEND_ANIMATION.receiver(MeatgunClient::receiveAnimation);

        HudRenderCallback.EVENT.register((drawContext, tickDelta) ->
        {
//            MinecraftClient client = MinecraftClient.getInstance();
//            ClientPlayerEntity player = client.player;
//            if (player.getMainHandStack().getItem() instanceof Meatgun)
//            {
//                MeatgunComponent component1 = MWComponents.MEATGUN.getNullable(player.getMainHandStack());
//                MeatgunComponent component2 = MWComponents.MEATGUN.getNullable(player.getOffHandStack());
//                if (component1 != null)
//                {
//                    renderHud(drawContext, tickDelta, component1.getRootHolder());
//                }
//                if (component2 != null)
//                {
//                    MeatgunAnimationManager animationManager = component2.getAnimationManager();
//                    if (animationManager != null)
//                        animationManager.queue(name, buf);
//                }
//            }
        });
    }

    public static void renderHud(DrawContext drawContext, float tickDelta, RootModuleHolder holder)
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

        int col = PLCCols.BORDER.col & 0x88FFFFFF;

        int width = 60;
        int height = lines * stride + 2;
        MatrixStack matrices = drawContext.getMatrices();
        matrices.translate(0, 0, -0.01);
        drawContext.fill(-1, -1, width + 1, height + 1, 0x30000000);
        matrices.translate(0, 0, 0.01);
        GUIUtil.renderBorderInner(drawContext, 0, 0, width, height, col, 0);

        int textY = 2;
        matrices.push();
        matrices.scale(1, 1, 0.1f); // Squish down the text and shadow layers in Z
        for (var entry : map.entrySet())
        {
            if (entry.getValue().isEmpty())
                continue;

            int amount = entry.getValue().stream().mapToInt(AmmunitionStoringModule::amount).sum();

            int col2 = col;
            if (amount == 0)
                col2 = 0x88FF0000;

            GUIUtil.drawText(drawContext, textRenderer, entry.getKey().stortName() + " " + amount, 2, textY, col2, true);
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
