package com.neep.assembly;

import com.neep.assembly.block.AnchorBlock;
import com.neep.assembly.block.IRail;
import com.neep.assembly.storage.AssemblyContainer;
import com.neep.neepmeat.init.BlockInitialiser;
import com.neep.neepmeat.util.LinearDirection;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
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
import net.minecraft.util.TypeFilter;
import net.minecraft.util.collection.ReusableStream;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IdListPalette;
import net.minecraft.world.chunk.Palette;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class AssemblyEntity extends Entity
{
    private static final Palette<BlockState> FALLBACK_PALETTE = new IdListPalette<>(Block.STATE_IDS, Blocks.AIR.getDefaultState());
    private static final TrackedData<Optional<BlockState>> BLOCK = DataTracker.registerData(AssemblyEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE);
    private static final TrackedData<NbtCompound> PALETTE = DataTracker.registerData(AssemblyEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);

    public AssemblyContainer blocks = new AssemblyContainer(FALLBACK_PALETTE,
        Block.STATE_IDS,
        NbtHelper::toBlockState,
        NbtHelper::fromBlockState,
            Blocks.AIR.getDefaultState());
    protected boolean needsBoxUpdate;

    protected List<BlockPos> anchorPositions = new ArrayList<>();

    public AssemblyEntity(EntityType<?> type, World world)
    {
        super(type, world);

//        this.updatePalette();
//        this.setBoundingBox(calculateBoundingBox());
//        this.updatePalette();
        this.needsBoxUpdate = true;
    }

    public AssemblyEntity(World world)
    {
        this(Assembly.ASSEMBLY_ENTITY, world);
    }

    public static boolean canAssemble(BlockState state)
    {
        return state.isOf(Assembly.PLATFORM) || state.isOf(Assembly.ANCHOR) ||
                state.isOf(BlockInitialiser.RUSTED_IRON_BLOCK);
//                state.isOf(BlockInitialiser.RUSTED_IRON_BLOCK.stairs);
    }

    @Override
    protected void initDataTracker()
    {
        this.dataTracker.startTracking(BLOCK, Optional.empty());
        this.dataTracker.startTracking(PALETTE, new NbtCompound());
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
//        writePalette(nbt);
        blocks.write(nbt, "Palette", "BlockStates");
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
//        readPalette(nbt);
        if (nbt.contains("Palette", 9) && nbt.contains("BlockStates", 12))
        {
            blocks.read(nbt.getList("Palette", 10), nbt.getLongArray("BlockStates"));
        }
        updateAnchorPositions();
   }

    public NbtCompound writePalette(NbtCompound nbt)
    {
        blocks.write(nbt, "Palette", "BlockStates");
        return nbt;
    }

    public void readPalette(NbtCompound nbt)
    {
        if (!world.isClient)
        {
        }
        if (nbt.contains("Palette", 9) && nbt.contains("BlockStates", 12))
        {
            blocks.read(nbt.getList("Palette", 10), nbt.getLongArray("BlockStates"));
        }
//        dataTracker.set(PALETTE, writePalette(new NbtCompound()));
    }

    public void updatePalette()
    {
        if (blocks == null)
        {
            initPalette();
        }
        dataTracker.set(PALETTE, writePalette(new NbtCompound()));
//        this.setBoundingBox(getBounds());
    }

    public void updateAnchorPositions()
    {
        AssemblyContainer container = getPalette();
        for (int i = 0; i < 16; ++i)
        {
            for (int j = 0; j < 16; ++j)
            {
                for (int k = 0; k < 16; ++k)
                {
                    BlockState state = container.get(i, j, k);
                    if (state.getBlock() instanceof AnchorBlock)
                    {
                        anchorPositions.add(new BlockPos(i, j, k));
                    }
                }
            }
        }
    }

    @Override
    public void tick()
    {
        super.tick();
        if (this.needsBoxUpdate)
        {
            this.setBoundingBox(calculateBoundingBox());
            if (!world.isClient)
            {
                updatePalette();
            }
            this.needsBoxUpdate = false;
        }

//        setVelocity(0.0, 0.0, 0);
//        this.move(MovementType.SELF, (getVelocity()));


        AssemblyContainer container = getPalette();
        if (anchorPositions.size() == 1)
        {
            for (BlockPos pos : anchorPositions)
            {
                Direction facing = container.get(pos).get(AnchorBlock.FACING);
                BlockPos worldPos = pos.add(this.getBlockPos()).offset(facing);
                BlockState railState;
                if ((railState = world.getBlockState(worldPos)).getBlock() instanceof IRail)
                {
                    Direction railFacing = railState.get(FacingBlock.FACING);
                    LinearDirection railDir = railState.get(IRail.DIRECTION);
                    Vec3f vel = railDir == LinearDirection.FORWARDS ? railFacing.getUnitVector() : railFacing.getOpposite().getUnitVector();
                    vel.multiplyComponentwise(0.1f, 0.1f, -0.1f);
                    this.setVelocity(vel);
                    this.velocityModified = true;
//                    System.out.println(vel);
                }
                else
                {
                    this.setVelocity(Vec3d.ZERO);
                }
            }
        }
        if (this.isLogicalSideForUpdatingMovement())
        {
//            this.move(MovementType.SELF, new Vec3d(0, -0.1, 0));
            this.move(MovementType.SELF, getVelocity());
        }
        else
        {
            this.move(MovementType.SELF, getVelocity());
//            this.setVelocity(Vec3d.ZERO);
        }
    }


    @Override
    public void move(MovementType movementType, Vec3d movement)
    {
        Vec3d vec3d;

        this.world.getProfiler().push("move");
        if (this.movementMultiplier.lengthSquared() > 1.0E-7)
        {
            movement = movement.multiply(this.movementMultiplier);
            this.movementMultiplier = Vec3d.ZERO;
            this.setVelocity(Vec3d.ZERO);
        }

        if (true)
        {
            if ((vec3d = this.adjustMovementForCollisions(movement)).lengthSquared() > 1.0E-7)
//            if ((vec3d = movement).lengthSquared() > 1.0E-7)
            {
                Vec3d newPos = new Vec3d(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
                this.setPosition(newPos);
                if (this.isLogicalSideForUpdatingMovement())
                {
                    this.updateTrackedPosition(newPos);
                }
                Vec3d finalMovement = movement;
//                if (false)
                {
                    world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), getBoundingBox().expand(0, 0.1, 0), (t) -> true).forEach(
                            entity ->
                            {
                                if (false)
                                    entity.setPosition(getPos().add(0.5, 0.5, 0));
                                if (true)
                                {
                                    entity.setVelocity(finalMovement);
                                    entity.setOnGround(true);
                                }
                            }
                    );
                }
            }

            this.world.getProfiler().pop();
            this.world.getProfiler().push("rest");
            this.horizontalCollision = !MathHelper.approximatelyEquals(movement.x, vec3d.x) || !MathHelper.approximatelyEquals(movement.z, vec3d.z);
            this.verticalCollision = movement.y != vec3d.y;

            if (this.isRemoved())
            {
                this.world.getProfiler().pop();
                return;
            }

            Vec3d vec3d2 = this.getVelocity();
            if (movement.x != vec3d.x)
            {
                this.setVelocity(0.0, vec3d2.y, vec3d2.z);
            }
            if (movement.z != vec3d.z)
            {
                this.setVelocity(vec3d2.x, vec3d2.y, 0.0);
            }

            this.tryCheckBlockCollision();
            float d = this.getVelocityMultiplier();
            this.setVelocity(this.getVelocity().multiply(d, 1.0, d));
        }

        this.world.getProfiler().pop();
    }

    private Vec3d adjustMovementForCollisions(Vec3d movement)
    {
        Box box = this.getBoundingBox();
        ShapeContext shapeContext = ShapeContext.of(this);
        VoxelShape voxelShape = this.world.getWorldBorder().asVoxelShape();

        Stream<VoxelShape> stream = VoxelShapes.matchesAnywhere(voxelShape, VoxelShapes.cuboid(box.contract(1.0E-7)), BooleanBiFunction.AND) ? Stream.empty() : Stream.of(voxelShape);
//        Stream<VoxelShape> stream2 = this.world.getEntityCollisions(this, box.stretch(movement), entity -> true);
//        ReusableStream<VoxelShape> reusableStream = new ReusableStream<>(Stream.concat(stream2, stream));
        ReusableStream<VoxelShape> reusableStream = new ReusableStream<>(stream);

        Vec3d vec3d = movement.lengthSquared() == 0.0 ? movement : Entity.adjustMovementForCollisions(this, movement, box, this.world, shapeContext, reusableStream);

        return vec3d;
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

        AssemblyContainer states = getPalette();
        if (states == null)
        {
            return new Box(0, 0, 0, 1, 1, 1);
        }
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
//                blocks.set(0, 0, 0, Assembly.PLATFORM.getDefaultState());
//                blocks.set(0, 1, 0, Assembly.PLATFORM.getDefaultState());
//                blocks.set(1, 1, 0, Assembly.PLATFORM.getDefaultState());
//                blocks.set(1, 1, 1, Assembly.PLATFORM.getDefaultState());
//                blocks.set(2, 1, 1, Assembly.PLATFORM.getDefaultState());
                blocks.set(4, 1, 1, Assembly.PLATFORM.getDefaultState());
            }
            else
            {
//                initPalette();
//                this.remove(RemovalReason.DISCARDED);
                AssemblyUtils.disassemble(world, this);
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
        return false;
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

    public BlockState getState()
    {
//        return dataTracker.get(BLOCK).orElseGet(Blocks.COAL_ORE::getDefaultState);
        return Blocks.COAL_ORE.getDefaultState();
    }

    public BlockState setState(int x, int y, int z, BlockState state)
    {
        if (state.isOf(Assembly.ANCHOR))
        {
            anchorPositions.add(new BlockPos(x, y, z));
        }
        return this.blocks.set(x, y, z, state);
    }

    public void initPalette()
    {
        this.blocks = new AssemblyContainer(FALLBACK_PALETTE,
                Block.STATE_IDS,
                NbtHelper::toBlockState,
                NbtHelper::fromBlockState,
                Blocks.AIR.getDefaultState());
    }

    public AssemblyContainer getPalette()
    {
        NbtCompound nbt;
        if ((nbt = dataTracker.get(PALETTE)) != null);
        {
            readPalette(nbt);
        }
        return this.blocks;
    }

    public void setVelocity(Vec3f vec)
    {
        this.setVelocity(new Vec3d(vec.getX(), vec.getY(), vec.getZ()));
    }

}
