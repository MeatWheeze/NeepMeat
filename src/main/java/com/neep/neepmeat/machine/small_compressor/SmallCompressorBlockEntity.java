package com.neep.neepmeat.machine.small_compressor;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.inventory.ImplementedInventory;
import com.neep.neepmeat.component.CompressedAirComponent;
import com.neep.neepmeat.init.NMComponents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmallCompressorBlockEntity extends SyncableBlockEntity implements NamedScreenHandlerFactory
{
    private final ImplementedInventory inventory = ImplementedInventory.ofSize(1);
    private final BurnerBehaviour burner = new BurnerBehaviour(inventory, this::updateState);
    private final Random random = Random.create();

    private void updateState(boolean burning)
    {
        if (getCachedState().get(SmallCompressorBlock.LIT) != burning)
        {
            world.setBlockState(pos, getCachedState().with(SmallCompressorBlock.LIT, burning));
        }
    }

    public SmallCompressorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void serverTick()
    {
        burner.tick();

        if (burner.getBurnTime() > 0)
        {
            Box box = Box.of(Vec3d.ofCenter(getPos()), 16, 8, 16);

            List<PlayerEntity> players = getWorld().getEntitiesByClass(PlayerEntity.class, box, p -> true);

            players.forEach(player ->
            {
                CompressedAirComponent component = NMComponents.COMPRESSED_AIR.getNullable(player);

                if (component != null)
                    component.insertAir(10);
            });
        }
    }

    public void clientTick()
    {
        if (getCachedState().get(SmallCompressorBlock.LIT) && random.nextInt(4) == 0)
        {
            this.getWorld().addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);

        inventory.writeNbt(nbt);
        burner.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);

        inventory.readNbt(nbt);
        burner.readNbt(nbt);
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
        return new SmallCompressorScreenHandler(playerInventory, inventory, syncId, burner.getDelegate());
    }

    public void dropItems()
    {
        ItemScatterer.spawn(getWorld(), getPos(), inventory);
    }
}
