package com.neep.neepmeat.client.screen;

import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.network.MachineDiagnosticsRequest;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class MachineHudOverlay
{
    static MachineHudOverlay INSTANCE = new MachineHudOverlay();

    public static MachineHudOverlay getInstance()
    {
        return INSTANCE;
    }

    protected MotorisedBlock.Diagnostics diagnostics = null;
    protected int updateInterval = 10;
    protected int updateCounter = 0;

    public MachineHudOverlay()
    {
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) ->
        {
            getInstance().update(MinecraftClient.getInstance(), MinecraftClient.getInstance().world);
            getInstance().onRender(MinecraftClient.getInstance(), matrixStack, tickDelta);
        });
    }

    public void receive(MotorisedBlock.Diagnostics diagnostics)
    {
        this.diagnostics = diagnostics;
    }

    public void update(MinecraftClient client, ClientWorld world)
    {
        updateCounter = Math.min(updateInterval, updateCounter + 1);
        if (world == null || client.player == null || updateCounter < updateInterval)
        {
            return;
        }

        updateCounter = 0;

        if (client.crosshairTarget != null && client.crosshairTarget instanceof BlockHitResult result)
        {
            BlockPos pos = result.getBlockPos();
            if (world.getBlockEntity(pos) instanceof MotorisedBlock.DiagnosticsProvider)
            {
                MachineDiagnosticsRequest.Client.send(world, pos);
            }
            else
            {
                diagnostics = null;
            }
        }
    }

    public void onRender(MinecraftClient client, MatrixStack matrixStack, float tickDelta)
    {
        if (diagnostics != null)
        {
            TextRenderer textRenderer = client.textRenderer;

            float width = client.getWindow().getScaledWidth();
            float height = client.getWindow().getScaledHeight();

            {
                boolean problem = diagnostics.problem();
                if (problem)
                {
                    Text t = diagnostics.title();
                    Text m = diagnostics.message();

                    int textWidth = textRenderer.getWidth(t);
                    int textHeight = textRenderer.fontHeight;

                    textRenderer.drawWithShadow(matrixStack, t,
                            (width - textWidth) / 2, (height + 1.5f * textHeight) / 2, Color.RED.getRGB());

                    textWidth = textRenderer.getWidth(m);
                    textRenderer.drawWithShadow(matrixStack, m,
                            (width - textWidth) / 2, (height + 3.5f * textHeight) / 2, Color.RED.getRGB());
                }
            }
        }
    }

    public static void init()
    {
    }
}
