package com.neep.neepmeat.api.data;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.text.Text;

import java.text.DecimalFormat;

public class DataUtil
{
    public static final DecimalFormat GIEB_FORMAT = new DecimalFormat("###.#");
    public static final DecimalFormat EB_FORMAT = new DecimalFormat("###");

    // One GieB (gibi-esoteric byte)
    public static final long GIEB = 1024;

    public static Text formatData(long data)
    {
        if (data > GIEB)
        {
            return Text.translatable("text." + NeepMeat.NAMESPACE + ".data.gieb_unit", GIEB_FORMAT.format(data / (double) GIEB));
        }
        return Text.translatable("text." + NeepMeat.NAMESPACE + ".data.eb_unit", EB_FORMAT.format(data));
    }
}
