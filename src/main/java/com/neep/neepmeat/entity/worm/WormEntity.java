package com.neep.neepmeat.entity.worm;

import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Monster;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Arm;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WormEntity extends LivingEntity implements Monster, IAnimatable
{
    protected static final TrackedData<String> CURRENT_ACTION = DataTracker.registerData(WormEntity.class, TrackedDataHandlerRegistry.STRING);

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    private WormAction currentAction;

    protected List<WormSegment> segments = new ArrayList<>(16);

    public WormEntity(EntityType<? extends WormEntity> type, World world)
    {
        super(type, world);
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

        if (currentAction != null)
        {
            currentAction.tick();
            dataTracker.set(CURRENT_ACTION, currentAction.getId().toString());
            if (currentAction.isFinished())
            {
                currentAction = chooseAction().create(this);
            }
        }
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
    public Iterable<ItemStack> getArmorItems()
    {
        return Collections.<ItemStack>emptyList();
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack)
    {

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

        @Override
        protected void initDataTracker() {}

        @Override
        protected void readCustomDataFromNbt(NbtCompound nbt) {}

        @Override
        protected void writeCustomDataToNbt(NbtCompound nbt) {}

        @Override
        public boolean isPartOf(Entity entity)
        {
            return this == entity || parent == entity;
        }

        @Override
        public Packet<?> createSpawnPacket()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public EntityDimensions getDimensions(EntityPose pose)
        {
            return this.partDimensions;
        }

        @Override
        public boolean shouldSave()
        {
            return false;
        }
    }
}