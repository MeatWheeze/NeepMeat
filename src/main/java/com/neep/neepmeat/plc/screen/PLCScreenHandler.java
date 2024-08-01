package com.neep.neepmeat.plc.screen;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.api.network.ParamCodec;
import com.neep.meatlib.network.ChannelManager;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.screen.plc.RecordMode;
import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.network.ScreenPropertyC2SPacket;
import com.neep.neepmeat.network.plc.PLCSyncAction;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.block.entity.PLCBlockEntity;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class PLCScreenHandler extends ScreenHandler
{
    private final PropertyDelegate delegate;
    private final PlayerEntity player;
    private final PLCBlockEntity plc;
    private final String initialText;

    public final ChannelManager<ApplyAction> channel;
    public final ChannelManager<ChangeOperation> changeOperation;
    public final ChannelManager<ApplyArgument> applyArgument;
    public final ChannelManager<Consumer<RecordMode>> changeMode;
    public final ChannelManager<Consumer<String>> updateText;

    public final ChannelManager<ApplyCompileMessage> compileMessageS2C;
    public final ChannelManager<Consumer<NbtCompound>> updateStackS2C;

    // Client
    public PLCScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf)
    {
        this(syncId,
                playerInventory.player, (PLCBlockEntity) playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(PLCBlockEntity.PLCPropertyDelegate.SIZE),
                buf.readString()
        );
        this.delegate.set(PLCBlockEntity.PLCPropertyDelegate.Names.EDIT_MODE.ordinal(), buf.readInt());
    }

    // Server
    public PLCScreenHandler(int syncId, PlayerEntity player, PLCBlockEntity plc, PropertyDelegate delegate, String source)
    {
        super(ScreenHandlerInit.PLC, syncId);
        this.player = player;
        this.plc = plc;
        this.delegate = delegate;
        this.initialText = source;
        addProperties(delegate);

        this.channel = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "plc_sync_program"),
                ChannelFormat.builder(ApplyAction.class).param(PLCSyncAction.PARAM_CODEC).build(), player);
        this.changeOperation = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "change_operation"),
                ChannelFormat.builder(ChangeOperation.class).param(ParamCodec.IDENTIFIER).build(), player);
        this.applyArgument = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "apply_argument"),
                ChannelFormat.builder(ApplyArgument.class).param(Argument.PARAM_CODEC).build(), player);
        this.changeMode = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "change_mode"),
                ChannelFormat.builder(ThingConsumer.<RecordMode>as()).param(RecordMode.PARAM_CODEC).build(), player);
        this.updateText = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "update_text"),
                ChannelFormat.builder(ThingConsumer.<String>as()).param(ParamCodec.STRING).build(), player);

        this.compileMessageS2C = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "compile_message"),
                ChannelFormat.builder(ApplyCompileMessage.class).param(ParamCodec.STRING).param(ParamCodec.BOOLEAN).param(ParamCodec.INT).build(), player);
        this.updateStackS2C = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "update_stack"),
                ChannelFormat.builder(ThingConsumer.<NbtCompound>as()).param(ParamCodec.NBT).build(), player);

        channel.receiver(this::onApply);
        changeOperation.receiver(this::changeOperation);
        applyArgument.receiver(this::applyArgument);
        changeMode.receiver(plc::setMode);
        updateText.receiver(s -> plc.getProgramEditor().setProgramSource(s));

    }

    @Override
    public void updateToClient()
    {
        super.updateToClient();
    }

    @Override
    public void sendContentUpdates()
    {
        super.sendContentUpdates();
        updateStackS2C.emitter().accept(plc.getVariableStack().writeNbt(new NbtCompound()));
//        PLCSyncThings.sendStack((ServerPlayerEntity) player, plc.getVariableStack());
    }

    @Override
    public void syncState()
    {
        super.syncState();
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return true;
    }

    public PLCBlockEntity getPlc()
    {
        return plc;
    }

    public RecordMode getMode()
    {
        return RecordMode.values()[
                delegate.get(PLCBlockEntity.PLCPropertyDelegate.Names.EDIT_MODE.ordinal())
                ];
    }

    public boolean isRunning()
    {
        return delegate.get(PLCBlockEntity.PLCPropertyDelegate.Names.RUNNING.ordinal()) > 0;
    }

    public int getCounter()
    {
        return delegate.get(PLCBlockEntity.PLCPropertyDelegate.Names.PROGRAM_COUNTER.ordinal());
    }

    public int getMaxArguments()
    {
        return delegate.get(PLCBlockEntity.PLCPropertyDelegate.Names.MAX_ARGUMENTS.ordinal());
    }

    public int getArguments()
    {
        return delegate.get(PLCBlockEntity.PLCPropertyDelegate.Names.ARGUMENT.ordinal());
    }

    public void setSelectedInstruction(int index)
    {
        ScreenPropertyC2SPacket.Client.send(PLCBlockEntity.PLCPropertyDelegate.Names.SELECTED_INSTRUCTION.ordinal(), index);
    }

    public int getSelectedInstruction()
    {
        return delegate.get(PLCBlockEntity.PLCPropertyDelegate.Names.SELECTED_INSTRUCTION.ordinal());
    }

    public String getInitialText()
    {
        return initialText;
    }

    public int hasProgram()
    {
        return delegate.get(PLCBlockEntity.PLCPropertyDelegate.Names.HAS_PROGRAM.ordinal());
    }

    public int debugLine()
    {
        return delegate.get(PLCBlockEntity.PLCPropertyDelegate.Names.DEBUG_LINE.ordinal());
    }

    @Override
    public void onClosed(PlayerEntity player)
    {
        super.onClosed(player);
        channel.close();
        changeOperation.close();
        applyArgument.close();
        changeMode.close();
        updateText.close();

        compileMessageS2C.close();
        updateStackS2C.close();
    }

    private void onApply(PLCSyncAction action)
    {
        switch (action)
        {
            case RUN -> plc.runProgram(plc.getProgramEditor().getProgram());
            case STOP -> plc.hardStop();
            case PAUSE -> plc.pause();
            case COMPILE -> plc.getProgramEditor().compile();
        }
    }

    private void changeOperation(Identifier id)
    {
        InstructionProvider provider = Instructions.REGISTRY.get(id);
        if (provider != null)
        {
            plc.getState().setInstructionBuilder(provider);
        }
    }

    private void applyArgument(Argument argument)
    {
        plc.getState().argument(argument);
    }

    @FunctionalInterface
    public interface ApplyAction
    {
        void apply(PLCSyncAction action);
    }

    @FunctionalInterface
    public interface ChangeOperation
    {
        void apply(Identifier id);
    }

    @FunctionalInterface
    public interface ApplyArgument
    {
        void apply(Argument argument);
    }

    @FunctionalInterface
    public interface ApplyCompileMessage
    {
        void apply(String message, boolean success, int line);
    }
}
