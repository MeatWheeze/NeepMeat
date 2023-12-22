package com.neep.neepmeat.blockentity.integrator;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.fluid_transfer.storage.TypedFluidBuffer;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

@SuppressWarnings("UnstableApiUsage")
public class IntegratorBlockEntity extends SyncableBlockEntity implements IAnimatable
{

    protected int growthTimeRemaining = 1000;
    protected final IntegratorStorage storage;

    int totalTime = 10;
    public boolean isMature = false;
    public float facing = 0f;
    public float targetFacing = 0f;

    private final AnimationFactory factory = new AnimationFactory(this);
    private boolean hatching;

    public IntegratorBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.INTEGRATOR, pos, state);
//        inputBuffer = new TypedFluidBuffer(this, 2 * FluidConstants.BUCKET, fluidVariant -> fluidVariant.isOf(NMFluids.STILL_BLOOD), TypedFluidBuffer.Mode.INSERT_ONLY);
//        outputBuffer = new TypedFluidBuffer(this, 2 * FluidConstants.BUCKET, fluidVariant -> fluidVariant.isOf(NMFluids.STILL_ENRICHED_BLOOD), TypedFluidBuffer.Mode.EXTRACT_ONLY);
//        buffer = new MultiTypedFluidBuffer(this, List.of(inputBuffer, outputBuffer));
        this.storage = new IntegratorStorage(this);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket()
    {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt()
    {
        return createNbt();
    }

    @Override
    public void writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        tag.putInt("growth_remaining", growthTimeRemaining);
        tag.putBoolean("fully_grown", isMature);
        storage.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        growthTimeRemaining = tag.getInt("growth_remaining");
        isMature = tag.getBoolean("fully_grown");
        storage.readNbt(tag);
    }

    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, IntegratorBlockEntity be)
    {
        if (be.canGrow())
        {
            be.grow();
            be.sync();
        }
        if (be.isMature)
        {
            be.process();
        }
    }

    @Override
    public void registerControllers(AnimationData data)
    {
        data.addAnimationController(
            new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory()
    {
        return factory;
    }

    public boolean canGrow()
    {
        return !isMature && storage.immatureStorage.getAmount() > 0;
    }

    public void grow()
    {
        long decrement = FluidConstants.BUCKET / (totalTime * 20L);
//        try (Transaction transaction = Transaction.openOuter())
//        {
//            long extracted = storage.immatureStorage.grow(decrement, transaction);
//            if (extracted == decrement)
//            {
//                --growthTimeRemaining;
//                transaction.commit();
//            }
//            else
//            {
//                transaction.abort();
//            }

        if (storage.immatureStorage.getAmount() >= storage.immatureStorage.getCapacity())
            --growthTimeRemaining;

        if (growthTimeRemaining <= 0)
        {
            isMature = true;
            hatch();
        }
//        }
    }

    public void hatch()
    {
        for (PlayerEntity otherPlayer : PlayerLookup.tracking(this))
        {
        }
    }

    public void process()
    {
//        long conversionAmount = 900;
//        Transaction transaction = Transaction.openOuter();
//        if (outputBuffer.getCapacity() - outputBuffer.getAmount() >= conversionAmount)
//        {
//            long extracted = inputBuffer.extractDirect(FluidVariant.of(NMFluids.STILL_BLOOD), conversionAmount, transaction);
//            long inserted = outputBuffer.insertDirect(FluidVariant.of(NMFluids.STILL_ENRICHED_BLOOD), extracted, transaction);
//        }
//        transaction.commit();
    }

    public void showContents(ServerPlayerEntity player)
    {
        player.sendMessage(Text.of("Blood: " + storage.immatureStorage.getAmount() / (FluidConstants.BUCKET / 1000)), true);
    }

    private <E extends BlockEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event)
    {
        event.getController().transitionLengthTicks = 20;
        if (this.hatching)
        {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.integrator.hatch", true));
            hatching = false;
        }
        else
        {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.integrator.idle", true));
        }

        return PlayState.CONTINUE;
    }

    public Storage<FluidVariant> getStorage(World world, BlockPos pos, BlockState state, Direction direction)
    {
        return storage.getFluidStorage(world, pos, state, direction);
    }
}
