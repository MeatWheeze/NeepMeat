package com.neep.neepmeat.client.hud;

import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.plc.PLCCols;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static com.neep.neepmeat.client.plc.PLCHudRenderer.drawCuboidShapeOutline;

public class BigBlockPlacementHelper
{
    public static void init()
    {
        WorldRenderEvents.BLOCK_OUTLINE.register(BigBlockPlacementHelper::onRender);
        ClientTickEvents.START_CLIENT_TICK.register(client ->
        {
            if (client.world != null && client.player != null)
                tick(client);
        });
    }

    @Nullable
    private static Result RESULT;

    private static void tick(MinecraftClient client)
    {
        ItemStack mainStack = client.player.getMainHandStack();
        if (mainStack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof BigBlock<?> bigBlock
                && client.crosshairTarget instanceof BlockHitResult hitResult)
        {
            ItemPlacementContext itemPlacementContext = new ItemPlacementContext(client.player, client.player.preferredHand, mainStack, hitResult);
            BlockPos pos = itemPlacementContext.getBlockPos();

            boolean place = itemPlacementContext.canPlace();

            BlockState placementState = bigBlock.getPlacementState(itemPlacementContext);
            place = place && bigBlock.canPlaceAt(placementState, client.world, pos);

            RESULT = new Result(
                    bigBlock.getVolume(placementState).toVoxelShape(),
//                    bigBlock.getOutlineShape(placementState, client.world, pos, ShapeContext.absent()),
                    pos,
                    place ? PLCCols.BORDER.col : PLCCols.ERROR_LINE.col);

            return;
        }
        RESULT = null;
    }

    private static boolean onRender(WorldRenderContext wrctx, WorldRenderContext.BlockOutlineContext blockOutlineContext)
    {
        if (RESULT != null)
        {
            MinecraftClient client = MinecraftClient.getInstance();
            BlockPos pos = RESULT.pos();

            Vec3d camPos = client.gameRenderer.getCamera().getPos();

            int col = RESULT.col();

            drawCuboidShapeOutline(
                    wrctx.matrixStack(),
                    wrctx.consumers().getBuffer(RenderLayer.getLines()),
                    RESULT.shape(),
                    pos.getX() - camPos.x,
                    pos.getY() - camPos.y,
                    pos.getZ() - camPos.z,
                    ((col >> 16) & 0xff) / 255f, ((col >> 8) & 0xff) / 255f, ((col >> 0) & 0xff) / 255f, 1
            );
            return false;
        }
        return true;
    }

    private static record Result(VoxelShape shape, BlockPos pos, int col)
    {

    }
}
