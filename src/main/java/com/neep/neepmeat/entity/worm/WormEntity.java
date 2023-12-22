package com.neep.neepmeat.entity.worm;

import com.neep.meatlib.api.entity.MultiPartEntity;
import com.neep.neepmeat.util.Bezier;
import com.neep.neepmeat.util.NMMaths;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
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
import java.util.Random;

public class WormEntity extends AbstractWormPart implements MultiPartEntity<WormEntity.WormSegment>, IAnimatable
{
    protected static final TrackedData<String> CURRENT_ACTION = DataTracker.registerData(WormEntity.class, TrackedDataHandlerRegistry.STRING);

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

        double segmentHeight = 15 / 16f;
        double y = getY();
        double x = getX();
        double z = getZ();

        head.setPos(x + 5, y + 16, z);


        head.setPitch(0);
        head.setYaw(MathHelper.wrapDegrees((float) world.getTime() / 2));

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

//        if (currentAction != null)
//        {
//            currentAction.tick();
//            dataTracker.set(CURRENT_ACTION, currentAction.getId().toString());
//            if (currentAction.isFinished())
//            {
//                currentAction = chooseAction().create(this);
//            }
//        }
    }


    @Override
    public void remove(RemovalReason reason)
    {
        segments.forEach(s -> s.remove(reason));
        super.remove(reason);
    }

    @Override
    public Arm getMainArm()
    {
        return Arm.LEFT;
    }

    protected WormActions.Entry chooseAction()
    {
        return WormActions.random();
    }

    public Random getRandom()
    {
        return random;
    }

    @Override
    public void registerControllers(final AnimationData data)
    {
        data.addAnimationController(new AnimationController<>(this, "Controller", 5, this::controller));
    }

    protected PlayState controller(final AnimationEvent<WormEntity> event)
    {
//        String anim = WormActions.getAnimation(dataTracker.get(CURRENT_ACTION));
        event.getController().setAnimation(WormActions.getAnimation(dataTracker.get(CURRENT_ACTION)));

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
        protected void initDataTracker()
        {
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