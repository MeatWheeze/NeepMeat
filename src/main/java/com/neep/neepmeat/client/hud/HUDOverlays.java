package com.neep.neepmeat.client.hud;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.item.CompoundInjectorItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class HUDOverlays
{
    public final MinecraftClient client;
    private static final Identifier INJECTOR_VIGNETTE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/heal_vignette.png");
    private static double scaledHeight;
    private static double scaledWidth;
    private static float opacity;
    private static int vignetteState;

    public HUDOverlays(MinecraftClient client)
    {
        this.client = client;
    }

    public static void renderVignettes(MatrixStack stack)
    {
        RenderSystem.setShader(GameRenderer::getPositionShader);
        MinecraftClient client = MinecraftClient.getInstance();
        scaledWidth = client.getWindow().getScaledWidth();
        scaledHeight = client.getWindow().getScaledHeight();
        ItemStack itemStack1 = client.player.getMainHandStack();
        ItemStack itemStack2 = client.player.getOffHandStack();

        for (ItemStack itemStack : ImmutableList.of(itemStack1, itemStack2))
        {
            if (itemStack.getItem() instanceof CompoundInjectorItem && CompoundInjectorItem.getHealsRemaining(itemStack) == ((CompoundInjectorItem) NMItems.COMPOUND_INJECTOR).getHealsFor())
            {
                vignetteState = 1;
                break;
            }
        }

        float f = client.getLastFrameDuration();
        if (vignetteState == 1)
        {
            opacity = (float) MathHelper.lerp(0.8F * f, opacity, 1.00F);
            renderOverlay(INJECTOR_VIGNETTE, opacity, 1.1f);
            if (opacity > 0.99)
                vignetteState = 2;
        }
        if (vignetteState == 2)
        {
            opacity = (float) MathHelper.lerp(0.1F * f, opacity, 0.0F);
            renderOverlay(INJECTOR_VIGNETTE, opacity, 1.1f);
            if (opacity < 0.001)
            {
                vignetteState = 0;
                opacity = 0;
            }
        }
    }

    private static void renderOverlay(Identifier texture, float opacity, float scale) {
        double midx = scaledWidth / 2;
        double midy = scaledHeight / 2;
        double height = scaledHeight * scale;
        double width = scaledWidth * scale;
        double x1 = midx - width / 2;
        double x2 = midx + width / 2;
        double x3 = x2;
        double x4 = x1;
        double y1 = midy + height / 2;
        double y3 = midy - height / 2;

        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, opacity);
        RenderSystem.setShaderTexture(0, texture);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        bufferBuilder.vertex(x1, y1, -90.0D).texture(0.0F, 1.0F).next();
        bufferBuilder.vertex(x2, y1, -90.0D).texture(1.0F, 1.0F).next();
        bufferBuilder.vertex(x3, y3, -90.0D).texture(1.0F, 0.0F).next();
        bufferBuilder.vertex(x4, y3, -90.0D).texture(0.0F, 0.0F).next();

        tessellator.draw();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void init()
    {
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> renderVignettes(matrixStack));
    }
}
