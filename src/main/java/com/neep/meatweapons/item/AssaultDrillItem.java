package com.neep.meatweapons.item;

import com.google.common.collect.Lists;
import com.neep.meatlib.item.IMeatItem;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.entity.BulletDamageSource;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class AssaultDrillItem extends Item implements IMeatItem, IAnimatable, ISyncable
{
    public AnimationFactory factory = new SingletonAnimationFactory(this);
    public boolean hasLore;
    protected String registryName;

    protected float attackDamage;

    public final String controllerName = "controller";

    public AssaultDrillItem(String registryName, FabricItemSettings settings)
    {
        super(settings.maxCount(1));
        this.registryName = registryName;

        this.attackDamage = 1;

        GeckoLibNetwork.registerSyncable(this);
        ItemRegistry.queueItem(this);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        tooltip.add(new TranslatableText("item." + MeatWeapons.NAMESPACE + "." + registryName + ".lore"));
        tooltip.add(new TranslatableText("item." + MeatWeapons.NAMESPACE + "." + registryName + ".damage_per_tick", getDamage(itemStack, null)).formatted(Formatting.BLUE));
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

    public static List<EnchantmentLevelEntry> getPossibleEntries(int power, ItemStack stack, boolean treasureAllowed) {
        ArrayList<EnchantmentLevelEntry> list = Lists.newArrayList();
        Item item = stack.getItem();
        boolean bl = stack.isOf(Items.BOOK);
        block0: for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment.isTreasure() && !treasureAllowed || !enchantment.isAvailableForRandomSelection() || !enchantment.type.isAcceptableItem(item) && !bl) continue;
            for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                int minp = enchantment.getMinPower(i);
                int maxp = enchantment.getMaxPower(i);
                if (power < enchantment.getMinPower(i) || power > enchantment.getMaxPower(i)) continue;
                list.add(new EnchantmentLevelEntry(enchantment, i));
                continue block0;
            }
        }
        return list;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        var l1 = getPossibleEntries(30, user.getStackInHand(hand), false);
        var l = EnchantmentHelper.generateEnchantments(new Random(0), user.getMainHandStack(), 3, true);
        System.out.println(l1);

        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);

        if (!world.isClient())
        {
            final int id = GeckoLibUtil.guaranteeIDForStack(user.getStackInHand(hand), (ServerWorld) world);
            world.getPlayers().forEach(p -> GeckoLibNetwork.syncAnimation(user, this, id, 0));
        }

        itemStack.getOrCreateNbt().putBoolean("using", true);

        return TypedActionResult.fail(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
    {
        double radius = 1.3;
        double distance = 1.2;
        Vec3d tip = user.getEyePos().add(user.getRotationVec(1).normalize().multiply(distance));
        Box box = Box.of(tip, radius, radius, radius);
        if (world instanceof ServerWorld serverWorld)
        {
            world.getOtherEntities(user, box, e -> true).forEach(entity ->
            {
//                entity.getBoundingBox().raycast(user.getEyePos(), user.getEyePos()
//                        .add(user.getRotationVec(1f).normalize().multiply(range))).ifPresent(v ->
                {
                    if (entity instanceof LivingEntity && entity.isAlive() && user instanceof PlayerEntity player)
                    {
                        entity.damage(BulletDamageSource.create(player, 0.04f, 13), getDamage(stack, entity));

                        serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.NETHER_WART_BLOCK.getDefaultState()), tip.x, tip.y, tip.z, 3, 0.02, 0.02, 0.02, 0.2);
                    }
                }
//                );
            });
        }

        super.usageTick(world, user, stack, remainingUseTicks);
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


    @Nullable
    @Override
    public SoundEvent getEquipSound()
    {
        return super.getEquipSound();
    }

    protected float getDamage(ItemStack stack, @Nullable Entity target)
    {
        float damage = target instanceof LivingEntity livingTarget ?
                EnchantmentHelper.getAttackDamage(stack, livingTarget.getGroup()) : EnchantmentHelper.getAttackDamage(stack, EntityGroup.DEFAULT);

        return attackDamage + damage;
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
            controller.markNeedsReload();
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.assault_drill.spin"));
        }
    }

    protected <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event)
    {
        return PlayState.CONTINUE;
    }
}