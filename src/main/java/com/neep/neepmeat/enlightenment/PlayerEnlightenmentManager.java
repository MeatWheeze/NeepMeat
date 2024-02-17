package com.neep.neepmeat.enlightenment;

import com.neep.neepmeat.api.enlightenment.EnlightenmentManager;
import com.neep.neepmeat.init.NMComponents;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public class PlayerEnlightenmentManager implements EnlightenmentManager, ClientTickingComponent, ServerTickingComponent, AutoSyncedComponent
{
    private final PlayerEntity player;
    double acuteEnlightenment;
    double chronicEnlightenment;

    double lastDose;
    double dose = 0;

    public PlayerEnlightenmentManager(PlayerEntity player)
    {
        this.player = player;
    }

    @Override
    public int getTotal()
    {
        // TODO: double acute when Pineal Eye is installed
        return (int) (acuteEnlightenment + chronicEnlightenment);
    }

    @Override
    public void exposeDose(float base, double sqDistance, double split)
    {
        // TODO: half chronic with preventatives

        double corrected = expDistance(base, sqDistance);
        acuteEnlightenment += (1 + corrected) * (1 - split);
        chronicEnlightenment += corrected * split;

        dose += corrected;


//        NeepMeat.LOGGER.info("added: " + corrected + ", acute: " + acuteEnlightenment + " chronic: " + chronicEnlightenment);
    }

    public static double expDistance(float base, double sqDistance)
    {
        return base * Math.exp(-Math.sqrt(sqDistance));
    }

    @Override
    public void addChronic(float amount)
    {

    }

    @Override
    public double lastDose()
    {
        return dose;
    }

    @Override
    public void serverTick()
    {
        acuteEnlightenment = Math.max(0, acuteEnlightenment - 1);
//        chronicEnlightenment += acuteEnlightenment / EnlightenmentUtil.THRESHOLD_NEGLIGIBLE / 50;
        chronicEnlightenment = Math.max(0, chronicEnlightenment - 0.001);

//        if (player.world.getTime() % 10 == 0 && lastDose != dose)
        if (lastDose != dose)
            NMComponents.ENLIGHTENMENT_MANAGER.sync(player);

        lastDose = dose;
        dose = 0;
    }

    @Override
    public void clientTick()
    {
//        lastDose = dose;
//        dose = 0;
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag)
    {
        this.acuteEnlightenment = tag.getDouble("acute");
        this.chronicEnlightenment = tag.getDouble("chronic");
        this.dose = tag.getDouble("dose");
        this.lastDose = tag.getDouble("last");
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag)
    {
        tag.putDouble("acute", acuteEnlightenment);
        tag.putDouble("chronic", chronicEnlightenment);
        tag.putDouble("dose", dose);
        tag.putDouble("last", lastDose);
    }

    public static RespawnCopyStrategy<PlayerEnlightenmentManager> RESPAWN_STRATEGY = (from, to, lossless, keepInventory, sameCharacter) ->
    {
        RespawnCopyStrategy.copy(from, to);
        to.acuteEnlightenment = 0;
    };
}
