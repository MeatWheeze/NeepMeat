package com.neep.neepmeat.neepbus.screen;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.api.network.ParamCodec;
import com.neep.meatlib.network.ChannelManager;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.neepbus.part.Slider;
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
    public final SliderConfigHandler sliderConfig;

    private final Slider slider;

    private boolean updateToClient = true;

    protected SliderScreenHandler(@Nullable ScreenHandlerType<?> type, PlayerInventory playerInventory, int syncId, Slider slider)
    {
        super(type, playerInventory, null, syncId, null);

        incrementC2S = ChannelManager.create(new Identifier(NeepMeat.NAMESPACE, "increment"),
                ChannelFormat.builder(MouseScroll.class).param(ParamCodec.DOUBLE).param(ParamCodec.BOOLEAN).build(),
                playerInventory.player);

        sliderConfig = new SliderConfigHandler(playerInventory.player, slider);

        this.slider = slider;

        incrementC2S.receiver(this::increment);
    }

    public SliderScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(ScreenHandlerInit.SLIDER, playerInventory, syncId, Slider.EMPTY);
    }

    public SliderScreenHandler(int syncId, PlayerInventory playerInventory, Slider slider)
    {
        this(ScreenHandlerInit.SLIDER, playerInventory, syncId, slider);
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
            sliderConfig.updateParamsS2C.emitter().update(List.of(slider.getValue(), slider.getMinValue(), slider.getMaxValue(), slider.getInterval()));
            updateToClient = false;
        }
    }

    @Override
    public void onClosed(PlayerEntity player)
    {
        super.onClosed(player);
        incrementC2S.close();

        sliderConfig.close();
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
