package com.neep.neepmeat.machine.small_compressor;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.inventory.ImplementedInventory;
import com.neep.neepmeat.component.CompressedAirComponent;
import com.neep.neepmeat.init.NMComponents;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmallCompressorBlockEntity extends SyncableBlockEntity implements NamedScreenHandlerFactory
{
    private final ImplementedInventory inventory = ImplementedInventory.ofSize(1);

    private int burnTime;
    private int maxBurnTime;

    private final Delegate delegate = new Delegate();

    public SmallCompressorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void serverTick()
    {
        tickBurn();

        if (burnTime > 0)
        {
            Box box = Box.of(Vec3d.ofCenter(pos), 16, 8, 16);

            List<PlayerEntity> players = world.getEntitiesByClass(PlayerEntity.class, box, p -> true);

            players.forEach(player ->
            {
                CompressedAirComponent component = NMComponents.COMPRESSED_AIR.getNullable(player);

                if (component != null)
                    component.insertAir(10);
            });
        }
    }

    private void tickBurn()
    {
        burnTime = Math.max(0, burnTime - 1);

        if (burnTime == 0)
        {
            Integer time = FuelRegistry.INSTANCE.get(inventory.getStack(0).getItem());
            if (time != null)
            {
                maxBurnTime = time;
                burnTime = maxBurnTime;
                inventory.getStack(0).decrement(1);
                world.setBlockState(pos, getCachedState().with(SmallCompressorBlock.LIT, true));
            }
            else
            {
                world.setBlockState(pos, getCachedState().with(SmallCompressorBlock.LIT, false));
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("max_burn_time", maxBurnTime);
        nbt.putInt("burn_time", burnTime);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.maxBurnTime = nbt.getInt("max_burn_time");
        this.burnTime = nbt.getInt("burn_time");
    }

    @Override
    public Text getDisplayName()
    {
        return Text.empty();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player)
    {
        return new SmallCompressorScreenHandler(playerInventory, inventory, syncId, delegate);
    }

    public class Delegate implements PropertyDelegate
    {
        @Override
        public int get(int index)
        {
            return switch (index)
            {
                case 0 -> burnTime;
                case 1 -> maxBurnTime;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value)
        {

        }

        @Override
        public int size()
        {
            return 2;
        }
    }
}
