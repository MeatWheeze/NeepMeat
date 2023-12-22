package com.neep.assembly;

import com.neep.assembly.block.AnchorBlock;
import com.neep.assembly.block.IRail;
import com.neep.assembly.storage.AssemblyContainer;
import com.neep.neepmeat.init.NMBlocks;
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
    private int delta;

    private double x;
    private double y;
    private double z;
    private double dx;
    private double dy;
    private double dz;

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
                state.isOf(NMBlocks.RUSTED_IRON_BLOCK);
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
    }

    public void updatePalette()
    {
        if (blocks == null)
        {
            initPalette();
        }
        dataTracker.set(PALETTE, writePalette(new NbtCompound()));
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

    public void updateVelocity()
    {
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
                }
                else
                {
                    this.setVelocity(Vec3d.ZERO);
                }
            }
        }
    }

//    @Override
//    public void tick()
//    {
//        super.tick();
//        if (this.needsBoxUpdate)
//        {
//            this.setBoundingBox(calculateBoundingBox());
//            if (!world.isClient)
//            {
//                updatePalette();
//            }
//            this.needsBoxUpdate = false;
//        }
//
////        setVelocity(0.0, 0.0, 0);
////        this.move(MovementType.SELF, (getVelocity()));
//
//
//
//        interpolateMotion();
//
//        if (this.isLogicalSideForUpdatingMovement())
//        {
////            this.move(MovementType.SELF, new Vec3d(0, -0.1, 0));
//            this.move(MovementType.SELF, getVelocity());
//        }
//        else
//        {
////            this.move(MovementType.SELF, getVelocity());
////            this.setVelocity(Vec3d.ZERO);
//            this.setVelocity(Vec3d.ZERO);
//        }
//    }

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

        super.tick();
        if (this.isLogicalSideForUpdatingMovement())
        {
            this.updateVelocity();
            this.move(MovementType.SELF, this.getVelocity());
        }
        else
        {
//            this.moveEntities(this.getVelocity());
            this.setVelocity(Vec3d.ZERO);
//            this.move(MovementType.SELF, this.getVelocity());
        }

        this.interpolateMotion();

        List<LivingEntity> entities = world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), getBoundingBox().expand(0, 0.2, 0), (t) -> true);
        for (LivingEntity entity : entities)
        {
            Box box = entity.getBoundingBox();
            Box thisBox = this.getBoundingBox();
            Box intersect = thisBox.intersection(box);
            Vec3d vel = entity.getVelocity();

            Direction.Axis axis = Direction.Axis.Y;
            double xArea = intersect.getYLength() * intersect.getZLength();
            double yArea = intersect.getXLength() * intersect.getZLength();
            double zArea = intersect.getXLength() * intersect.getYLength();


            if (xArea > Math.max(yArea, zArea))
                axis = Direction.Axis.X;
            if (yArea > Math.max(xArea, zArea))
                axis = Direction.Axis.Y;
            if (zArea > Math.max(xArea, yArea))
                axis = Direction.Axis.Z;

            double dist1 = thisBox.getMax(axis) - box.getMin(axis); // Positive direction
            double dist2 = box.getMax(axis) - thisBox.getMin(axis); // Negative direction
//                    System.out.println("dist1: " + dist1 + " dist2: " + dist2);

            if (dist1 > 0 && dist2 > dist1)
            {
                Vec3d vec = AssemblyUtils.getAxisUnitVector(axis).multiply(dist1);
                entity.setPosition(entity.getPos().add(vec));
                entity.setOnGround(true);

                // Prevent falling when vertical
                if (entity.getVelocity().getComponentAlongAxis(axis) < 0)
                {
                    entity.setVelocity(entity.getVelocity().multiply(1, this.getVelocity().y == 0 ? 0 : 0.9, 1));
//                        entity.setVelocity(getVelocity().x, getVelocity().y, getVelocity().z);
//                        entity.fallDistance = 0;
                }
                if (axis.isVertical())
                {
//                        entity.setPosition(entity.getPos().add(this.getVelocity()));
//                        entity.addVelocity(getVelocity().x, getVelocity().y, getVelocity().z);
                }
                break;
            }
            if (dist2 > 0 && dist1 > dist2)
            {
                Vec3d vec = AssemblyUtils.getAxisUnitVector(axis).multiply(-dist2);
                entity.setPosition(entity.getPos().add(vec));
                break;
            }
            entity.setOnGround(true);
        }

