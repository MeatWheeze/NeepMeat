package com.neep.neepmeat.machine.small_compressor;

import com.neep.meatlib.inventory.ImplementedInventory;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMEntities;
import com.neep.neepmeat.init.NMItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SmallCompressorMinecart extends AbstractMinecartEntity implements NamedScreenHandlerFactory
{
    private final ImplementedInventory inventory = ImplementedInventory.ofSize(1);
    private final BurnerBehaviour burner = new BurnerBehaviour(inventory, this::updateBlockState);

    private void updateBlockState(boolean burning)
    {
        BlockState state = getContainedBlock();
        if (state.getBlock() instanceof SmallCompressorBlock)
        {
            setCustomBlock(state.with(SmallCompressorBlock.LIT, burning));
        }
    }

    public SmallCompressorMinecart(EntityType<?> entityType, World world)
    {
        super(entityType, world);
    }

    public SmallCompressorMinecart(World world, double x, double y, double z)
    {
        super(NMEntities.SMALL_COMPRESSOR_MINECART, world, x, y, z);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        inventory.writeNbt(nbt);
        burner.writeNbt(nbt);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        inventory.readNbt(nbt);
        burner.readNbt(nbt);
    }

    @Override
    public void tick()
    {
        if (!getWorld().isClient())
        {
            burner.tick();
        }
    }

    @Override
    public BlockState getDefaultContainedBlock()
    {
        return NMBlocks.SMALL_COMPRESSOR.getDefaultState();
    }

    @Override
    protected Item getItem()
    {
        return NMItems.SMALL_COMPRESSOR_MINECART;
    }

    @Override
    public Type getMinecartType()
    {
        return null;
    }

    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand)
    {
        player.openHandledScreen(this);
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand)
    {
        return super.interact(player, hand);
    }

    @Override
    public ItemStack getPickBlockStack()
    {
        return NMItems.SMALL_COMPRESSOR_MINECART.getDefaultStack();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player)
    {
        return new SmallCompressorScreenHandler(playerInventory, inventory, syncId, burner.getDelegate());
    }
}
