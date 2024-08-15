package com.neep.neepbus.client.item;

import com.neep.meatlib.client.event.CrosshairRenderEvent;
import com.neep.meatlib.client.event.ScrollEvents;
import com.neep.meatlib.client.event.UseAttackCallback;
import com.neep.neepbus.NeepBus;
import com.neep.neepbus.block.NeepBusProvider;
import com.neep.neepbus.network.NeepBusNetwork;
import com.neep.neepbus.util.ConfigEntry;
import com.neep.neepbus.util.NeepBusConfig;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.Window;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public class NetworkingToolClient
{
    @Nullable private static StoredConfig CONFIG;

    private static Mode mode = Mode.SENDER;
    @Nullable private static StoredAddress address;

    private static int selected;

    public static void init()
    {
        ClientTickEvents.END_CLIENT_TICK.register(client ->
        {
            if (client.world != null && client.player != null )
            {
                ItemStack mainStack = client.player.getMainHandStack();
                if (mainStack.isOf(NeepBus.NETWORKING_TOOL))
                {
                    tick(client);
                }
                else
                    CONFIG = null;
            }
        });

        HudRenderCallback.EVENT.register((drawContext, tickDelta) ->
        {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world != null && client.player != null )
            {
                ItemStack mainStack = client.player.getMainHandStack();
                if (mainStack.isOf(NeepBus.NETWORKING_TOOL))
                {
                    renderHud(drawContext, client, tickDelta);
                }
            }
        });

        ScrollEvents.PRE_SCROLL.register((window, amount) ->
        {
            MinecraftClient client = MinecraftClient.getInstance();

            // Ignore if the player has a screen open.
            if (client.world != null && client.player != null && client.getOverlay() == null && client.currentScreen == null)
            {
                if (CONFIG != null)
                {
                    int newSelected = clampSelection(selected + (int) Math.round(amount), CONFIG.config());
                    if (newSelected != selected)
                    {
                        selected = newSelected;
                        client.getSoundManager().play(new PositionedSoundInstance(NMSounds.CLICK, SoundCategory.PLAYERS,
                                1, 1, SoundInstance.createRandom(),
                                client.player.getX(), client.player.getY(), client.player.getZ()));
                    }
                    return true;
                }
            }
            return false;
        });

        UseAttackCallback.DO_USE.register((client, player) ->
        {
            if (override() && player.getMainHandStack().isOf(NeepBus.NETWORKING_TOOL))
            {
                if (CONFIG == null && address != null)
                    clear();
                else if (CONFIG != null)
                    configClick(client, CONFIG.blockName(), CONFIG.config());

                UseAttackCallback.swingHand(client, Hand.MAIN_HAND);

                return false;
            }
            return true;
        });

        CrosshairRenderEvent.EVENT.register((context, player) -> override() && player.getMainHandStack().isOf(NeepBus.NETWORKING_TOOL));
    }

    private static void clear()
    {
        mode = Mode.SENDER;
        address = null;
    }

    private static void configClick(MinecraftClient client, Text blockName, NeepBusConfig config)
    {
        selected = clampSelection(selected, config);

        if (address == null)
        {
            if (mode == Mode.SENDER)
            {
                var entries = config.getOutputs();
                if (!entries.isEmpty())
                {
                    ConfigEntry entry = config.getOutputs().get(selected);
                    address = new StoredAddress(blockName, entry);
                    mode = Mode.RECEIVER;

                    client.getSoundManager().play(new PositionedSoundInstance(NMSounds.UI_BEEP, SoundCategory.PLAYERS,
                            1, 1, SoundInstance.createRandom(),
                            client.player.getX(), client.player.getY(), client.player.getZ()));
                }
            }
            // Add handling for RECEIVER
        }
        else
        {
            if (mode == Mode.RECEIVER)
            {
                var entries = config.getInputs();
                if (!entries.isEmpty())
                {
                    if (client.crosshairTarget instanceof BlockHitResult blockHitResult)
                    {
                        NeepBusNetwork.NT_CONNECT.emitter(client.player)
                                .send(blockHitResult.getBlockPos(), false, selected, address.address());

                        client.getSoundManager().play(new PositionedSoundInstance(NMSounds.UI_BEEP, SoundCategory.PLAYERS,
                                1, 1, SoundInstance.createRandom(),
                                client.player.getX(), client.player.getY(), client.player.getZ()));
                    }

                    mode = Mode.SENDER;
                    address = null;
                }
            }
            // Add handling for SENDER
        }
    }

    private static void renderHud(DrawContext context, MinecraftClient client, float tickDelta)
    {
        Window window = client.getWindow();
        int wWidth = window.getScaledWidth();
        int wHeight = window.getScaledHeight();
        TextRenderer textRenderer = client.textRenderer;

        int entryHeight = 20;
        int entryStride = entryHeight + 1;
        int entryWidth = 120;

        if (address != null)
        {
            int xStart = wWidth / 2 - entryWidth - 10;
            int yStart = (wHeight - entryStride) / 2;
            renderEntry(context, textRenderer, address, true, xStart, yStart, entryWidth, entryHeight);

            renderHeader(context, textRenderer, address.blockName(), xStart, yStart - entryStride + 5, entryWidth, entryHeight);
        }


        if (address != null && CONFIG != null)
        {
            // Render right panel
            if (mode == Mode.RECEIVER)
            {
                List<? extends ConfigEntry> inputs = CONFIG.config().getInputs();

                int idx = MathHelper.clamp(selected, 0, inputs.size() - 1);

                int xStart = wWidth / 2 + 10; // Subtract to leave room for crosshairs
                int yStart = (wHeight - entryStride) / 2
                        + idx * entryStride;

                int i = 0;
                for (i = 0; i < inputs.size(); ++i)
                {
                    int y = yStart - i * entryStride;
                    ConfigEntry entry = inputs.get(i);
                    renderEntry(context, textRenderer, entry, i == idx, xStart, y, entryWidth, entryHeight);
                }

                int labelY = yStart - i * entryStride + 5;
                renderHeader(context, textRenderer, CONFIG.blockName(), xStart, labelY, entryWidth, entryHeight);
            }
        }

        if (address == null && CONFIG != null)
        {
            // Render left panel
            if (mode == Mode.SENDER)
            {
                List<? extends ConfigEntry> outputs = CONFIG.config().getOutputs();

                int idx = MathHelper.clamp(selected, 0, outputs.size() - 1);

                int xStart = wWidth / 2 - entryWidth - 10; // Subtract to leave room for crosshairs
                int yStart = (wHeight - entryStride) / 2
                        + idx * entryStride;

                int i = 0;
                for (i = 0; i < outputs.size(); ++i)
                {
                    int y = yStart - i * entryStride;
                    ConfigEntry entry = outputs.get(i);
                    renderEntry(context, textRenderer, entry, i == idx, xStart, y, entryWidth, entryHeight);
                }

                int labelY = yStart - i * entryStride + 5;
                renderHeader(context, textRenderer, CONFIG.blockName(), xStart, labelY, entryWidth, entryHeight);
            }
        }

        if (override())
        {
            String arrow = "→";
            GUIUtil.drawText(context, textRenderer, arrow,
                    (wWidth - textRenderer.getWidth(arrow)) / 2f,
                    (wHeight - textRenderer.fontHeight) / 2f + 1,
                    PLCCols.TEXT.col, true);
        }

    }

    private static void renderEntry(DrawContext context, TextRenderer textRenderer, ConfigEntry entry, boolean selected, int x, int y, int w, int h)
    {
        int col = selected ? PLCCols.SELECTED.col : PLCCols.TEXT.col;
        GUIUtil.fill(context, x, y, x + w, y + h, 0x90000000);
        GUIUtil.renderBorderInner(context, x, y, w, h, col, 0);
        GUIUtil.drawText(context, textRenderer, entry.getName(), x + 2, y + 2, col, true);
        GUIUtil.drawText(context, textRenderer, "→", x + 2, y + 2 + textRenderer.fontHeight, PLCCols.INVALID.col, false);

        String address = entry.getAddress();
        int addressWidth = textRenderer.getWidth(address);
        GUIUtil.drawText(context, textRenderer, address, x + w - addressWidth - 2, y + textRenderer.fontHeight + 2, PLCCols.TEXT.col, true);
    }

    private static void renderHeader(DrawContext context, TextRenderer textRenderer, Text name, int x, int y, int w, int h)
    {
        GUIUtil.drawCenteredText(context, textRenderer, name,
                x + w / 2f, y,
                PLCCols.TEXT.col, true);

        GUIUtil.drawHorizontalLine1(context, x, x + w, y + 10, PLCCols.BORDER.col);
    }

    private static void tick(MinecraftClient client)
    {
        updateConfig(client);

        updateSelection();
    }

    private static void updateSelection()
    {
        selected = clampSelection(selected, CONFIG != null ? CONFIG.config : null);
    }

    private static int clampSelection(int selected, NeepBusConfig config)
    {
        if (config != null)
        {
            return switch (mode)
            {
                case SENDER -> MathHelper.clamp(selected, 0, config.getOutputs().size() - 1);
                case RECEIVER -> MathHelper.clamp(selected, 0, config.getInputs().size() - 1);
            };
        }
        return 0;
    }

    private static void updateConfig(MinecraftClient client)
    {
        if (client.crosshairTarget instanceof BlockHitResult hitResult)
        {
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = client.world.getBlockState(pos);
            if (state.getBlock() instanceof NeepBusProvider provider)
            {
                @Nullable NeepBusConfig config = provider.getConfig(client.world, pos, state);
                if (config != null)
                {
                    CONFIG = new StoredConfig(state.getBlock().getName(), config);
                    return;
                }
            }
        }
        CONFIG = null;
    }

