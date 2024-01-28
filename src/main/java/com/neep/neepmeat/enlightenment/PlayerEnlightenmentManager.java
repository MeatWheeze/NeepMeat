package com.neep.neepmeat.enlightenment;

import com.neep.neepmeat.api.enlightenment.EnlightenmentManager;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public class PlayerEnlightenmentManager implements EnlightenmentManager, ServerTickingComponent
{
    private final PlayerEntity player;
    double acuteEnlightenment;
    double chronicEnlightenment;

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
        double corrected = base * Math.exp(-Math.sqrt(sqDistance));
        acuteEnlightenment += (1 + corrected) * (1 - split);
        chronicEnlightenment += corrected * split;

//        NeepMeat.LOGGER.info("added: " + corrected + ", acute: " + acuteEnlightenment + " chronic: " + chronicEnlightenment);
    }

    @Override
    public void addChronic(float amount)
    {

    }

    @Override
    public void serverTick()
    {
        acuteEnlightenment = Math.max(0, acuteEnlightenment - 1);
//        chronicEnlightenment += acuteEnlightenment / EnlightenmentUtil.THRESHOLD_NEGLIGIBLE / 50;
        chronicEnlightenment -= 0.001;
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag)
    {
        this.acuteEnlightenment = tag.getDouble("acute");
        this.chronicEnlightenment = tag.getDouble("chronic");
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag)
    {
        tag.putDouble("acute", acuteEnlightenment);
        tag.putDouble("chronic", chronicEnlightenment);
    }

    public static RespawnCopyStrategy<PlayerEnlightenmentManager> RESPAWN_STRATEGY = (from, to, lossless, keepInventory, sameCharacter) ->
    {
        RespawnCopyStrategy.copy(from, to);
        to.acuteEnlightenment = 0;
    };
}
