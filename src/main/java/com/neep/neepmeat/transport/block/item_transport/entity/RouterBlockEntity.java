package com.neep.neepmeat.transport.block.item_transport.entity;

import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.inventory.ImplementedInventory;
import com.neep.neepmeat.transport.machine.item.RouterInventory;
import com.neep.neepmeat.screen_handler.RouterScreenHandler;
import com.neep.neepmeat.transport.item_network.ItemInPipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class RouterBlockEntity extends BlockEntity implements NamedScreenHandlerFactory
{
    public ImplementedInventory inventory = new RouterInventory();
    public static Direction[] DIRECTIONS = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN};

    public RouterBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.ROUTER, pos, state);
    }

    public RouterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public Text getDisplayName()
    {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
    {
        return new RouterScreenHandler(syncId, inv, this.inventory);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        inventory.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        inventory.readNbt(nbt);
    }

    @Nullable
    public Direction getOutputDirection(ItemInPipe item)
    {
        for (int i = 0; i < 6; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                int index = i * 3 + j;
                ItemStack filterStack = inventory.getItems().get(index);
//                System.out.println(filterStack);
                if (filterStack.isOf(item.getItemStack().getItem()))
                {
                    return DIRECTIONS[i];
                }
            }
        }
        return null;
    }
}
