package com.neep.neepmeat.api.enlightenment;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMComponents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

public class EnlightenmentUtil
{
//    protected static final String STAT_NAME = "enlightenment";
//    public static final Identifier ID_ENLIGHTENMENT = new Identifier(NeepMeat.NAMESPACE, STAT_NAME);
//    public static Stat ENLIGHTENMENT;

    public static final float THRESHOLD_EXTREME = 20000;
    public static final float THRESHOLD_HIGH = 10000;
    public static final float THRESHOLD_MEDIUM = 5000;
    public static final float THRESHOLD_LOW = 2500;
    public static final float THRESHOLD_NEGLIGIBLE = 1000;

    public static void init()
    {
//        Registry.register(Registry.CUSTOM_STAT, STAT_NAME, ID_ENLIGHTENMENT);
//        ENLIGHTENMENT = Stats.CUSTOM.getOrCreateStat(ID_ENLIGHTENMENT, StatFormatter.DEFAULT);
    }

    public static float getEventProbability(ServerPlayerEntity player)
    {
        float enlightenment = getEnlightenment(player);

        float p1 = MathHelper.clamp(enlightenment / getMaxEnlightenment(player), 0, 1);

        return p1 / 100;
    }

    public static float getEnlightenment(ServerPlayerEntity player)
    {
        return NMComponents.ENLIGHTENMENT_MANAGER.get(player).getTotal();
//        return player.getStatHandler().getStat(ENLIGHTENMENT);
    }

    public static void applyDose(ServerPlayerEntity player, int baseAmount, double sqDistance, double split)
    {
        EnlightenmentManager manager = NMComponents.ENLIGHTENMENT_MANAGER.get(player);
        manager.exposeDose(baseAmount, sqDistance, split);
    }

    public static int getMaxEnlightenment(ServerPlayerEntity player)
    {
        return (int) (THRESHOLD_EXTREME);
    }
}
