package com.neep.neepmeat.client.screen.plc;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.client.plc.PLCHudRenderer;
import com.neep.neepmeat.client.plc.PLCMotionController;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.Camera;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vector4f;
import net.minecraft.world.RaycastContext;
import org.lwjgl.glfw.GLFW;

public class PLCProgramScreen extends Screen
{

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
        if (button == GLFW.GLFW_MOUSE_BUTTON_2)
            return false;

        Vec3d camPos = client.gameRenderer.getCamera().getPos();
        Vec3d farPos = screenToWorld(mouseX, mouseY, 1.0f);
        Vec3d nearPos = screenToWorld(mouseX, mouseY, 0.0f);

        RaycastContext raycastContext = new RaycastContext(
                new Vec3d(nearPos.getX(), nearPos.getY(), nearPos.getZ()).add(camPos),
                new Vec3d(farPos.getX(), farPos.getY(), farPos.getZ()).add(camPos),
                RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, client.player);
        BlockHitResult result = client.world.raycast(raycastContext);

        for (int i = 0; i < 15; ++i)
        {
            client.world.addParticle(ParticleTypes.SMOKE, result.getPos().x, result.getPos().y, result.getPos().z, 0, 0, 0);
        }

        client.player.sendMessage(Text.of(String.valueOf(client.world.getBlockState(result.getBlockPos()))));

        return super.mouseClicked(mouseX, mouseY, button);
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
