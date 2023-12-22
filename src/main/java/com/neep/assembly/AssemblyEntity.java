package com.neep.assembly;

import com.sun.jna.platform.mac.SystemB;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IdListPalette;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;

import java.util.Optional;

public class AssemblyEntity extends Entity
{
    private static final Palette<BlockState> FALLBACK_PALETTE = new IdListPalette<>(Block.STATE_IDS, Blocks.AIR.getDefaultState());
    private static final TrackedData<Optional<BlockState>> BLOCK = DataTracker.registerData(AssemblyEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE);
    private static final TrackedData<NbtCompound> PALETTE = DataTracker.registerData(AssemblyEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);

    public BlockState state;
    public PalettedContainer<BlockState> blocks;

    public AssemblyEntity(EntityType<?> type, World world)
    {
        super(type, world);

        this.state = Blocks.STONE.getDefaultState();
//        this.blocks = new PalettedContainer<>(FALLBACK_PALETTE,
//                Block.STATE_IDS,
//                NbtHelper::toBlockState,
//                NbtHelper::fromBlockState,
//                Blocks.AIR.getDefaultState());

        this.setBoundingBox(getBounds());

        blocks.set(0, 0, 0, Blocks.DIRT.getDefaultState());
        updatePalette();

    }

    public AssemblyEntity(World world)
    {
        this(Assembly.ASSEMBLY_ENTITY, world);
    }

    @Override
    protected void initDataTracker()
    {
//        this.dataTracker.startTracking(SLEEPING_POSITION, Optional.empty());
        this.dataTracker.startTracking(BLOCK, Optional.empty());
        this.dataTracker.startTracking(PALETTE, writePalette(new NbtCompound()));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt)
    {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt)
    {
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        writePalette(nbt);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        readPalette(nbt);
   }

    public NbtCompound writePalette(NbtCompound nbt)
    {
        if (blocks == null)
        {
            this.blocks = new PalettedContainer<>(FALLBACK_PALETTE,
                    Block.STATE_IDS,
                    NbtHelper::toBlockState,
                    NbtHelper::fromBlockState,
                    Blocks.AIR.getDefaultState());
        }


        blocks.write(nbt, "Palette", "BlockStates");
        return nbt;
    }

    public void readPalette(NbtCompound nbt)
    {
        if (nbt.contains("Palette", 9) && nbt.contains("BlockStates", 12))
        {
            blocks.read(nbt.getList("Palette", 10), nbt.getLongArray("BlockStates"));
        }
    }

    public void updatePalette()
    {
        dataTracker.set(PALETTE, writePalette(new NbtCompound()));
    }

    @Override
    public void tick()
    {
        super.tick();

        if (!getEntityWorld().isClient)
        {
            this.state = world.getBlockState(getBlockPos().down(1));
            dataTracker.set(BLOCK, Optional.of(state));
        }
//        this.setBoundingBox(getBounds());
    }

    @Override
    public Packet<?> createSpawnPacket()
    {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    protected Box calculateBoundingBox()
    {
        return getBounds().offset(getPos());
    }

    public Box getBounds()
    {
        return Box.of(new Vec3d(0, 0, 0), 1, 2, 1);
    }

    @Override
    public void pushAwayFrom(Entity entity)
    {
//        if (entity.getBoundingBox().minY <= this.getBoundingBox().minY)
//        {
            super.pushAwayFrom(entity);
//        }
    }

    @Override
    public boolean collides()
    {
        return !this.isRemoved();
    }

    @Override
    public boolean isCollidable()
    {
        return true;
    }

    @Override
    public boolean isPushable()
    {
        return true;
    }

    @Override
    public boolean collidesWith(Entity other)
    {
        return BoatEntity.canCollide(this, other);
    }

    public BlockState getState()
    {
        return dataTracker.get(BLOCK).orElseGet(Blocks.COAL_ORE::getDefaultState);
    }

    public PalettedContainer<BlockState> getPalette()
    {
        readPalette(dataTracker.get(PALETTE));
        return this.blocks;
    }
}
