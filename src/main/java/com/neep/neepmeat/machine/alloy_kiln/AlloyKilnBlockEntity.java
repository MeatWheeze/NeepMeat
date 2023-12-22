package com.neep.neepmeat.machine.alloy_kiln;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.IHeatable;
import com.neep.neepmeat.screen_handler.AlloyKilnScreenHandler;
import com.neep.neepmeat.screen_handler.StirlingEngineScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AlloyKilnBlockEntity extends SyncableBlockEntity implements IHeatable, NamedScreenHandlerFactory
{
    protected int fuelTime;
    protected int burnTime;
    protected int cookTime;
    protected int cookTimeTotal;

    protected AlloyKilnStorage storage;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate()
    {

        @Override
        public int get(int index)
        {
            switch (index)
            {
                case 0 -> {
                    return burnTime;
                }
                case 1 -> {
                    return fuelTime;
                }
                case 2 -> {
                    return cookTime;
                }
                case 3 -> {
                    return cookTimeTotal;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value)
        {
            switch (index)
            {
                case 0 -> burnTime = value;
                case 1 -> fuelTime = value;
                case 2 -> cookTime = value;
                case 3 -> cookTimeTotal = value;
            }
        }

        @Override
        public int size()
        {
            return 4;
        }
    };

    public AlloyKilnBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.storage = new AlloyKilnStorage(this);
    }

    public AlloyKilnBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.ALLOY_KILN, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, AlloyKilnBlockEntity be)
    {

    }

    @Override
    public void setBurning()
    {

    }

    @Override
    public void updateState(World world, BlockPos pos, BlockState oldState)
    {

    }

    @Override
    public int getCurrentBurnTime()
    {
        return 0;
    }

    @Override
    public Text getDisplayName()
    {
        return new TranslatableText("container." + NeepMeat.NAMESPACE + ".alloy_kiln");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
    {
        return new AlloyKilnScreenHandler(syncId, inv, storage.inventory, propertyDelegate);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putShort("fuelTime", (short) fuelTime);
        nbt.putShort("burnTime", (short) burnTime);
        nbt.putShort("cookTime", (short) cookTime);
        nbt.putShort("cookTimeTotal", (short) cookTimeTotal);
        storage.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.fuelTime = nbt.getShort("fuelTime");
        this.burnTime = nbt.getShort("burnTime");
        this.cookTime = nbt.getShort("cookTime");
        this.cookTimeTotal = nbt.getShort("cookTimeTotal");
        storage.readNbt(nbt);
    }

    public AlloyKilnStorage getStorage()
    {
        return storage;
    }
}
