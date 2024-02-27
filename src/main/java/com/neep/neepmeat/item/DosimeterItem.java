package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.api.enlightenment.EnlightenmentManager;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.init.NMItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;

public class DosimeterItem extends BaseItem
{
    public DosimeterItem(String registryName, TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(registryName, tooltipSupplier, settings);
        ItemRegistry.queue(this);
    }

    @Environment(EnvType.CLIENT)
    public static class Client
    {
        private static final DecimalFormat DF = new DecimalFormat("###.#");

        public static void init()
        {
            HudRenderCallback.EVENT.register(Client::renderOverlay);
//            ClientTickEvents.START_CLIENT_TICK.
        }

        private static void onTick()
        {

        }

        private static void renderOverlay(DrawContext matrices, float tickDelta)
        {
            MinecraftClient client = MinecraftClient.getInstance();

            ItemStack stack = client.player.getMainHandStack();
            if (stack.isOf(NMItems.DOSIMETER))
            {
                EnlightenmentManager manager = NMComponents.ENLIGHTENMENT_MANAGER.get(client.player);
                int width = client.getWindow().getScaledWidth();
                int height = client.getWindow().getScaledHeight();

                Text text = Text.of("Dose per tick: " + DF.format(manager.lastDose())).copy().formatted(Formatting.YELLOW);
                int textWidth = client.textRenderer.getWidth(text);

                GUIUtil.drawText(matrices, client.textRenderer, text,
                        (float) (width - textWidth) / 2, height - 50, 0xFFFFFFFF, true);
            }
        }
    }
}
