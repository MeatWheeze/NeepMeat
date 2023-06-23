package com.neep.neepmeat.entity.keeper;

import com.neep.meatweapons.MWItems;
import com.neep.meatweapons.item.FusionCannonItem;
import com.neep.neepmeat.util.NMMaths;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class KeeperEntity extends HostileEntity implements RangedAttackMob
{
    private final ServerBossBar bossBar = new ServerBossBar(this.getDisplayName(), BossBar.Color.RED, BossBar.Style.PROGRESS);

    private final KeeperRangedAttackGoal<KeeperEntity> rangedAttackGoal = new KeeperRangedAttackGoal<>(this, 1.0, 20, 15.0f, MWItems.FUSION_CANNON);
    private final MeleeAttackGoal meleeAttackGoal = new MeleeAttackGoal(this, 1.2, false){

    };
    protected ItemStack equipped = new ItemStack(MWItems.FUSION_CANNON);

//    protected GoalSelector targetSelector = new GoalSelector(world.getProfilerSupplier());
//    protected GoalSelector goalSelector = new GoalSelector(world.getProfilerSupplier());

    public KeeperEntity(EntityType<? extends HostileEntity> entityType, World world)
    {
        super(entityType, world);
        updateAttackType();
    }

    public static DefaultAttributeContainer.Builder createLivingAttributes()
    {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 2.5)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.0)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0);
    }

    @Override
    public float getMovementSpeed()
    {
        return 0.1f;
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt)
    {
        updateAttackType();
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initGoals()
    {
        targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));

        goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
//        goalSelector.add(2, new KeeperCritGoal(this, 1, 10));
    }

    public void updateAttackType()
    {
        if (this.world == null || this.world.isClient) return;

        this.goalSelector.remove(this.meleeAttackGoal);
        this.goalSelector.remove(this.rangedAttackGoal);

        ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, MWItems.FUSION_CANNON));
        if (itemStack.isOf(MWItems.FUSION_CANNON))
        {
            int i = 20;
            this.rangedAttackGoal.setAttackInterval(i);
            this.goalSelector.add(4, this.rangedAttackGoal);
        }
        else
        {
            this.goalSelector.add(4, this.meleeAttackGoal);
        }
    }

    @Override
    protected void mobTick()
    {
        super.mobTick();
        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player)
    {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player)
    {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        return super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        updateAttackType();
    }

    @Override
    public float getHealth()
    {
        return super.getHealth();
    }

    @Override
    public Iterable<ItemStack> getArmorItems()
    {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot)
    {
        return slot == EquipmentSlot.MAINHAND ? equipped : ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack)
    {
        if (slot == EquipmentSlot.MAINHAND) equipped = stack;
        if (!world.isClient()) updateAttackType();
    }

    @Override
    public Arm getMainArm()
    {
        return Arm.LEFT;
    }

    @Override
    public void attack(LivingEntity target, float pullProgress)
    {
        ItemStack itemStack = getStackInHand(Hand.MAIN_HAND);
        if (!world.isClient() && itemStack.isOf(MWItems.FUSION_CANNON))
        {
            Vec2f pitchYaw = NMMaths.rectToPol(target.getPos().add(0, target.getHeight() / 2, 0).subtract(getPos().add(0, 1.4, 0)));
            ((FusionCannonItem) itemStack.getItem()).fireBeam(world, this, target, itemStack);
        }
    }


}
