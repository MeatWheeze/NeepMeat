package com.neep.neepmeat.entity.worm;

import com.neep.meatlib.api.entity.MultiPartEntity;
import com.neep.neepmeat.util.Bezier;
import com.neep.neepmeat.util.Easing;
import com.neep.neepmeat.util.NMMaths;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

import static com.neep.neepmeat.network.NMTrackedData.DOUBLE;

public class WormEntity extends AbstractWormPart implements MultiPartEntity<WormEntity.WormSegment>, IAnimatable
{
    protected static final TrackedData<String> CURRENT_ACTION = DataTracker.registerData(WormEntity.class, TrackedDataHandlerRegistry.STRING);
    protected static final TrackedData<Double> HEAD_X = DataTracker.registerData(WormEntity.class, DOUBLE);
    protected static final TrackedData<Double> HEAD_Y = DataTracker.registerData(WormEntity.class, DOUBLE);
    protected static final TrackedData<Double> HEAD_Z = DataTracker.registerData(WormEntity.class, DOUBLE);
    protected static final TrackedData<Float> HEAD_PITCH = DataTracker.registerData(WormEntity.class, TrackedDataHandlerRegistry.FLOAT);
    protected static final TrackedData<Float> HEAD_YAW = DataTracker.registerData(WormEntity.class, TrackedDataHandlerRegistry.FLOAT);

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private WormAction currentAction;
    protected List<WormSegment> segments = new ArrayList<>(17);
    protected List<WormSegment> tail = new ArrayList<>(16);
    public final WormSegment head;


    public WormEntity(EntityType<? extends WormEntity> type, World world)
    {
        super(type, world);
        int length = 16;
        for (int i = 0; i < length; ++i)
        {
            WormSegment segment = new WormSegment(this, 26 / 16f, 16);
            tail.add(segment);
        }
        head = new WormSegment(this, 26 / 16f, 16);
        segments.addAll(tail);
        segments.add(head);

//        segments.forEach(world::spawnEntity);
    }

    @Override
    protected void initDataTracker()
    {
        super.initDataTracker();
        dataTracker.startTracking(CURRENT_ACTION, WormAction.EmptyAction.ID.toString());

        dataTracker.startTracking(HEAD_X, getX());
        dataTracker.startTracking(HEAD_Y, getY() + 15);
        dataTracker.startTracking(HEAD_Z, getZ());
        dataTracker.startTracking(HEAD_PITCH, -90f);
        dataTracker.startTracking(HEAD_YAW, 0f);
    }

