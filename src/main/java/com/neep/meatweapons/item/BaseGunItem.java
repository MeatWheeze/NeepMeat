package com.neep.meatweapons.item;

import com.neep.meatlib.item.IMeatItem;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.Util;
import com.neep.meatweapons.init.GraphicsEffects;
import com.neep.meatweapons.network.BeamPacket;
import com.neep.meatweapons.network.MWNetwork;
import com.neep.meatweapons.particle.GraphicsEffect;
import com.neep.neepmeat.NMItemGroups;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.*;
import java.util.function.Predicate;

public abstract class BaseGunItem extends Item implements IMeatItem, IAnimatable, ISyncable
{
    public AnimationFactory factory = new AnimationFactory(this);
    Map<GunSounds, SoundEvent> sounds = new EnumMap<GunSounds, SoundEvent>(GunSounds.class);
    public Item ammunition;
    public boolean hasLore;
    public final int maxShots;
    public final int cooldown;
    public static final int ANIM_FIRE = 0;
    public static final int ANIM_RELOAD = 1;
    protected final Random rand = new Random(0);
    protected String registryName;

    public BaseGunItem(String registryName, Item ammunition, int maxShots, int cooldown, boolean hasLore, FabricItemSettings settings)
    {
        super(settings.group(NMItemGroups.WEAPONS).maxCount(1).maxDamage(maxShots).maxDamageIfAbsent(maxShots));
        this.registryName = registryName;

        this.ammunition = ammunition;
        this.maxShots = maxShots;
        this.hasLore = hasLore;
        this.cooldown = cooldown;
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
        if (hasLore)
        {
            tooltip.add(new TranslatableText("item." + MeatWeapons.NAMESPACE + "." + registryName + ".lore"));
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        return UseAction.NONE;
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
        fire(world, user, itemStack);
        return TypedActionResult.fail(itemStack);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference)
    {
        if (cursorStackReference.get().getItem().equals(ammunition) && stack.getDamage() != 0)
        {
            this.reload(player, stack);
            cursorStackReference.get().decrement(1);
            return true;
        }
        return false;
    }

    public abstract void fire(World world, PlayerEntity user, ItemStack stack);

    public abstract Vec3d getMuzzleOffset(PlayerEntity player, ItemStack stack);

    // Should only be called on server.
    public void reload(PlayerEntity user, ItemStack stack)
    {
        user.getItemCooldownManager().set(this, 7);
        ItemStack ammo = getStack(this.ammunition, user);
        if (ammo != null)
        {
            stack.setDamage(0);
            ammo.decrement(1);

            if (!user.world.isClient)
            {
                // Do not sync reload animation with other players; it looks silly.
                final int id = GeckoLibUtil.guaranteeIDForStack(stack, (ServerWorld) user.world);
                GeckoLibNetwork.syncAnimation(user, this, id, ANIM_RELOAD);

                playSound(user.world, user, GunSounds.RELOAD);
            }
        }
    }

    public Optional<LivingEntity> hitScan(PlayerEntity caster, Vec3d start, Vec3d end, double distance)
    {
        World world = caster.world;
        if (!world.isClient)
        {
            // Find where the ray hits a block
            RaycastContext ctx = new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, caster);
            BlockHitResult blockResult = world.raycast(ctx);

            Predicate<Entity> entityFilter = entity -> !entity.isSpectator() && entity.collides() && entity instanceof LivingEntity;

            double minDistance = distance;
            Entity entity = null;
            EntityHitResult entityResult = null;
            for (EntityHitResult result : Util.getRayTargets(caster, start, blockResult.getPos(), entityFilter, 0.1))
            {
                if (result.getPos().distanceTo(start) < minDistance)
                {
                    minDistance = result.getPos().distanceTo(start);
                    entity = result.getEntity();
                    entityResult = result;
                }
            }

            Vec3d hitPos = Objects.requireNonNullElse(entityResult, blockResult).getPos();
            syncBeamEffect((ServerWorld) world, start, hitPos, new Vec3d(0, 0, 0), 0.2f, 9, GraphicsEffects.BEAM, 100);

            return Optional.ofNullable((LivingEntity) entity);
        }
        return Optional.empty();
    }

    public void syncBeamEffect(ServerWorld world, Vec3d pos, Vec3d end, Vec3d velocity, float width, int maxTime, GraphicsEffect.Factory type, double showRadius)
    {
        for (ServerPlayerEntity player : PlayerLookup.around(world, pos, showRadius))
        {
            Packet<?> packet = BeamPacket.create(world, type, pos, end, velocity, width, maxTime, MWNetwork.EFFECT_ID);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, packet);
        }
    }

    // Removes ammunition from inventory. Returns null if none present.
    public ItemStack getStack(Item type, PlayerEntity player)
    {
        for (int i = 0; i < player.getInventory().size(); ++i)
        {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem().equals(type))
            {
                return stack;
            }
        }
        return null;
    }

    public void playSound(World world, PlayerEntity player, GunSounds sound)
    {
        if (sounds.containsKey(sound))
        {
            world.playSound(
                    null,
                    player.getBlockPos(),
                    sounds.get(sound),
                    SoundCategory.PLAYERS,
                    1f,
                    1f
            );
        }
    }

    public enum GunSounds
    {
        FIRE_PRIMARY,
        RELOAD,
        EMPTY,
    }
}
