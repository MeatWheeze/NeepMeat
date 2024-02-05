package com.neep.neepmeat.plc.screen;

import com.neep.neepmeat.client.screen.plc.RecordMode;
import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.network.ScreenPropertyC2SPacket;
import com.neep.neepmeat.plc.block.entity.PLCBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;

public class PLCScreenHandler extends ScreenHandler
{
    private final PropertyDelegate delegate;
    private final PLCBlockEntity plc;
    private final String initialText;

    // Client
    public PLCScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf)
    {
        this(syncId,
                (PLCBlockEntity) playerInventory.player.world.getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(PLCBlockEntity.PLCPropertyDelegate.SIZE),
                buf.readString()
        );
    }

    // Server
    public PLCScreenHandler(int syncId, PLCBlockEntity plc, PropertyDelegate delegate, String source)
    {
        super(ScreenHandlerInit.PLC, syncId);
        this.plc = plc;
        this.delegate = delegate;
        this.initialText = source;
        addProperties(delegate);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index)
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
        ScreenPropertyC2SPacket.send(PLCBlockEntity.PLCPropertyDelegate.Names.SELECTED_INSTRUCTION.ordinal(), index);
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
}
