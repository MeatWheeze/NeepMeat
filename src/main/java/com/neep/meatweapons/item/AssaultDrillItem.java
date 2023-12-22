package com.neep.meatweapons.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multimap;
import com.neep.meatlib.api.event.InputEvents;
import com.neep.meatlib.item.CustomEnchantable;
import com.neep.meatlib.item.MeatlibItem;
import com.neep.meatlib.item.PoweredItem;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.entity.BulletDamageSource;
import com.neep.neepmeat.api.item.OverrideSwingItem;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.implant.item.ShieldUpgrade;
import com.neep.neepmeat.init.NMComponents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.core.manager.SingletonAnimationFactory;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AssaultDrillItem extends Item implements MeatlibItem, IAnimatable, ISyncable, PoweredItem, CustomEnchantable, OverrideSwingItem
{
    public AnimationFactory factory = new SingletonAnimationFactory(this);
    protected String registryName;

    protected float attackDamage;

    public final String controllerName = "controller";
    private final TagKey<Block> effectiveBlocks;
    private final float miningSpeed;

    public AssaultDrillItem(String registryName, int maxDamage, FabricItemSettings settings)
    {
        super(settings.maxCount(1).maxDamage(maxDamage));
        this.registryName = registryName;

        this.attackDamage = 1;
        this.effectiveBlocks = BlockTags.PICKAXE_MINEABLE;
        this.miningSpeed = ToolMaterials.DIAMOND.getMiningSpeedMultiplier();

        GeckoLibNetwork.registerSyncable(this);
        ItemRegistry.queue(this);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        tooltip.add(Text.translatable("item." + MeatWeapons.NAMESPACE + "." + registryName + ".lore"));
        tooltip.add(Text.translatable("item." + MeatWeapons.NAMESPACE + "." + registryName + ".damage_per_tick", getDamage(itemStack, null) / 2f).formatted(Formatting.BLUE));
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack)
    {
        return false;
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        return UseAction.NONE;
    }

    @Override
    public void registerControllers(AnimationData animationData)
    {
        animationData.addAnimationController(new AnimationController(this, controllerName, 1, this::predicate));
    }

    public AnimationFactory getFactory()
    {
        return this.factory;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);

        if (!world.isClient())
        {
            final int id = GeckoLibUtil.guaranteeIDForStack(user.getStackInHand(hand), (ServerWorld) world);
            world.getPlayers().forEach(p -> GeckoLibNetwork.syncAnimation(user, this, id, 0));
        }

        itemStack.getOrCreateNbt().putBoolean("using", true);

        if (user instanceof ServerPlayerEntity player && world instanceof ServerWorld serverWorld)
        {
            BlockPos targetPos = raycast(serverWorld, player, RaycastContext.FluidHandling.NONE).getBlockPos();
//            player.interactionManager.processBlockBreakingAction(targetPos, PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, Direction.DOWN, player.world.getTopY());
        }

        return TypedActionResult.fail(itemStack);
    }

    private final EntityAttributeModifier eam = new EntityAttributeModifier("aa", 8, EntityAttributeModifier.Operation.ADDITION);

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot)
    {
        var manager = NMComponents.IMPLANT_MANAGER.get(stack);
        if (manager.getInstalled().contains(ShieldUpgrade.ID) && stack.getOrCreateNbt().getBoolean("using"))
        {
            return ImmutableMultimap.of(EntityAttributes.GENERIC_ARMOR, eam);
        }
        return super.getAttributeModifiers(stack, slot);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
    {
        boolean canUse = true;
        if (stack.getDamage() >= getMaxDamage(stack))
        {
            stack.getOrCreateNbt().putBoolean("using", false);
            canUse = false;
        }

        double radius = 1.3;
        double distance = 1.2;
        Vec3d tip = user.getEyePos().add(user.getRotationVec(1).normalize().multiply(distance));
        Box box = Box.of(tip, radius, radius, radius);
        if (world instanceof ServerWorld serverWorld && canUse)
        {
            // Damage all entities within a small box at the end of the drill model
            List<Entity> entities = world.getOtherEntities(user, box, e -> true);
            if (!entities.isEmpty())
            {
                entities.forEach(entity ->
                {
                    if (entity instanceof LivingEntity && entity.isAlive() && user instanceof PlayerEntity player)
                    {
                        // Set entity damage cooldown to 4 ticks (20 - 14)
                        entity.damage(BulletDamageSource.create(player, 0.04f, 14), getDamage(stack, entity));

                        // Spawn blood particles
                        serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.NETHER_WART_BLOCK.getDefaultState()), tip.x, tip.y, tip.z, 3, 0.05, 0.05, 0.05, 0.2);
                    }
                });
            }
            else
            {
            }

            // Reduce durability
            if (!(user instanceof PlayerEntity player && player.isCreative())
                && stack.getDamage() < getMaxDamage(stack))
            {
                stack.setDamage(stack.getDamage() + 1);
            }
        }


        super.usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner)
    {
        return true;
    }

    public int getMaxDamage(ItemStack stack)
    {
        return stack.getMaxDamage();
    }


    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack)
    {
        return super.getTooltipData(stack);
    }

    public static boolean using(ItemStack stack)
    {
        if (stack.getItem() instanceof AssaultDrillItem)
        {
            return stack.getOrCreateNbt().getBoolean("using");
        }
        return false;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
    {
        stack.getOrCreateNbt().putBoolean("using", false);
    }

    protected float getDamage(ItemStack stack, @Nullable Entity target)
    {
        float damage = target instanceof LivingEntity livingTarget ?
                EnchantmentHelper.getAttackDamage(stack, livingTarget.getGroup()) : EnchantmentHelper.getAttackDamage(stack, EntityGroup.DEFAULT);

        return attackDamage + damage;
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state)
    {
        return state.isIn(this.effectiveBlocks) ? this.miningSpeed * 4 : 1.0f;
    }

    @Override
    public boolean isSuitableFor(BlockState state)
    {
        return state.isIn(this.effectiveBlocks);
    }

    static
    {
        InputEvents.POST_INPUT.register((window, key, scancode, action, modifiers) ->
        {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.options != null)
            {
                ItemStack mainStack = client.player.getMainHandStack();

                if (mainStack.getItem() instanceof AssaultDrillItem drill &&
                        (client.options.attackKey.matchesKey(key, scancode)
                     || client.options.attackKey.matchesMouse(key))
                )
                {
                    if (client.options.attackKey.isPressed())
                    {
                        drill.onAttackBlock(mainStack, client.player);
                    }
                    else
                    {
                        drill.onFinishAttackBlock(mainStack, client.player);
                    }
                }
            }
        });
    }

    @Override
    public int getItemBarStep(ItemStack stack)
    {
        return super.getItemBarStep(stack);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack)
    {
        return true;
    }

    @Override
    public boolean isDamageable()
    {
        return false;
    }

    @Override
    public int getItemBarColor(ItemStack stack)
    {
        return MathHelper.hsvToRgb(0.5f, 0.75f, 0.75f);
    }

    @Override
    public boolean isEnchantable(ItemStack stack)
    {
        return true;
    }

    @Override
    public int getEnchantability()
    {
        return 1;
    }

    @Override
    public void onAnimationSync(int id, int state)
    {
        if (state == 0)
        {
            final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, controllerName);
            if (controller.getCurrentAnimation() == null || !Objects.equals(controller.getCurrentAnimation().animationName, "animation.assault_drill.spin"))
            {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("animation.assault_drill.spin"));
            }
        }
    }

    public void onAttackBlock(ItemStack stack, PlayerEntity player)
    {
        stack.getOrCreateNbt().putBoolean("attacking", true);
    }

    public void onFinishAttackBlock(ItemStack stack, PlayerEntity player)
    {
        stack.getOrCreateNbt().putBoolean("attacking", false);
    }

    protected <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event)
    {
        return PlayState.CONTINUE;
    }

    @Override
    public boolean onSwing(ItemStack stack, PlayerEntity player)
    {
        player.handSwingProgress = 0;
        return false;
    }

    public static Storage<FluidVariant> getStorage(ItemStack stack, ContainerItemContext containerItemContext)
    {
        return new InternalStorage(stack, containerItemContext);
    }

    @SuppressWarnings("UnstableApiUsage")
    protected static class InternalStorage extends SnapshotParticipant<Integer> implements InsertionOnlyStorage<FluidVariant>, StorageView<FluidVariant>
    {
        protected static final int ENERGY_PER_DURABILITY = 10;
        protected final ItemStack stack;
        protected final ContainerItemContext context;

        public InternalStorage(ItemStack stack, ContainerItemContext context)
        {
            this.stack = stack.copy();
            this.context = context;
        }

        @Override
        public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction)
        {
            long energy = PowerUtils.amountToAbsEnergy(maxAmount, insertedVariant.getFluid());
            int currentEnergy = (stack.getMaxDamage() - stack.getDamage()) * ENERGY_PER_DURABILITY;
            long insertedEnergy = Math.min(energy, energyCapacity() - currentEnergy);
            if (insertedEnergy > 0)
            {
                updateSnapshots(transaction);
                int insertedDamage = (int) (insertedEnergy / ENERGY_PER_DURABILITY);
                stack.setDamage(stack.getDamage() - insertedDamage);
                context.exchange(ItemVariant.of(stack), 1, transaction);
            }
            return PowerUtils.absToAmount(insertedVariant.getFluid(), insertedEnergy);
        }

        @Override
        public boolean isResourceBlank()
        {
            return true;
        }

        @Override
        public FluidVariant getResource()
        {
            return FluidVariant.blank();
        }

        @Override
        public long getAmount()
        {
            return 0;
        }

        @Override
        public long getCapacity()
        {
            return 0;
        }

        protected int energyCapacity()
        {
            return stack.getMaxDamage() * ENERGY_PER_DURABILITY;
        }

        @Override
        public Iterator<StorageView<FluidVariant>> iterator()
        {
            return Iterators.singletonIterator(this);
        }

        @Override
        protected Integer createSnapshot()
        {
            return (stack.getMaxDamage() - stack.getDamage()) * ENERGY_PER_DURABILITY ;
        }

        @Override
        protected void readSnapshot(Integer snapshot)
        {
            stack.setDamage(stack.getMaxDamage() - snapshot / ENERGY_PER_DURABILITY);
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction)
        {
            return 0;
        }

    }
}