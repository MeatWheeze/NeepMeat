package com.neep.neepmeat.client.item;

import com.neep.neepmeat.item.ScutterItem;
import com.sun.source.tree.WhileLoopTree;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import static com.neep.neepmeat.client.plc.PLCHudRenderer.drawCuboidShapeOutline;

@Environment(EnvType.CLIENT)
public class FarmingScutterHudRenderer
{
    public static void init()
    {
        WorldRenderEvents.BLOCK_OUTLINE.register((wrctx, blockOutlineContext) ->
        {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player.getMainHandStack().getItem() instanceof ScutterItem<?> scutter && client.crosshairTarget instanceof BlockHitResult hitResult)
            {
                BlockPos pos = hitResult.getBlockPos();

                Vec3d camPos = client.gameRenderer.getCamera().getPos();
                BlockState targetState = client.world.getBlockState(pos);
                VoxelShape shape = targetState.getOutlineShape(client.world, pos, ShapeContext.of(client.player));

                drawCuboidShapeOutline(
                        wrctx.matrixStack(),
                        wrctx.consumers().getBuffer(RenderLayer.getLines()),
                        shape,
                        pos.getX() - camPos.x,
                        pos.getY() - camPos.y,
                        pos.getZ() - camPos.z,
                        1, 0.36f, 0.13f, 0.8f
                );
                return false;
            }
            return true;
        });
    }
}
