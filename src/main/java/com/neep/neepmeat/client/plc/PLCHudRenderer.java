package com.neep.neepmeat.client.plc;

import com.neep.meatlib.api.event.InputEvents;
import com.neep.neepmeat.machine.surgical_controller.SurgicalRobot;
import com.neep.neepmeat.mixin.CameraAccessor;
import com.neep.neepmeat.network.plc.PLCRobotEnterS2C;
import com.neep.neepmeat.plc.block.entity.PLCBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

@Environment(value = EnvType.CLIENT)
public class PLCHudRenderer
{
    @Nullable
    public static BlockHitResult HIT_RESULT;
    public static Matrix4f PROJECTION;
    public static Matrix4f MODEL_VIEW;
    @Nullable
    private static PLCHudRenderer INSTANCE;
    private final PLCBlockEntity be;
    private final SurgicalRobot.Client robotClient;
    private final MinecraftClient client;
    private final PLCMotionController controller;
    private final Camera camera;

    private PLCHudRenderer(PLCBlockEntity be)
    {
        this.client = MinecraftClient.getInstance();
        this.be = be;
        this.robotClient = new SurgicalRobot.Client(be.getSurgeryRobot(), be);
        this.be.getSurgeryRobot().setController(client.player);
        this.controller = new PLCMotionController(be.getSurgeryRobot());
        this.camera = client.gameRenderer.getCamera();
    }

    @Nullable
    public static PLCHudRenderer getInstance()
    {
        return INSTANCE;
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
        if (INSTANCE != null)
        {
            INSTANCE.exit();
        }
        INSTANCE = null;
    }

    public static void drawCuboidShapeOutline(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape shape, double offsetX, double offsetY, double offsetZ,
                                              float r, float g, float b, float a)
    {
        MatrixStack.Entry entry = matrices.peek();
        shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) ->
        {
            float k = (float) (maxX - minX);
            float l = (float) (maxY - minY);
            float m = (float) (maxZ - minZ);
            float n = MathHelper.sqrt(k * k + l * l + m * m);
            vertexConsumer.vertex(entry.getPositionMatrix(), (float) (minX + offsetX), (float) (minY + offsetY), (float) (minZ + offsetZ)).color(r, g, b, a).normal(entry.getNormalMatrix(), k /= n, l /= n, m /= n).next();
            vertexConsumer.vertex(entry.getPositionMatrix(), (float) (maxX + offsetX), (float) (maxY + offsetY), (float) (maxZ + offsetZ)).color(r, g, b, a).normal(entry.getNormalMatrix(), k, l, m).next();
        });
    }

    public static void init()
    {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context ->
        {
            PROJECTION = new Matrix4f(context.projectionMatrix());
            MODEL_VIEW = new Matrix4f(context.matrixStack().peek().getPositionMatrix());
        });

        ClientTickEvents.START_CLIENT_TICK.register(client ->
        {
            PLCHudRenderer instance = getInstance();
            if (instance != null)
            {
                instance.clientTick();
            }
        });

        WorldRenderEvents.BLOCK_OUTLINE.register((worldRenderContext, blockOutlineContext) ->
        {
            PLCHudRenderer instance = getInstance();
            if (instance != null)
            {
                instance.onOutlineRender(worldRenderContext, blockOutlineContext);
                return false;
            }
            return true;

        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client1) ->
        {
            leave();
        });

        InputEvents.PRE_INPUT.register((window, key, scancode, action, modifiers) ->
        {
            PLCHudRenderer instance = getInstance();
            if (instance != null)
            {
                InputUtil.Key key2 = InputUtil.fromKeyCode(key, scancode);
                if (action == 0)
                {
                    KeyBinding.setKeyPressed(key2, false);
                }
                else
                {
                    KeyBinding.setKeyPressed(key2, true);
                    KeyBinding.onKeyPressed(key2);
                }
            }
        });
    }

    public void exit()
    {
        PLCRobotEnterS2C.Client.send(be);
        this.be.exit();
    }

    public boolean onRender()
    {
        return true;
    }

    public void onCameraUpdate(float tickDelta)
    {
        controller.update();

        var robot = be.getSurgeryRobot();

        be.getSurgeryRobot().cameraX = MathHelper.lerp(0.1d, be.getSurgeryRobot().cameraX, be.getSurgeryRobot().getX());
        be.getSurgeryRobot().cameraY = MathHelper.lerp(0.1d, be.getSurgeryRobot().cameraY, be.getSurgeryRobot().getY());
        be.getSurgeryRobot().cameraZ = MathHelper.lerp(0.1d, be.getSurgeryRobot().cameraZ, be.getSurgeryRobot().getZ());

        ((CameraAccessor) camera).callSetPos(robot.cameraX, robot.cameraY, robot.cameraZ);

        ((CameraAccessor) camera).callSetRotation(controller.lerpYaw, controller.lerpPitch);

        ((CameraAccessor) camera).setThirdPerson(true);
    }

    private void onOutlineRender(WorldRenderContext wrctx, WorldRenderContext.BlockOutlineContext boctx)
    {
        if (HIT_RESULT == null)
            return;

        BlockPos pos = HIT_RESULT.getBlockPos();
        Vec3d camPos = camera.getPos();
        BlockState targetState = client.world.getBlockState(pos);
        VoxelShape shape = targetState.getOutlineShape(client.world, pos, ShapeContext.of(client.player));

        drawCuboidShapeOutline(
                wrctx.matrixStack(),
                wrctx.consumers().getBuffer(RenderLayer.getLines()),
                shape,
                pos.getX() - camPos.x,
                pos.getY() - camPos.y,
                pos.getZ() - camPos.z,
                1, 0.36f, 0.13f, 0.8f
        );

    }

    private void clientTick()
    {
        if (!client.isPaused())
        {
            robotClient.tick();

//            ((MinecraftClientAccessor) client).callHandleInputEvents();
        }
    }

    public PLCBlockEntity getBlockEntity()
    {
        return be;
    }

    public PLCMotionController getController()
    {
        return controller;
    }
}
