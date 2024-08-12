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

public class SliderScreenHandler extends BasicScreenHandler
{
    public final ChannelManager<MouseScroll> incrementC2S;
    public final ChannelManager<UpdateParams> updateParamsS2C;
    private final Slider slider;

    protected SliderScreenHandler(@Nullable ScreenHandlerType<?> type, PlayerInventory playerInventory, int syncId, Slider slider)
    {
        super(type, playerInventory, null, syncId, null);

        incrementC2S = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "increment"),
                ChannelFormat.builder(MouseScroll.class).param(ParamCodec.DOUBLE).param(ParamCodec.BOOLEAN).build(),
                playerInventory.player);

        updateParamsS2C = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "update_params"),
            ChannelFormat.builder(UpdateParams.class).param(ParamCodec.INT).param(ParamCodec.INT).param(ParamCodec.INT).param(ParamCodec.INT).build(),
                playerInventory.player);
        this.slider = slider;

        incrementC2S.receiver(slider::largeIncrement);
    }

    public SliderScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(ScreenHandlerInit.SLIDER, playerInventory, syncId, Slider.EMPTY);
    }

    public SliderScreenHandler(int syncId, PlayerInventory playerInventory, Slider slider)
    {
        this(ScreenHandlerInit.SLIDER, playerInventory, syncId, slider);
    }

    @Override
    public void onClosed(PlayerEntity player)
    {
        super.onClosed(player);
        incrementC2S.close();
        updateParamsS2C.close();
    }

    @Override
    public void sendContentUpdates()
    {
        super.sendContentUpdates();
        updateParamsS2C.emitter().update(slider.getValue(), slider.getMinValue(), slider.getMaxValue(), slider.getDivisions());
    }

    @FunctionalInterface
    public interface MouseScroll
    {
        void scroll(double amount, boolean large);
    }

    public interface UpdateParams
    {
        void update(int value, int min, int max, int divisions);
    }
}
