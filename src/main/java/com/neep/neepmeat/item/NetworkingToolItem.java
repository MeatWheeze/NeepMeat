package com.neep.neepmeat.item;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.api.DataPort;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;
import java.util.Set;

import static com.neep.neepmeat.client.plc.PLCHudRenderer.drawCuboidShapeOutline;

public class NetworkingToolItem extends BaseItem
{
    public NetworkingToolItem(String registryName, TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(registryName, tooltipSupplier, settings.maxCount(1));
        ItemRegistry.queue(this);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context)
    {
        PlayerEntity player = context.getPlayer();
        if (player == null || context.getWorld().isClient())
            return super.useOnBlock(context);

        // Clear data
        if (context.getPlayer().isSneaking())
        {
            context.getStack().removeSubNbt("networking");

            return ActionResult.SUCCESS;
        }

        DataPort port = DataPort.DATA_PORT.find(context.getWorld(), context.getBlockPos(), null);

        if (port != null)
        {
            NbtCompound nbt = context.getStack().getOrCreateSubNbt("networking");

            if (!nbt.contains("first"))
            {
                nbt.put("first", NbtHelper.fromBlockPos(context.getBlockPos()));
                nbt.putString("first_name", context.getWorld().getBlockState(context.getBlockPos()).getBlock().getName().getString());
                BlockPos first = context.getBlockPos();
                context.getPlayer().sendMessage(Text.translatable(getTranslationKey() + ".from", first.getX(), first.getY(), first.getZ()));
            }
            else
            {
                BlockPos first = NbtHelper.toBlockPos(nbt.getCompound("first"));
                BlockPos second = context.getBlockPos();
                DataPort firstPort = DataPort.DATA_PORT.find(context.getWorld(), first, null);
                if (firstPort != null && findRoute(context.getWorld(), first, second, 20))
                {
                    firstPort.setTarget(second);
                    context.getPlayer().sendMessage(Text.translatable(getTranslationKey() + ".connected",
                            first.getX(), first.getY(), first.getZ(),
                            second.getX(), second.getY(), second.getZ()));
                    context.getStack().removeSubNbt("networking");
                }
                else
                {
                    player.sendMessage(Text.of("Connection failed: no route"), false);
                }
            }
            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(context);
    }

    private boolean findRoute(World world, BlockPos from, BlockPos to, int maxDist)
    {
        Queue<BlockPos> queue = Queues.newArrayDeque();
        Set<BlockPos> visited = Sets.newHashSet();
        queue.add(from);

        while (!queue.isEmpty())
        {
            BlockPos current = queue.poll();
            BlockPos.Mutable mutable = current.mutableCopy();
            for (Direction direction : Direction.values())
            {
                mutable.set(current, direction);

                if (mutable.equals(to))
                {
                    return true;
                }

                if (from.getManhattanDistance(mutable) > maxDist || visited.contains(mutable))
                    continue;

                if (world.getBlockState(mutable).isOf(NMBlocks.DATA_CABLE))
                {
                    queue.add(mutable.toImmutable());
                    visited.add(mutable.toImmutable());
                }
            }
        }
        return false;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        super.appendTooltip(itemStack, world, tooltip, tooltipContext);
        NbtCompound nbt = itemStack.getSubNbt("networking");
        if (nbt != null)
        {
            String name = nbt.getString("first_name");
            BlockPos pos = NbtHelper.toBlockPos(nbt.getCompound("first"));
            tooltip.add(Text.translatable(getTranslationKey() + ".from", pos.getX(), pos.getY(), pos.getZ()));
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Client
    {
        public static void init()
        {
            WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register(Client::renderOutline);
        }

        private static boolean renderOutline(WorldRenderContext context, @Nullable HitResult hitResult)
        {
            MinecraftClient client = MinecraftClient.getInstance();
            Camera camera = client.gameRenderer.getCamera();

            ItemStack stack = client.player.getMainHandStack();
            if (stack.isOf(NMItems.NETWORKING_TOOL))
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
}
