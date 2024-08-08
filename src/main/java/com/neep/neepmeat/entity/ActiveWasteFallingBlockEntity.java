package com.neep.neepmeat.entity;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMEntities;
import com.neep.neepmeat.machine.live_machine.block.LargestHopperBlock;
import com.neep.neepmeat.util.IterateRandomly;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ActiveWasteFallingBlockEntity extends FallingBlockEntity
{
    protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(ActiveWasteFallingBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    public int timeFalling;
    public boolean dropItem = true;
    @Nullable
    public NbtCompound blockEntityData;
    private BlockState block = Blocks.SAND.getDefaultState();
    private boolean destroyedOnLanding;
    private boolean hurtEntities;
    private int fallHurtMax = 40;
    private float fallHurtAmount;

    public ActiveWasteFallingBlockEntity(EntityType<? extends ActiveWasteFallingBlockEntity> entityType, World world)
    {
        super(entityType, world);
    }

    private ActiveWasteFallingBlockEntity(World world, double x, double y, double z, BlockState block)
    {
        this(NMEntities.FALLING_ACTIVE_WASTE, world);
        this.block = block;
        this.intersectionChecked = true;
        this.setPosition(x, y, z);
        this.setVelocity(Vec3d.ZERO);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.setFallingBlockPos(this.getBlockPos());
    }

    public static ActiveWasteFallingBlockEntity spawnFromBlock(ServerWorld serverWorld, BlockPos pos, BlockState state)
    {
        ActiveWasteFallingBlockEntity fallingBlockEntity = new ActiveWasteFallingBlockEntity(
                serverWorld,
                (double)pos.getX() + 0.5,
                (double)pos.getY(),
                (double)pos.getZ() + 0.5,
                state.contains(Properties.WATERLOGGED) ? state.with(Properties.WATERLOGGED, Boolean.valueOf(false)) : state
        );
        serverWorld.setBlockState(pos, state.getFluidState().getBlockState(), Block.NOTIFY_ALL);
        serverWorld.spawnEntity(fallingBlockEntity);
        return fallingBlockEntity;
    }

    @Override
    public boolean isAttackable()
    {
        return false;
    }

    public BlockPos getFallingBlockPos()
    {
        return this.dataTracker.get(BLOCK_POS);
    }

    public void setFallingBlockPos(BlockPos pos)
    {
        this.dataTracker.set(BLOCK_POS, pos);
    }

    @Override
    protected Entity.MoveEffect getMoveEffect()
    {
        return Entity.MoveEffect.NONE;
    }

    @Override
    protected void initDataTracker()
    {
        this.dataTracker.startTracking(BLOCK_POS, BlockPos.ORIGIN);
    }

    @Override
    public boolean canHit()
    {
        return !this.isRemoved();
    }

    private boolean canPlaceAt(World world, BlockPos pos, BlockState state)
    {
        BlockState oldState = world.getBlockState(pos);

        if (oldState.isOf(Blocks.MOVING_PISTON))
            return false;

        boolean canReplace = world.getBlockState(pos).canReplace(new AutomaticItemPlacementContext(this.getWorld(), pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
//        boolean bl4 = FallingBlock.canFallThrough(this.getWorld().getBlockState(blockPos.down()));
        boolean canPlace = state.canPlaceAt(this.getWorld(), pos);

        return canReplace && canPlace;
    }

    @Override
    public void tick()
    {
        if (this.block.isAir())
        {
            this.discard();
        }
        else
        {
            Block block = this.block.getBlock();
            ++this.timeFalling;
            if (!this.hasNoGravity())
            {
                this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
            }

            this.move(MovementType.SELF, this.getVelocity());

            if (!this.getWorld().isClient)
            {
                BlockPos blockPos = this.getBlockPos();
                if (this.isOnGround())
                {
                    BlockState oldState = this.getWorld().getBlockState(blockPos);
                    if (LargestHopperBlock.isLargeHopper(oldState)
                            || LargestHopperBlock.isLargeHopper(getWorld().getBlockState(blockPos.down())))
                    {
                        discard();
                        this.onDestroyedOnLanding(block, blockPos);
                        this.dropItem(block);
                        return;
                    }

                    if (random.nextBoolean() && jump(getWorld(), blockPos))
                    {
                    }
                    else
                    {
                        this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));

                        // Find the next viable place
                        BlockPos placePos = blockPos;
                        while (!canPlaceAt(getWorld(), placePos, this.block))
                        {
                            placePos = placePos.up();
                        }

                        if (this.block.contains(Properties.WATERLOGGED) && this.getWorld().getFluidState(placePos).getFluid() == Fluids.WATER)
                        {
                            this.block = this.block.with(Properties.WATERLOGGED, Boolean.TRUE);
                        }

                        if (this.getWorld().setBlockState(placePos, this.block, Block.NOTIFY_ALL))
                        {
                            ((ServerWorld) this.getWorld())
                                    .getChunkManager()
                                    .threadedAnvilChunkStorage
                                    .sendToOtherNearbyPlayers(this, new BlockUpdateS2CPacket(placePos, this.getWorld().getBlockState(placePos)));
                            this.discard();
                            if (block instanceof LandingBlock)
                            {
                                ((LandingBlock) block).onLanding(this.getWorld(), placePos, this.block, oldState, this);
                            }

                            if (this.blockEntityData != null && this.block.hasBlockEntity())
                            {
                                BlockEntity blockEntity = this.getWorld().getBlockEntity(placePos);
                                if (blockEntity != null)
                                {
                                    NbtCompound nbtCompound = blockEntity.createNbt();

                                    for (String string : this.blockEntityData.getKeys())
                                    {
                                        nbtCompound.put(string, this.blockEntityData.get(string).copy());
                                    }

                                    try
                                    {
                                        blockEntity.readNbt(nbtCompound);
                                    }
                                    catch (Exception var15)
                                    {
                                        NeepMeat.LOGGER.error("Failed to load block entity from falling block", var15);
                                    }

                                    blockEntity.markDirty();
                                }
                            }
                        }
                        else if (this.dropItem && this.getWorld().getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
                        {
                            this.discard();
                            this.onDestroyedOnLanding(block, blockPos);
                            this.dropItem(block);
                        }
                    }
                }
                else if (!this.getWorld().isClient
                        && (this.timeFalling > 100 && (blockPos.getY() <= this.getWorld().getBottomY() || blockPos.getY() > this.getWorld().getTopY()) || this.timeFalling > 600))
                {
                    if (this.dropItem && this.getWorld().getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
                    {
                        this.dropItem(block);
                    }

                    this.discard();
                }
            }

            this.setVelocity(this.getVelocity().multiply(0.98));
        }
    }

    private boolean jump(World world, BlockPos pos)
    {
        Direction[] directions = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

        for (int i : new IterateRandomly(4))
        {
            BlockPos side = pos.offset(directions[i]);
            BlockPos sideDown = side.down();

            if (world.isAir(side) && world.isAir(sideDown))
            {
                setPos(side.getX() + 0.5, getY(), side.getZ() + 0.5);
                setPosition(side.getX() + 0.5, getY(), side.getZ() + 0.5);
                setFallingBlockPos(side);
                setOnGround(false);
                return true;
            }
        }

        return false;
    }

    @Override
    public BlockState getBlockState()
    {
        return block;
    }

    @Override
    public void setHurtEntities(float fallHurtAmount, int fallHurtMax)
    {
        this.hurtEntities = true;
        this.fallHurtAmount = fallHurtAmount;
        this.fallHurtMax = fallHurtMax;
    }

    @Override
    public void onDestroyedOnLanding(Block block, BlockPos pos)
    {
        if (block instanceof LandingBlock) {
            ((LandingBlock)block).onDestroyedOnLanding(this.getWorld(), pos, this);
        }
    }

    @Override
    public void setDestroyedOnLanding()
    {
        destroyedOnLanding = true;
    }

    @Override
    protected Text getDefaultName()
    {
        return Text.translatable("entity.minecraft.falling_block_type", this.block.getBlock().getName());
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket()
    {
        return new EntitySpawnS2CPacket(this, Block.getRawIdFromState(this.getBlockState()));
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet)
    {
        super.onSpawnPacket(packet);
        this.block = Block.getStateFromRawId(packet.getEntityData());
        this.intersectionChecked = true;
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        this.setPosition(d, e, f);
        this.setFallingBlockPos(this.getBlockPos());
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt)
    {
        nbt.put("BlockState", NbtHelper.fromBlockState(this.block));
        nbt.putInt("Time", this.timeFalling);
        nbt.putBoolean("DropItem", this.dropItem);
        nbt.putBoolean("HurtEntities", this.hurtEntities);
        nbt.putFloat("FallHurtAmount", this.fallHurtAmount);
        nbt.putInt("FallHurtMax", this.fallHurtMax);
        if (this.blockEntityData != null)
        {
            nbt.put("TileEntityData", this.blockEntityData);
        }

        nbt.putBoolean("CancelDrop", this.destroyedOnLanding);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt)
    {
        this.block = NbtHelper.toBlockState(this.getWorld().createCommandRegistryWrapper(RegistryKeys.BLOCK), nbt.getCompound("BlockState"));
        this.timeFalling = nbt.getInt("Time");
        if (nbt.contains("HurtEntities", NbtElement.NUMBER_TYPE))
        {
            this.hurtEntities = nbt.getBoolean("HurtEntities");
            this.fallHurtAmount = nbt.getFloat("FallHurtAmount");
            this.fallHurtMax = nbt.getInt("FallHurtMax");
        }
        else if (this.block.isIn(BlockTags.ANVIL))
        {
            this.hurtEntities = true;
        }

        if (nbt.contains("DropItem", NbtElement.NUMBER_TYPE))
        {
            this.dropItem = nbt.getBoolean("DropItem");
        }

        if (nbt.contains("TileEntityData", NbtElement.COMPOUND_TYPE))
        {
            this.blockEntityData = nbt.getCompound("TileEntityData");
        }

        this.destroyedOnLanding = nbt.getBoolean("CancelDrop");
        if (this.block.isAir())
        {
            this.block = Blocks.SAND.getDefaultState();
        }
    }
}
