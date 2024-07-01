package com.neep.neepmeat.item;

import com.neep.meatlib.item.ClientBlockAttackListener;
import com.neep.neepmeat.datagen.tag.NMTags;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RockDrillItem extends Item implements ClientBlockAttackListener
{
    public static final Identifier CHANNEL_ID = new Identifier("rock_drill");

    public RockDrillItem(Settings settings)
    {
        super(settings);
//        super(settings.maxDamage(500);
    }

    public static boolean using(ItemStack stack)
    {
        if (stack.getItem() instanceof RockDrillItem && stack.hasNbt())
        {
            NbtCompound nbt = stack.getNbt();
            return nbt.getBoolean("attacking");
        }
        return false;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner)
    {
        if (!world.isClient && world.getTime() % 2 == 0)
        {
//            stack.damage(1, miner, e -> {});
            world.playSoundFromEntity(null, miner, NMSounds.ROCK_DRILL, SoundCategory.PLAYERS, 1f, 1);
        }
        return true;
    }

    private void sendAttack(boolean attacking)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(attacking);
        ClientPlayNetworking.send(CHANNEL_ID, buf);
    }

    @Override
    public boolean isSuitableFor(BlockState state)
    {
        return state.isIn(NMTags.ROCK_DRILL_MINEABLE);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state)
    {
        if (state.isIn(NMTags.ROCK_DRILL_MINEABLE))
        {
            return 50;
        }

        return super.getMiningSpeedMultiplier(stack, state);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner)
    {
        return state.isIn(NMTags.ROCK_DRILL_MINEABLE);
    }

    @Override
    public void onAttackBlock(ItemStack stack, PlayerEntity player)
    {
        sendAttack(true);
    }

    @Override
    public void onFinishAttackBlock(ItemStack stack, PlayerEntity player)
    {
        sendAttack(false);
    }

    static
    {
        ServerPlayNetworking.registerGlobalReceiver(CHANNEL_ID, RockDrillItem::onAttackPacket);
    }

    private static void onAttackPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
    {
        boolean attacking = buf.readBoolean();

        server.execute(() ->
        {
            ItemStack mainStack = player.getMainHandStack();
            if (mainStack.getItem() instanceof RockDrillItem)
            {
                mainStack.getOrCreateNbt().putBoolean("attacking", attacking);
            }
        });
    }
}
