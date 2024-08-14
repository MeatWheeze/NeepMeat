package com.neep.neepbus.client.item;

import com.neep.neepbus.NeepBus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static com.neep.neepmeat.client.plc.PLCHudRenderer.drawCuboidShapeOutline;

@Environment(EnvType.CLIENT)
public class NetworkingToolClient
{
    public static void init()
    {
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register(NetworkingToolClient::renderOutline);

        HudRenderCallback.EVENT.register((drawContext, tickDelta) ->
        {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world != null && client.player != null )
            {
                ItemStack mainStack = client.player.getMainHandStack();
                if (mainStack.isOf(NeepBus.NETWORKING_TOOL))
                {
                    renderHud(mainStack, drawContext, tickDelta);
                }
            }
        });
    }

    private static void renderHud(ItemStack stack, DrawContext context, float tickDelta)
    {

    }

    private static boolean renderOutline(WorldRenderContext context, @Nullable HitResult hitResult)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        Camera camera = client.gameRenderer.getCamera();

        ItemStack stack = client.player.getMainHandStack();
        if (stack.isOf(NeepBus.NETWORKING_TOOL))
        {
            NbtCompound nbt = stack.getSubNbt("networking");
            if (nbt != null && nbt.contains("first"))
            {
                BlockPos first = NbtHelper.toBlockPos(nbt.getCompound("first"));

                Vec3d camPos = camera.getPos();
                BlockState targetState = client.world.getBlockState(first);
                VoxelShape shape = targetState.getOutlineShape(client.world, first, ShapeContext.of(client.player));

                drawCuboidShapeOutline(
                        context.matrixStack(),
                        context.consumers().getBuffer(RenderLayer.getLines()),
                        shape,
                        first.getX() - camPos.x,
                        first.getY() - camPos.y,
                        first.getZ() - camPos.z,
                        1, 0.36f, 0.13f, 0.8f
                );
            }
        }
        return true;
    }
}
