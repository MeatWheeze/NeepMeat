package com.neep.neepmeat.neepbus.screen;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.api.network.ParamCodec;
import com.neep.meatlib.network.ChannelManager;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.neepbus.part.RangedPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public class RangeConfigHandler
{
    public final ChannelManager<SliderScreenHandler.UpdateParams> updateParamsS2C;
    public final ChannelManager<SliderScreenHandler.UpdateParams> receiveParamsC2S;
    protected final RangedPart ranged;
    protected boolean updateToClient;

    public RangeConfigHandler(PlayerEntity player, RangedPart ranged)
    {
        updateParamsS2C = ChannelManager.create(
                new Identifier(NeepMeat.NAMESPACE, "update_params"),
                ChannelFormat.builder(SliderScreenHandler.UpdateParams.class).param(ParamCodec.list(ParamCodec.INT)).build(),
                player);

        receiveParamsC2S = ChannelManager.create(
                new Identifier(NeepMeat.NAMESPACE, "receive_params"),
                ChannelFormat.builder(SliderScreenHandler.UpdateParams.class).param(ParamCodec.list(ParamCodec.INT)).build(),
                player);
        this.ranged = ranged;

        receiveParamsC2S.receiver(this::receiveParams);
    }

    protected void receiveParams(List<Integer> ints)
    {
        updateToClient = true;

        ranged.setMinValue(ints.get(1));
        ranged.setMaxValue(ints.get(2));
    }

    public void close()
    {
        updateParamsS2C.close();
        receiveParamsC2S.close();
    }
}
