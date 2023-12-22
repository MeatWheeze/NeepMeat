package com.neep.assembly;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IdListPalette;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AssemblyEntity extends Entity
{
    private static final Palette<BlockState> FALLBACK_PALETTE = new IdListPalette<>(Block.STATE_IDS, Blocks.AIR.getDefaultState());
    private static final TrackedData<Optional<BlockState>> BLOCK = DataTracker.registerData(AssemblyEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE);
    private static final TrackedData<NbtCompound> PALETTE = DataTracker.registerData(AssemblyEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);

    public BlockState state;
    public PalettedContainer<BlockState> blocks;
//    protected boolean needsPaletteUpdate;

    public AssemblyEntity(EntityType<?> type, World world)
    {
        super(type, world);

        this.state = Blocks.STONE.getDefaultState();
//        this.blocks = new PalettedContainer<>(FALLBACK_PALETTE,
//                Block.STATE_IDS,
//                NbtHelper::toBlockState,
//                NbtHelper::fromBlockState,
//                Blocks.AIR.getDefaultState());

        this.updatePalette();
        this.setBoundingBox(getBounds());

//        blocks.set(0, 0, 0, Blocks.DIRT.getDefaultState());
//        blocks.set(0, 0, 0, BlockInitialiser.SCAFFOLD_PLATFORM.getDefaultState());

    }

    public AssemblyEntity(World world)
    {
        this(Assembly.ASSEMBLY_ENTITY, world);
    }

    public static boolean canAssemble(BlockState state)
    {
        return state.isOf(Assembly.PLATFORM);
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
            initPalette();
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
        this.setBoundingBox(getBounds());
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
//        return getBounds().offset(getPos().add(-0.5, -1, -0.5));
        return getBounds().offset(getPos());
    }

    public Box getBounds()
    {
//        return Box.of(new Vec3d(0, 0, 0), 1, 2, 1);
//        Box base = Box.of(new Vec3d(0, 0, 0), 0, 0, 0);
        double dx = 0;
        double dy = 0;
        double dz = 0;

        PalettedContainer<BlockState> states = getPalette();
        for (int i = 1; i <= 16; ++i)
        {
            for (int j = 1; j <= 16; ++j)
            {
                for (int k = 1; k <= 16; ++k)
                {
//                    if (!(states.get(i, j, i).equals(Blocks.AIR.getDefaultState())))
//                    if (!states.get(i, j, k).isAir())
                    if (states.get(i - 1, j - 1, k - 1).isOf(Assembly.PLATFORM))
                    {
//                        System.out.println(i + ", " + j + ", " + k + ", " + ", current z: " + dz + ", " + states.get(i, j, k));
                        if (i > dx)
                            dx = i;
                        if (j > dy)
                            dy = j;
                        if (k > dz)
                            dz = k;
                    }
                }
            }
        }
//        return Box.of(Vec3d.ZERO, dx, dy, dz);
        if (dx == 0 || dy == 0 || dz == 0)
        {
            return new Box(0, 0, 0, 1, 1, 1);
        }
        return new Box(0, 0, 0, dx, dy, dz);
//        return Box.of(Vec3d.ZERO, 1, 1, 1);
    }

    public ActionResult interact(PlayerEntity player, Hand hand)
    {
        if (!player.getEntityWorld().isClient)
        {
            if (!player.isSneaking())
            {
                blocks.set(0, 0, 0, Assembly.PLATFORM.getDefaultState());
                blocks.set(0, 1, 0, Assembly.PLATFORM.getDefaultState());
                blocks.set(1, 1, 0, Assembly.PLATFORM.getDefaultState());
                blocks.set(1, 1, 1, Assembly.PLATFORM.getDefaultState());
                blocks.set(2, 1, 1, Assembly.PLATFORM.getDefaultState());
                blocks.set(4, 1, 1, Assembly.PLATFORM.getDefaultState());
            }
            else
            {
//                initPalette();
                this.remove(RemovalReason.DISCARDED);
            }
            updatePalette();
        }

//        System.out.println(world.getBlockState(getBlockPos()));
//        System.out.println(!world.getBlockState(getBlockPos()).isAir());

        return ActionResult.SUCCESS;
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

    public boolean canUsePortals()
    {
        return false;
    }

    @Override
    public boolean collidesWith(Entity other)
    {
        return BoatEntity.canCollide(this, other);
    }

//    public void setBlocks(List<BlockPos> posList, List<BlockState> stateList)
//    {
//        PalettedContainer<BlockState> states = getPalette();
//        for (int i = 0; i < posList.size(); ++i)
//        {
//            BlockPos pos =
//            states.set(posList.get(i))
//        }
//    }

    public BlockState getState()
    {
        return dataTracker.get(BLOCK).orElseGet(Blocks.COAL_ORE::getDefaultState);
    }

    public void initPalette()
    {
        this.blocks = new PalettedContainer<>(FALLBACK_PALETTE,
                Block.STATE_IDS,
                NbtHelper::toBlockState,
                NbtHelper::fromBlockState,
                Blocks.AIR.getDefaultState());
    }

    public PalettedContainer<BlockState> getPalette()
    {
        readPalette(dataTracker.get(PALETTE));
        return this.blocks;
    }
}