//    private static boolean renderOutline(WorldRenderContext context, @Nullable HitResult hitResult)
//    {
//        MinecraftClient client = MinecraftClient.getInstance();
//        Camera camera = client.gameRenderer.getCamera();
//
//        ItemStack stack = client.player.getMainHandStack();
//        if (stack.isOf(NeepBus.NETWORKING_TOOL))
//        {
//            NbtCompound nbt = stack.getSubNbt("networking");
//            if (nbt != null && nbt.contains("first"))
//            {
//                BlockPos first = NbtHelper.toBlockPos(nbt.getCompound("first"));
//
//                Vec3d camPos = camera.getPos();
//                BlockState targetState = client.world.getBlockState(first);
//                VoxelShape shape = targetState.getOutlineShape(client.world, first, ShapeContext.of(client.player));
//
//                drawCuboidShapeOutline(
//                        context.matrixStack(),
//                        context.consumers().getBuffer(RenderLayer.getLines()),
//                        shape,
//                        first.getX() - camPos.x,
//                        first.getY() - camPos.y,
//                        first.getZ() - camPos.z,
//                        1, 0.36f, 0.13f, 0.8f
//                );
//            }
//        }
//        return true;
//    }

    public static boolean override()
    {
        return CONFIG != null || address != null;
    }

    public enum Mode
    {
        SENDER,
        RECEIVER
    }

    private record StoredConfig(Text blockName, NeepBusConfig config)
    {

    }

    private record StoredAddress(Text blockName, String name, String address) implements ConfigEntry
    {
        public StoredAddress(Text blockName, ConfigEntry entry)
        {
            this(blockName, entry.getName(), entry.getAddress());
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public String getAddress()
        {
            return address;
        }

        @Override
        public void setAddress(String address)
        {

        }
    }
}
