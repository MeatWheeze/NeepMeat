package com.neep.neepmeat.client.hud;

import com.neep.meatlib.client.api.event.AppendTooltipEvent;
import com.neep.meatlib.item.MeatlibItemExtension;
import com.neep.neepmeat.client.screen.tablet.GuideMainScreen;
import com.neep.neepmeat.datagen.tag.NMTags;
import com.neep.neepmeat.guide.GuideNode;
import com.neep.neepmeat.guide.GuideReloadListener;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.item.ProjectorItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@Environment(EnvType.CLIENT)
public class GuideLookupThings
{
    public static ItemStack NEXT_LOOKUP = ItemStack.EMPTY;

    public static void init()
    {
//        RenderItemGuiEvent.EVENT.register(GuideTooltipThings::onRender);

        ClientTickEvents.START_CLIENT_TICK.register(GuideLookupThings::onStartTick);
        AppendTooltipEvent.EVENT.register(GuideLookupThings::onAppendTooltip);
    }

    private static void onStartTick(MinecraftClient client)
    {
        if (!NEXT_LOOKUP.isEmpty())
        {
            if (!client.player.getInventory().contains(NMTags.GUIDE_LOOKUP))
            {
                NEXT_LOOKUP = ItemStack.EMPTY;
                return;
            }

            List<GuideNode> path = GuideReloadListener.getInstance().getPath(NEXT_LOOKUP);
            if (path != null)
            {
                GuideMainScreen screen = ProjectorItem.Client.openScreen();
                for (var node : path)
                {
                    node.visitScreen(screen);
                }
            }
            NEXT_LOOKUP = ItemStack.EMPTY;
        }
    }

    private static void onAppendTooltip(ItemStack stack, @Nullable World world, TooltipContext context)
    {
        if (((MeatlibItemExtension) stack.getItem()).meatlib$supportsGuideLookup() &&
                Screen.hasControlDown() && InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_L))
        {
            GuideLookupThings.NEXT_LOOKUP = stack.copy();
        }
    }

//    private static void onRender(DrawContext context, TextRenderer textRenderer, ItemStack stack, int x, int y)
//    {
//        MinecraftClient client = MinecraftClient.getInstance();
//        if (client.player == null || client.player.currentScreenHandler == null)
//            return;
//
//        ItemStack cursorStack = client.player.currentScreenHandler.getCursorStack();
//
//        if (!stack.isOf(NMItems.PROJECTOR) || !cursorStack.equals(stack))
//            return;
//
////        client.player.currentScreenHandler.click
//        context.drawTooltip(textRenderer, Text.of("GROMMY"), x, y);
//    }
}
