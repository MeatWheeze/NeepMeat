package com.neep.neepmeat.client.plc;

import com.neep.neepmeat.machine.surgical_controller.SurgicalRobot;
import com.neep.neepmeat.mixin.CameraAccessor;
import com.neep.neepmeat.plc.PLCBlockEntity;
import dev.architectury.event.events.client.ClientPlayerEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value = EnvType.CLIENT)
public class PLCHudRenderer
{
    @Nullable private static PLCHudRenderer INSTANCE;

    @Nullable
    public static PLCHudRenderer getInstance()
    {
        return INSTANCE;
    }

    private final PLCBlockEntity be;
    private final SurgicalRobot.Client robotClient;
    private final MinecraftClient client;

    private PLCHudRenderer(PLCBlockEntity be)
    {
        this.client = MinecraftClient.getInstance();
        this.be = be;
        this.robotClient = new SurgicalRobot.Client(be.getRobot(), be);
        this.be.getRobot().setController(client.player);
    }


    public boolean onRender()
    {
        var camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        return true;
    }


    public static void enter(PLCBlockEntity be)
    {
        INSTANCE = new PLCHudRenderer(be);
    }

    public static boolean active()
    {
        return INSTANCE != null;
    }

    public static void leave()
    {
        INSTANCE = null;
    }

    public void onCameraUpdate(Camera camera, float tickDelta)
    {
        if (be != null)
        {
            var robot = be.getRobot();

            ((CameraAccessor) camera).callSetPos(
//                    robot.clientX,
//                    robot.clientY,
//                    robot.clientZ
                    MathHelper.lerp(tickDelta, robot.prevX, robot.getX()),
                    MathHelper.lerp(tickDelta, robot.prevY, robot.getY()),
                    MathHelper.lerp(tickDelta, robot.prevZ, robot.getZ())
            );
        }
    }

    private void clientTick()
    {
        if (!client.isPaused())
        {
            robotClient.tick();
        }
    }

    public PLCBlockEntity getBlockEntity()
    {
        return be;
    }

    public static void init()
    {
        ClientTickEvents.START_CLIENT_TICK.register(client ->
        {
            PLCHudRenderer instance = getInstance();
            if (instance != null)
            {
                instance.clientTick();
            }
        });

        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player ->
        {
            leave();
        });
    }

}