    public static DefaultAttributeContainer.Builder createLivingAttributes()
    {
        return DefaultAttributeContainer.builder().add(EntityAttributes.GENERIC_MAX_HEALTH).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).add(EntityAttributes.GENERIC_MOVEMENT_SPEED).add(EntityAttributes.GENERIC_ARMOR).add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).add(EntityAttributes.GENERIC_FOLLOW_RANGE);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);

        nbt.put("currentAction", WormActions.toNbt(currentAction));
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);

        currentAction = WormActions.fromNbt(nbt.getCompound("currentAction"), this);
    }

    @Override
    public Packet<?> createSpawnPacket()
    {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public void tick()
    {
        super.tick();
        setPitch(-90);

        double y = getY();
        double x = getX();
        double z = getZ();

//        head.setPos(x + 5, y + 16, z);
//        updateHead();
//        float radius = 8;
//        float angle = MathHelper.wrapDegrees((float) world.getTime() / 3);

        if (!world.isClient())
        {
//            setHeadPos(
//                    getX() + radius * Math.cos(Math.toRadians(angle)),
//                    getY() + 5,
//                    getZ() + radius * Math.sin(Math.toRadians(angle)));
//            setHeadAngles(0, angle - 90);
        }
        else updateHead();

        Vec3d headLook = head.getPos().add(Vec3d.fromPolar(head.getPitch(), head.getYaw()).multiply(-8));

        for (int i = 0; i < tail.size(); ++i)
        {
            WormSegment segment = tail.get(i);
            float delta = ((float) i) / tail.size();

            double x1 = Bezier.bezier3(delta, x, x, headLook.x, head.getX());
            double y1 = Bezier.bezier3(delta, y, y + 5, headLook.y, head.getY());
            double z1 = Bezier.bezier3(delta, z, z, headLook.z, head.getZ());

            double u = Bezier.derivative3(delta, x, x, headLook.x, head.getX());
            double v = Bezier.derivative3(delta, y, y + 5, headLook.y, head.getY());
            double w = Bezier.derivative3(delta, z, z, headLook.z, head.getZ());

            Vec2f pitchYaw = NMMaths.rectToPol(u, v, w);

//            float pitch1 = MathHelper.lerp(delta, pitch, head.getPitch());
//            float yaw1 = MathHelper.lerp(delta, yaw, head.getYaw());

            segment.setPos(x1, y1, z1);
            segment.setPitch(pitchYaw.x);
            segment.setYaw(pitchYaw.y);
//            segment.setYaw(0);
        }

        if (currentAction != null && !world.isClient())
        {
            currentAction.tick();
            dataTracker.set(CURRENT_ACTION, currentAction.getId().toString());
            if (currentAction.isFinished())
            {
                currentAction = chooseAction().create(this);
            }
        }
    }

    protected Vec3d getNeutralHeadPos()
    {
        return new Vec3d(getX(), getY() + 15, getZ());
    }

    private Vec2f getNeutralHeadPitch()
    {
        return new Vec2f(-90, 0);
    }

    protected void updateHead()
    {
        double x = dataTracker.get(HEAD_X);
        double y = dataTracker.get(HEAD_Y);
        double z = dataTracker.get(HEAD_Z);
        float pitch = dataTracker.get(HEAD_PITCH);
        float yaw = dataTracker.get(HEAD_YAW);
        head.updatePositionAndAngles(dataTracker.get(HEAD_X), dataTracker.get(HEAD_Y), dataTracker.get(HEAD_Z),
                dataTracker.get(HEAD_YAW), dataTracker.get(HEAD_PITCH));
    }

    @Override
    public void remove(RemovalReason reason)
    {
        segments.forEach(s -> s.remove(reason));
        super.remove(reason);
    }

    protected WormActions.Entry chooseAction()
    {
        return WormActions.random();
    }

    public void setHeadPos(double x, double y, double z)
    {
        dataTracker.set(HEAD_X, x);
        dataTracker.set(HEAD_Y, y);
        dataTracker.set(HEAD_Z, z);
        head.setPos(x, y, z);
    }

    public void setHeadAngles(float pitch, float yaw)
    {
        if (!Float.isNaN(pitch))
        {
            head.setPitch(pitch);
            dataTracker.set(HEAD_PITCH, pitch);
        }
        if (!Float.isNaN(yaw))
        {
            head.setYaw(yaw);
            dataTracker.set(HEAD_YAW, yaw);
        }
    }

    @Override
    public void registerControllers(final AnimationData data)
    {
        data.addAnimationController(new AnimationController<>(this, "Controller", 5, this::controller));
    }

    protected PlayState controller(final AnimationEvent<WormEntity> event)
    {
//        String anim = WormActions.getAnimation(dataTracker.get(CURRENT_ACTION));
//        event.getController().setAnimation(WormActions.getAnimation(dataTracker.get(CURRENT_ACTION)));

        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory()
    {
        return factory;
    }

    @Override
    public Iterable<WormSegment> getParts()
    {
        return segments;
    }

    public void lerpHeadPos(int tick, int totalTicks, Vec3d toPos)
    {
        float delta = (float) tick / totalTicks;
        double headX = MathHelper.lerp(delta, head.getX(), toPos.getX());
        double headY = MathHelper.lerp(delta, head.getY(), toPos.getY());
        double headZ = MathHelper.lerp(delta, head.getZ(), toPos.getZ());

        setHeadPos(headX, headY, headZ);
    }

    public void lerpHeadPos(int tick, int totalTicks, Vec3d toPos, Easing.Curve curve)
    {
        double c = curve.apply((double) tick / totalTicks);
        double headX = head.getX() + (toPos.getX() - head.getX()) * c;
        double headY = head.getY() + (toPos.getY() - head.getY()) * c;
        double headZ = head.getZ() + (toPos.getZ() - head.getZ()) * c;

        setHeadPos(headX, headY, headZ);
    }

    public void lerpHeadAngles(int tick, int totalTicks, float toPitch, float toYaw)
    {
        float delta = (float) tick / totalTicks;
        float headPitch = MathHelper.lerp(delta, head.getPitch(), toPitch);
        float headYaw = MathHelper.lerp(delta, head.getYaw(), toYaw);

        setHeadAngles(headPitch, headYaw);
    }

    public void returnHeadToNeutral(int tick, int totalTicks)
    {
        lerpHeadPos(tick, totalTicks, getNeutralHeadPos());
        Vec2f pitchYaw = getNeutralHeadPitch();
        lerpHeadAngles(tick, totalTicks, pitchYaw.x, pitchYaw.y);
    }


    public static class WormSegment extends Entity
    {
        protected final WormEntity parent;
        protected final EntityDimensions partDimensions;

        public WormSegment(WormEntity parent, float width, float height)
        {
            super(parent.getType(), parent.world);
            this.parent = parent;
            this.partDimensions = EntityDimensions.changing(width, height);
            calculateDimensions();
        }

//        public WormSegment(EntityType<? extends WormSegment> type, World world)
//        {
//
//        }


        @Override
        protected void readCustomDataFromNbt(NbtCompound nbt)
        {
        }

        @Override
        protected void writeCustomDataToNbt(NbtCompound nbt)
        {
        }

        @Override
        public void setPitch(float pitch)
        {
            super.setPitch(pitch);
        }

        @Override
        public boolean isPartOf(Entity entity)
        {
            return this == entity || parent == entity;
        }

        @Override
        public Packet<?> createSpawnPacket()
        {
            throw new UnsupportedOperationException();
//            return new EntitySpawnS2CPacket(this);
        }

        @Override
        public void onSpawnPacket(EntitySpawnS2CPacket packet)
        {
            super.onSpawnPacket(packet);
        }

        @Override
        public EntityDimensions getDimensions(EntityPose pose)
        {
            return this.partDimensions;
        }

        @Override
        protected void initDataTracker()
        {

        }

        @Override
        public boolean damage(DamageSource source, float amount)
        {
            return parent.damage(source, amount);
        }

        @Override
        public boolean shouldSave()
        {
            return false;
        }
    }

    public enum Type
    {
        HEAD,
        TAIL
    }

}