//        this.moveEntities();
        this.checkBlockCollision();
    }

    public void moveEntities(Vec3d movement)
    {
        world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), getBoundingBox().expand(0, 0.2, 0), (t) -> true).forEach(
                entity ->
                {
                    entity.setVelocity(movement);
                    entity.setOnGround(true);
////                                    System.out.println(finalMovement);
                }
        );
    }

    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.delta = 10;
    }

    protected void interpolateMotion()
    {
        if (this.isLogicalSideForUpdatingMovement())
        {
            this.delta = 0;
            this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
        }
        if (this.delta <= 0)
        {
            return;
        }

        this.dx = this.x - getX();
        this.dy = this.y - getY();
        this.dz = this.z - getZ();

        double d = this.getX() + dx / (double) this.delta;
        double e = this.getY() + dy / (double) this.delta;
        double f = this.getZ() + dz / (double) this.delta;

        this.setVelocity(dx / (double) delta, dy / (double) delta, dz / (double) delta);
        --this.delta;
        this.setPosition(d, e, f);
        this.setRotation(this.getYaw(), this.getPitch());

        world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), getBoundingBox().expand(0, 0.2, 0), (t) -> true).forEach(
                entity ->
                {
                    if (false)
                        entity.setPosition(getPos().add(0.5, 0.5, 0));
                    if (world.isClient)
                    {
//                        double d = entity.getX() + dx / (double) this.delta;
//                        double e = entity.getY() + dy / (double) this.delta;
//                        double f = entity.getZ() + dz / (double) this.delta;

//                        double v = d + e + f;

//                        entity.setPosition(entity.getPos().add(dx, dy, dz));
//                        entity.setPos(entity.getX(), this.getY() + 2, entity.getZ());
//                        System.out.println(entity.getPos().y);
//                        entity.setVelocity(getVelocity());
//                        entity.setVelocity(new Vec3d(0, 1, 0));
//                        entity.setOnGround(true);
                    }
                });

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
//                if (this.isLogicalSideForUpdatingMovement())
//                {
//                    this.updateTrackedPosition(newPos);
//                }
                Vec3d finalMovement = movement;
//                if (false)
                {
//                    world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), getBoundingBox().expand(0, 0.2, 0), (t) -> true).forEach(
//                            entity ->
//                            {
//                                if (false)
//                                    entity.setPosition(getPos().add(0.5, 0.5, 0));
//                                if (true)
//                                {
//                                    entity.setVelocity(finalMovement);
//                                    entity.setOnGround(true);
////                                    System.out.println(finalMovement);
//                                }
//                            }
//                    );
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
        return getBounds().offset(getPos());
    }

    public Box getBounds()
    {
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
        if (dx == 0 || dy == 0 || dz == 0)
        {
            return new Box(0, 0, 0, 1, 1, 1);
        }
        return new Box(0, 0, 0, dx, dy, dz);
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
                AssemblyUtils.disassemble(world, this);
            }
            updatePalette();
        }


        return ActionResult.SUCCESS;
    }

    @Override
    public void pushAwayFrom(Entity entity)
    {
//        if (entity.getBoundingBox().minY <= this.getBoundingBox().minY)
//        {
//            super.pushAwayFrom(entity);
//        }
    }

    @Override
    public boolean collides()
    {
        return !this.isRemoved();
//        return false;
    }

    @Override
    public boolean isCollidable()
    {
        return false;
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
