package com.neep.neepmeat.machine.phage_ray;

import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.neep.meatlib.api.event.InputEvents;
import com.neep.meatlib.api.event.UseAttackCallback;
import com.neep.meatlib.graphics.GraphicsEffects;
import com.neep.meatlib.network.PacketBufUtil;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMGraphicsEffects;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.machine.pylon.PylonBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PhageRayEntity extends Entity
{
    public static final Identifier CHANNEL_ID = new Identifier(NeepMeat.NAMESPACE, "phage_ray");
    protected static final TrackedData<Boolean> RUNNING = DataTracker.registerData(PhageRayEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Nullable private PhageRayBlockEntity parent;

    private boolean trigger = false;
    private int triggerTicks = 0;
    private double range = 30;

    public PhageRayEntity(EntityType<?> type, World world)
    {
        super(type, world);
        intersectionChecked = true;
    }

    private void syncBeamEffect(ServerPlayerEntity player, World world, Vec3d start, Vec3d end, Vec3d velocity, float scale, int maxTime)
    {
        if (world.isClient)
            throw new IllegalStateException("packet create called on the client!");

        PacketByteBuf byteBuf = GraphicsEffects.createPacket(NMGraphicsEffects.PHAGE_RAY, world);

        PacketBufUtil.writeVec3d(byteBuf, start);
        PacketBufUtil.writeVec3d(byteBuf, end);
        PacketBufUtil.writeVec3d(byteBuf, velocity);
        byteBuf.writeFloat(scale);
        byteBuf.writeInt(maxTime);
        byteBuf.writeVarInt(getId());

        ServerPlayNetworking.send(player, GraphicsEffects.CHANNEL_ID, byteBuf);

    }

    @Override
    protected void initDataTracker()
    {
        dataTracker.startTracking(RUNNING, false);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt)
    {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt)
    {

    }

    @Nullable
    @Override
    public Entity getPrimaryPassenger()
    {
        return getFirstPassenger();
    }

    @Override
    public void tick()
    {
        super.tick();
        if (!world.isClient() && (age > 5 && (parent == null || parent.isRemoved())))
        {
            remove(RemovalReason.DISCARDED);
        }

        if (isLogicalSideForUpdatingMovement() && getFirstPassenger() != null)
        {
            this.prevYaw = getYaw();
            this.prevPitch = getPitch();
            this.setYaw(getFirstPassenger().getYaw());
            this.setPitch(limitPitch(getFirstPassenger().getPitch()));
            this.setRotation(this.getYaw(), this.getPitch());
        }

        if (world.isClient())
        {
            clientTick();
        }
        else
        {
            // Update running state
            boolean canRun = parent != null && parent.canRun();

            if (dataTracker.get(RUNNING) != canRun)
            {
                dataTracker.set(RUNNING, canRun);
            }
        }

        if (isRunning() && trigger)
        {
            if (triggerTicks >= 20)
            {
                if (!world.isClient())
                {
                    spawnBeams();
                    breakBlocks();
                }
            }
            ++triggerTicks;

            if (!hasPlayerRider())
                trigger = false;
        }
        else
        {
            triggerTicks = 0;
        }

    }

    private void spawnBeams()
    {
        int beamInterval = 10;
        if (triggerTicks % beamInterval == 0)
        {
            for (ServerPlayerEntity player : PlayerLookup.tracking(this))
            {
                syncBeamEffect(player, world,
                        getBeamOrigin(), getBeamEnd(), Vec3d.ZERO, 1.2f, beamInterval);
            }
        }
    }

    public boolean trigger()
    {
        return trigger;
    }

    private final HashMap<BlockPos, Float> targets = Maps.newHashMap();

    private Set<BlockPos> getTargets(Vec3d origin, Vec3d end)
    {
        Vec3d v = new Vec3d(0, 0.5, 0);
        Vec3d u = getRotationVector().crossProduct(v).normalize().multiply(0.5);

        var starts = List.of(
                origin.subtract(u).subtract(v),
                origin.subtract(u).add(v),
                origin.add(u).add(v),
                origin.add(u).subtract(v)
        );

        var ends = List.of(
                end.subtract(u).subtract(v),
                end.subtract(u).add(v),
                end.add(u).add(v),
                end.add(u).subtract(v)
        );

        Set<BlockPos> newTargets = Sets.newHashSet();
        for (int i = 0; i < 4; i++)
        {
            RaycastContext context = new RaycastContext(
                    starts.get(i),
                    ends.get(i),
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    this);

            BlockHitResult result = world.raycast(context);

            if (result.getType() == HitResult.Type.BLOCK)
            {
                newTargets.add(result.getBlockPos());
                targets.putIfAbsent(result.getBlockPos(), 0.0f);
            }
        }

        return newTargets;
    }

    private void breakBlocks()
    {
        if (hasPassengers() && getFirstPassenger() != null)
        {
            if (world.getTime() % 2 == 0)
            {
                Set<BlockPos> newTargets = getTargets(getBeamOrigin(), getBeamEnd());

                targets.entrySet().removeIf(e -> !newTargets.contains(e.getKey()));
            }

            Iterator<Map.Entry<BlockPos, Float>> it = targets.entrySet().iterator();
            while (it.hasNext())
            {
                var target = it.next();
                if (target.getValue() >= 1)
                {
                    world.breakBlock(target.getKey(), false);
                    it.remove();
                }
                else
                {
                    BlockState state = world.getBlockState(target.getKey());
                    target.setValue(target.getValue() + calcBlockBreakingDelta(state, world, target.getKey()));
                }
            }
        }
    }

    public float calcBlockBreakingDelta(BlockState state, BlockView world, BlockPos pos)
    {
        float f = state.getHardness(world, pos);
        if (f == -1.0f)
        {
            return 0.0f;
        }

        return 20 / f / 30f;
    }

    private void setPlayerTrigger(boolean trigger)
    {
        this.trigger = trigger;
    }

    public Vec3d getBeamOrigin()
    {
        return getPos().add(0, 1.5, 0);
    }

    public Vec3d getBeamEnd()
    {
        return getBeamOrigin().add(getRotationVector().multiply(range));
    }

    public Vec3d getClientBeamEnd(float tickDelta)
    {
        return getBeamOrigin().add(getRotationVec(tickDelta).multiply(range));
    }

    private float limitPitch(float pitch)
    {
        return MathHelper.clamp(pitch, -90, 30);
    }

    public boolean canHit()
    {
        return !this.isRemoved();
    }

    @Override
    protected void addPassenger(Entity passenger)
    {
        super.addPassenger(passenger);
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger)
    {
        return getPos().add(0, getHeight(), 0);
//        Vec3d vec3d = AbstractVehicleEntity.getPassengerDismountOffset(this.getWidth() * MathHelper.SQUARE_ROOT_OF_TWO, passenger.getWidth(), passenger.getYaw());
//        double posX = this.getX() + vec3d.x;
//        double posZ = this.getZ() + vec3d.z;
//        BlockPos blockPos = new BlockPos(posX, this.getBoundingBox().maxY, posZ).down();
//        if (!this.world.isWater(blockPos))
//        {
//            ArrayList<Vec3d> list = Lists.newArrayList();
//            double f = this.world.getDismountHeight(blockPos);
//            if (Dismounting.canDismountInBlock(f))
//            {
//                list.add(new Vec3d(posX, blockPos.getY() + f, posZ));
//            }
//            double g = this.world.getDismountHeight(blockPos);
//            if (Dismounting.canDismountInBlock(g))
//            {
//                list.add(new Vec3d(posX, blockPos.getY() + g, posZ));
//            }
//            for (EntityPose entityPose : passenger.getPoses())
//            {
//                for (Vec3d vec3d2 : list)
//                {
//                    if (!Dismounting.canPlaceEntityAt(this.world, vec3d2, passenger, entityPose))
//                        continue;
//                    passenger.setPose(entityPose);
//                    return vec3d2;
//                }
//            }
//        }
//        return super.updatePassengerForDismount(passenger);
    }

    @Override
    public boolean canUsePortals()
    {
        return false;
    }

    @Override
    public void move(MovementType movementType, Vec3d movement)
    {
        super.move(movementType, movement);
    }

    @Override
    public boolean collidesWith(Entity other)
    {
        return super.collidesWith(other);
    }

    @Override
    public boolean isCollidable()
    {
        return true;
    }

    public boolean isRunning()
    {
        return dataTracker.get(RUNNING);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand)
    {
        if (!world.isClient())
        {
            if (hasPassengers())
                return ActionResult.PASS;

            return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void updatePassengerPosition(Entity passenger)
    {
        this.updatePassengerPosition(passenger, Entity::setPosition);
    }

    private void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater)
    {
        if (this.hasPassenger(passenger))
        {
            Vec3d v = new Vec3d(2, 0.0, 0.0).rotateY((float) (-Math.toRadians(this.getYaw())));
            v = v.add(getX(), getY() + 1, getZ());
            positionUpdater.accept(passenger, v.x, v.y, v.z);
        }
    }
    @Override
    public void onPassengerLookAround(Entity passenger)
    {
        super.onPassengerLookAround(passenger);
//        this.setYaw(passenger.getYaw());
//        this.setPitch(limitPitch(passenger.getPitch()));
    }
    //    @Override
//    public double getMountedHeightOffset()
//    {

//        return geth;

//    }

    @Override
    public Packet<?> createSpawnPacket()
    {
        return new EntitySpawnS2CPacket(this);
    }

    public void setParent(PhageRayBlockEntity phageRayBlockEntity)
    {
        this.parent = phageRayBlockEntity;
    }

    static
    {
        ServerPlayNetworking.registerGlobalReceiver(CHANNEL_ID, (server, player, handler, buf, responseSender) ->
        {
            boolean trigger = buf.readBoolean();
            server.execute(() ->
            {
                if (player.getVehicle() instanceof PhageRayEntity phageRay)
                {
                    phageRay.setPlayerTrigger(trigger);
                }
            });
        });
    }

    // --- Client things ---

    @Environment(EnvType.CLIENT)
    @Nullable
    private Client client;

    @Environment(EnvType.CLIENT)
    public void clientTick()
    {
        if (client == null)
            client = new Client(this);

        SoundManager manager = MinecraftClient.getInstance().getSoundManager();
        if (isRunning() && trigger && triggerTicks == 0)
        {
            manager.play(new EntityTrackingSoundInstance(NMSounds.PHAGE_RAY_CHARGE, SoundCategory.BLOCKS, 16, 1, this, 0));
        }

        if (isRunning() && triggerTicks >= 20 && !manager.isPlaying(client.runningInstance))
        {
            manager.play(client.runningInstance);
        }
        else if (triggerTicks == 0 && manager.isPlaying(client.runningInstance))
        {
            manager.stop(client.runningInstance);
        }
    }

    @Environment(EnvType.CLIENT)
    private static class TrackingSoundInstance extends EntityTrackingSoundInstance
    {
        public TrackingSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, Entity entity)
        {
            super(sound, category, volume, pitch, entity, 0);
            this.repeat = true;
            this.repeatDelay = 0;
        }
    }

    @Nullable
    @Override
    public ItemStack getPickBlockStack()
    {
        return NMBlocks.PHAGE_RAY.asItem().getDefaultStack();
    }

    @Environment(EnvType.CLIENT)
    public static class Client
    {
        private static boolean prevUse;

        private final TrackingSoundInstance runningInstance;

        public Client(PhageRayEntity parent)
        {
            runningInstance = new TrackingSoundInstance(
                    NMSounds.PHAGE_RAY_RUNNING, SoundCategory.BLOCKS,
                    16, 1,
                    parent);
        }

        public static void init()
        {
            InputEvents.POST_INPUT.register((window, key, scancode, action, modifiers) ->
            {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player == null)
                    return;

                if (client.player.getVehicle() instanceof PhageRayEntity phageRay)
                {
                    if (client.options.useKey.isPressed())
                    {
                        if (!prevUse)
                        {
                            phageRay.setPlayerTrigger(true);
                            sendPacket(client.player, true);
                            prevUse = true;
                        }
                    }
                    else
                    {
                        if (prevUse)
                        {
                            phageRay.setPlayerTrigger(false);
                            sendPacket(client.player, false);
                            prevUse = false;
                        }
                    }
                }
            });

            UseAttackCallback.DO_USE.register(client ->
            {
                if (client.player.getVehicle() instanceof PhageRayEntity phageRay)
                {
                    return false;
                }
                return true;
            });
        }

        private static void sendPacket(PlayerEntity player, boolean trigger)
        {
            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeBoolean(trigger);

            ClientPlayNetworking.send(CHANNEL_ID, buf);
        }

    }
}
