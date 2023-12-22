package com.neep.neepmeat.api.enlightenment;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EnlightenmentUtil
{

    protected static final String STAT_NAME = "enlightenment";
    public static final Identifier ID_ENLIGHTENMENT = new Identifier(NeepMeat.NAMESPACE, STAT_NAME);
    public static Stat ENLIGHTENMENT;

    private static final float THRESHOLD_EXTREME = 20000;
    private static final float THRESHOLD_HIGH = 10000;
    private static final float THRESHOLD_MEDIUM = 5000;
    private static final float THRESHOLD_LOW = 2500;
    private static final float THRESHOLD_NEGLIGIBLE = 1000;

    public static void init()
    {
        Registry.register(Registry.CUSTOM_STAT, STAT_NAME, ID_ENLIGHTENMENT);
        ENLIGHTENMENT = Stats.CUSTOM.getOrCreateStat(ID_ENLIGHTENMENT, StatFormatter.DEFAULT);
    }

    public static float getEventProbability(ServerPlayerEntity player)
    {
        float enlightenment = getEnlightenment(player);

        // Probability of an event occurring in an hour
        float p1 = enlightenment / getMaxEnlightenment(player);

        return p1 / (60);
    }

    public static float getEnlightenment(ServerPlayerEntity player)
    {
        return player.getStatHandler().getStat(ENLIGHTENMENT);
    }

    public static void setEnlightenment(ServerPlayerEntity player, int value)
    {
        player.getStatHandler().setStat(player, ENLIGHTENMENT, value);
        player.getScoreboard().forEachScore(ENLIGHTENMENT, player.getEntityName(), score -> score.setScore(value));
    }

    public static void  addEnlightenment(ServerPlayerEntity player, int add)
    {
        StatHandler handler = player.getStatHandler();
        int max = Math.min(getMaxEnlightenment(player), handler.getStat(ENLIGHTENMENT) + add);
        handler.setStat(player, ENLIGHTENMENT, max);
        player.getScoreboard().forEachScore(ENLIGHTENMENT, player.getEntityName(), score -> score.setScore(max));
    }

    public static int getMaxEnlightenment(ServerPlayerEntity player)
    {
        return (int) (THRESHOLD_EXTREME);
    }
}
