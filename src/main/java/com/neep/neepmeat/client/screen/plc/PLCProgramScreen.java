package com.neep.neepmeat.client.screen.plc;

import com.neep.neepmeat.client.plc.PLCHudRenderer;
import com.neep.neepmeat.client.plc.PLCMotionController;
import com.neep.neepmeat.plc.opcode.InstructionBuilder;
import com.neep.neepmeat.plc.opcode.InstructionProvider;
import com.neep.neepmeat.plc.program.PLCInstruction;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vector4f;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class PLCProgramScreen extends Screen
{
    protected PLCOperationSelector operationSelector;
    @Nullable protected InstructionBuilder instructionBuilder;
    @Nullable private InstructionProvider instructionProvider;

    public PLCProgramScreen()
    {
        super(Text.empty());
        this.passEvents = true;
    }

    @Override
    public boolean shouldPause()
    {
        return false;
    }

    @Override
    protected void init()
    {
        super.init();
        operationSelector = new PLCOperationSelector(this);
        addDrawableChild(operationSelector);
        operationSelector.init(client, width, height);
        operationSelector.setDimensions(width, height);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if (button == GLFW.GLFW_MOUSE_BUTTON_2)
        {
            PLCHudRenderer renderer = PLCHudRenderer.getInstance();
            if (renderer != null)
            {
                double sensitivity = 0.4;
                PLCMotionController controller = renderer.getController();
                controller.setPitchYaw(
                        (float) (controller.getPitch() - deltaY * sensitivity),
                        (float) (controller.getYaw() - deltaX * sensitivity));
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (super.mouseClicked(mouseX, mouseY, button))
        {
            return true;
        }

        return handleWorldClick(mouseX, mouseY, button);
    }

    protected boolean handleWorldClick(double mouseX, double mouseY, int button)
    {
        if (button == GLFW.GLFW_MOUSE_BUTTON_2)
            return false;

        var result = raycastClick(mouseX, mouseY);

        if (result.getType() == HitResult.Type.MISS)
        {
            return false;
        }

        addArgument(result);

        for (int i = 0; i < 15; ++i)
        {
            client.world.addParticle(ParticleTypes.SMOKE, result.getPos().x, result.getPos().y, result.getPos().z, 0, 0, 0);
        }
//
//        client.player.sendMessage(Text.of(String.valueOf(client.world.getBlockState(result.getBlockPos()))));

        return true;
    }

    public void updateInstruction(InstructionProvider provider)
    {
        this.instructionProvider = provider;
        this.instructionBuilder = provider.start(client.world, this::emitInstruction);
    }

    protected void addArgument(BlockHitResult result)
    {
        if (instructionBuilder == null)
            return;

        instructionBuilder.argument(result.getBlockPos(), result.getSide());
    }

    protected void emitInstruction(PLCInstruction instruction)
    {
        client.player.sendMessage(Text.of(instruction.toString()));
    }

    protected BlockHitResult raycastClick(double mouseX, double mouseY)
    {
        Vec3d camPos = client.gameRenderer.getCamera().getPos();
        Vec3d farPos = screenToWorld(mouseX, mouseY, 1.0f);
        Vec3d nearPos = screenToWorld(mouseX, mouseY, 0.0f);

        RaycastContext raycastContext = new RaycastContext(
                new Vec3d(nearPos.getX(), nearPos.getY(), nearPos.getZ()).add(camPos),
                new Vec3d(farPos.getX(), farPos.getY(), farPos.getZ()).add(camPos),
                RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, client.player);
        return client.world.raycast(raycastContext);
    }

    public Vec3d screenToWorld(double mouseX, double mouseY, double z)
    {
        float fx = (float) (mouseX / client.getWindow().getScaledWidth()) * 2 - 1;
        float fy = (float) -((mouseY / client.getWindow().getScaledHeight()) * 2 - 1); // Screen coords are vertically inverted

        var modelView = PLCHudRenderer.MODEL_VIEW.copy();
        var projection = PLCHudRenderer.PROJECTION.copy();
        projection.multiply(modelView);
        projection.invert();

        Vector4f pos = new Vector4f(fx, fy, (float) z, 1.0f);
        pos.transform(projection);
        pos.multiply(1.0f / pos.getW());

        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY)
    {
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public void close()
    {
        super.close();
        PLCHudRenderer.leave();
    }
}
