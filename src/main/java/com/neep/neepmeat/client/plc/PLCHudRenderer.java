package com.neep.neepmeat.client.plc;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.api.event.KeyboardEvents;
import com.neep.neepmeat.client.screen.plc.PLCProgramScreen;
import com.neep.neepmeat.machine.surgical_controller.SurgicalRobot;
import com.neep.neepmeat.mixin.CameraAccessor;
import com.neep.neepmeat.network.plc.PLCRobotEnterS2C;
import com.neep.neepmeat.plc.block.entity.PLCBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
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
import org.joml.Matrix4dc;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

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

        robotClient.resetKeys();
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

        // Rendere the PLC's orange block outline
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

        // Screen.passEvents was removed before 1.20, so input events must be processed here while in the scree.
        KeyboardEvents.PRE_INPUT.register((window, key, scancode, action, modifiers) ->
        {
            MinecraftClient client = MinecraftClient.getInstance();

            PLCHudRenderer instance = getInstance();
            if (instance != null
                    && client.currentScreen instanceof PLCProgramScreen ps
                    && ps.passEvents())
            {
                // Handle repeats as presses
                instance.handleMovementKey(client, key, action != GLFW.GLFW_RELEASE);
            }
        });
    }

    private void handleMovementKey(MinecraftClient client, int key, boolean pressed)
    {
        InputUtil.Key forward = KeyBindingHelper.getBoundKeyOf(client.options.forwardKey);
        InputUtil.Key back = KeyBindingHelper.getBoundKeyOf(client.options.backKey);
        InputUtil.Key left = KeyBindingHelper.getBoundKeyOf(client.options.leftKey);
        InputUtil.Key right = KeyBindingHelper.getBoundKeyOf(client.options.rightKey);
        InputUtil.Key up = KeyBindingHelper.getBoundKeyOf(client.options.jumpKey);
        InputUtil.Key down = KeyBindingHelper.getBoundKeyOf(client.options.sneakKey);

        if (key == forward.getCode())
            robotClient.forwardKey = pressed;
        else if (key == back.getCode())
            robotClient.backKey = pressed;
        else if (key == left.getCode())
            robotClient.leftKey = pressed;
        else if (key == right.getCode())
            robotClient.rightKey = pressed;
        else if (key == up.getCode())
            robotClient.upKey = pressed;
        else if (key == down.getCode())
            robotClient.downKey = pressed;

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

        double realCameraX = be.getSurgeryRobot().cameraX;
        double realCameraY = be.getSurgeryRobot().cameraY;
        double realCameraZ = be.getSurgeryRobot().cameraZ;

        if (MeatLib.vsUtil != null)
        {
            Vector3d pos = new Vector3d(realCameraX, realCameraY, realCameraZ);
            if (MeatLib.vsUtil.hasShipAtPosition(be.getSurgeryRobot().getBasePos(), client.world))
            {
                Matrix4dc shipToWorld = MeatLib.vsUtil.getShipToWorldMatrix(be.getSurgeryRobot().getBasePos(), client.world);
                if (shipToWorld != null)
                {
                    shipToWorld.transformPosition(pos);
                    realCameraX = pos.x;
                    realCameraY = pos.y;
                    realCameraZ = pos.z;
                }
            }
        }

        ((CameraAccessor) camera).callSetPos(realCameraX, realCameraY, realCameraZ);

        ((CameraAccessor) camera).callSetRotation(controller.lerpYaw, controller.lerpPitch);

        ((CameraAccessor) camera).setThirdPerson(true);
    }

    private void onOutlineRender(WorldRenderContext wrctx, WorldRenderContext.BlockOutlineContext boctx)
    {
        if (HIT_RESULT == null)
            return;

        wrctx.matrixStack().push();

        BlockPos pos = HIT_RESULT.getBlockPos();
        Vec3d camPos = camera.getPos();
        BlockState targetState = client.world.getBlockState(pos);
        VoxelShape shape = targetState.getOutlineShape(client.world, pos, ShapeContext.of(client.player));

        Vector3d realPos = new Vector3d(pos.getX(), pos.getY(), pos.getZ());

        if (MeatLib.vsUtil != null)
        {
            MeatLib.vsUtil.CLIENT.transformRenderIfOnShip(wrctx.matrixStack(), new Vector3d(pos.getX(), pos.getY(), pos.getZ()));
            if (MeatLib.vsUtil.hasShipAtPosition(pos, client.world))
            {
                Matrix4dc matrix = MeatLib.vsUtil.getShipToWorldMatrix(pos, client.world);
                if (matrix != null)
                {
                    matrix.transformPosition(realPos);
                }
            }
        }
        else
        {
            wrctx.matrixStack().translate(realPos.x, realPos.y, realPos.z);
        }

        drawCuboidShapeOutline(
                wrctx.matrixStack(),
                wrctx.consumers().getBuffer(RenderLayer.getLines()),
                shape,
                -camPos.x,
                -camPos.y,
                -camPos.z,
                1, 0.36f, 0.13f, 0.8f
        );

        wrctx.matrixStack().pop();
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
