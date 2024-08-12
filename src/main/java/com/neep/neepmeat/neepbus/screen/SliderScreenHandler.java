package com.neep.neepmeat.neepbus.screen;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.api.network.ParamCodec;
import com.neep.meatlib.network.ChannelManager;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.neepbus.Slider;
import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SliderScreenHandler extends BasicScreenHandler
{
    public final ChannelManager<MouseScroll> incrementC2S;
    public final ChannelManager<UpdateParams> updateParamsS2C;
    public final ChannelManager<UpdateParams> receiveParamsC2S;
    private final Slider slider;

    private boolean updateToClient = true;

    protected SliderScreenHandler(@Nullable ScreenHandlerType<?> type, PlayerInventory playerInventory, int syncId, Slider slider)
    {
        super(type, playerInventory, null, syncId, null);

        incrementC2S = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "increment"),
                ChannelFormat.builder(MouseScroll.class).param(ParamCodec.DOUBLE).param(ParamCodec.BOOLEAN).build(),
                playerInventory.player);

        updateParamsS2C = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "update_params"),
            ChannelFormat.builder(UpdateParams.class).param(ParamCodec.list(ParamCodec.INT)).build(),
                playerInventory.player);

        receiveParamsC2S = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "receive_params"),
                ChannelFormat.builder(UpdateParams.class).param(ParamCodec.list(ParamCodec.INT)).build(),
                playerInventory.player);

        this.slider = slider;

        incrementC2S.receiver(this::increment);
        receiveParamsC2S.receiver(this::receiveParams);
    }

    public SliderScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(ScreenHandlerInit.SLIDER, playerInventory, syncId, Slider.EMPTY);
    }

    public SliderScreenHandler(int syncId, PlayerInventory playerInventory, Slider slider)
    {
        this(ScreenHandlerInit.SLIDER, playerInventory, syncId, slider);
    }

    private void receiveParams(List<Integer> ints)
    {
        slider.setValue(ints.get(0));
        slider.setMinValue(ints.get(1));
        slider.setMaxValue(ints.get(2));
        slider.setInterval(ints.get(3));

        updateToClient = true;
    }

    private void increment(double amount, boolean large)
    {
        slider.increment(amount, large);

        updateToClient = true;
    }

    @Override
    public void sendContentUpdates()
    {
        super.sendContentUpdates();
        if (!isClient() && updateToClient)
        {
            // Send the updated (and possibly sanitised) values back to the client.
            updateParamsS2C.emitter().update(List.of(slider.getValue(), slider.getMinValue(), slider.getMaxValue(), slider.getInterval()));
            updateToClient = false;
        }
    }


    @Override
    public void onClosed(PlayerEntity player)
    {
        super.onClosed(player);
        incrementC2S.close();
        updateParamsS2C.close();
        receiveParamsC2S.close();
    }

    @FunctionalInterface
    public interface MouseScroll
    {
        void scroll(double amount, boolean large);
    }

    public interface UpdateParams
    {
//        void update(int value, int min, int max, int divisions);
        void update(List<Integer> ints);
    }
}
