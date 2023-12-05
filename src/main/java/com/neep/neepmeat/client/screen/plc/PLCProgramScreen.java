package com.neep.neepmeat.client.screen.plc;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.recipe.Workpiece;
import com.neep.neepmeat.client.plc.PLCHudRenderer;
import com.neep.neepmeat.client.plc.PLCMotionController;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.network.plc.PLCSyncProgram;
import com.neep.neepmeat.plc.PLCBlockEntity;
import com.neep.neepmeat.api.plc.instruction.Argument;
import com.neep.neepmeat.api.plc.instruction.InstructionProvider;
import com.neep.neepmeat.plc.component.MutateInPlace;
import com.neep.neepmeat.plc.recipe.ItemWorkpiece;
import com.neep.neepmeat.plc.screen.PLCScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vector4f;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib3.core.util.Color;

import java.util.List;
import java.util.stream.Collectors;

public class PLCProgramScreen extends Screen implements ScreenHandlerProvider<PLCScreenHandler>
{
    protected static final Identifier VIGNETTE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/plc_robot_vignette.png");
    public static final Identifier WIDGETS = new Identifier(NeepMeat.NAMESPACE, "textures/gui/widget/plc_widgets.png");

    protected final PLCOperationSelector operationSelector = new PLCOperationSelector(this);
    protected final PLCProgramOutline outline;

    private final PLCScreenHandler handler;
    private final PLCBlockEntity plc;

    // Text relating to the block that the mouse is currently over
    private final List<Text> tooltipText = Lists.newArrayList();
    private double mouseX;
    private double mouseY;

    public PLCProgramScreen(PLCScreenHandler handler, PlayerInventory playerInventory, Text unused)
    {
        super(unused);
        this.handler = handler;
        this.passEvents = true;
        this.plc = handler.getPlc();
        this.outline = new PLCProgramOutline(plc.getEditor(), this);
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
        addDrawableChild(operationSelector);
        operationSelector.init(client, width, height);
        operationSelector.setDimensions(width, height);

        addDrawableChild(outline);
        outline.init(client, width, height);
        outline.setDimensions(width, height);

        addDrawableChild(new SaveButton(width - 17, 2, 16, 16, Text.of("Save")));
        addDrawableChild(new StopButton(width - 2 * 17, 2, 16, 16, Text.of("Stop")));
        addDrawableChild(new RunButton(width - 3 * 17, 2, 16, 16, Text.of("Run")));
        addDrawableChild(new ImmediateRecordButton(width - 4 * 17, 2, 16, 16, Text.of("")));
    }

    @Override
    public void tick()
    {
        super.tick();
        tickTooltip(mouseX, mouseY);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        float x0 = 0;
        float y0 = 0;
        float x1 = width;
        float y1 = height;
        float z = 0;
        float u0 = 0;
        float v0 = 0;
        float u1 = 1;
        float v1 = 1;
        var matrix = matrices.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, VIGNETTE);
        RenderSystem.enableBlend();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, x0, y1, z).texture(u0, v1).next();
        bufferBuilder.vertex(matrix, x1, y1, z).texture(u1, v1).next();
        bufferBuilder.vertex(matrix, x1, y0, z).texture(u1, v0).next();
        bufferBuilder.vertex(matrix, x0, y0, z).texture(u0, v0).next();
        BufferRenderer.drawWithShader(bufferBuilder.end());

        if (!tooltipText.isEmpty())
        {
            renderTooltipText(matrices, tooltipText, mouseX, mouseY, 0);
        }

        super.render(matrices, mouseX, mouseY, delta);
    }


    private void tickTooltip(double mouseX, double mouseY)
    {
        tooltipText.clear();
        var result = raycastClick(mouseX, mouseY);
        if (result.getType() == HitResult.Type.BLOCK)
        {
            var mip1 = MutateInPlace.ITEM.find(client.world, result.getBlockPos(), null);
            var mip2 = MutateInPlace.ENTITY.find(client.world, result.getBlockPos(), null);
            if (mip1 != null)
            {
                var stack = mip1.get();
                if (stack == null || stack.isEmpty())
                    return;

                tooltipText.addAll(mip1.get().getTooltip(client.player, TooltipContext.Default.NORMAL));
            }
            else if (mip2 != null)
            {
                makeEntityTooltip(mip2.get(), tooltipText);
            }
        }
    }

    private static void makeEntityTooltip(@Nullable Entity entity, List<Text> tooltip)
    {
        if (entity == null)
            return;

        tooltip.add(entity.getDisplayName());
        Workpiece workpiece = NMComponents.WORKPIECE.getNullable(entity);
        if (workpiece != null)
        {
            for (var step : workpiece.getSteps())
            {
                step.appendText(tooltip);
            }
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if (button == GLFW.GLFW_MOUSE_BUTTON_2 || button == GLFW.GLFW_MOUSE_BUTTON_3)
        {
            PLCHudRenderer renderer = PLCHudRenderer.getInstance();
            if (renderer != null)
            {
                double sensitivity = 0.4;
                PLCMotionController controller = renderer.getController();
                controller.setPitchYaw(
                        (float) MathHelper.clamp(controller.getPitch() - (deltaY * sensitivity), -90, 90),
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
        if (button == GLFW.GLFW_MOUSE_BUTTON_2 || button == GLFW.GLFW_MOUSE_BUTTON_3)
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
            client.getSoundManager().play(PositionedSoundInstance.master(NMSounds.PLC_SELECT, 1.0f));
        }
//
//        client.player.sendMessage(Text.of(String.valueOf(client.world.getBlockState(result.getBlockPos()))));

        return true;
    }

    public void updateInstruction(InstructionProvider provider)
    {
//        if (mode == RecordMode.IMMEDIATE)
//        {
//            PLCSyncProgram.Client.switchOperationImmediate((ImmediateInstructionProvider) provider, plc);
//        }
//        else
//        {
//        }
        PLCSyncProgram.Client.switchOperation(provider, plc);
    }

    protected void addArgument(BlockHitResult result)
    {
        PLCSyncProgram.Client.sendArgument(new Argument(result.getBlockPos(), result.getSide()), plc);
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
    public void removed()
    {
        super.removed();
        PLCHudRenderer.leave();
    }

    @Override
    public void close()
    {
        super.close();
        this.client.player.closeHandledScreen();
        PLCHudRenderer.leave();
    }

    private void renderTooltipText(MatrixStack matrices, List<Text> texts, int x, int y, int col)
    {
        renderTooltipComponents(matrices, texts.stream().map(t -> TooltipComponent.of(t.asOrderedText())).collect(Collectors.toList()), x, y, col);
    }

    private void renderTooltipComponents(MatrixStack matrices, List<TooltipComponent> components, int x, int y, int col)
    {
        x += 12;
        y -= 12;
        if (components.isEmpty())
        {
            return;
        }

        int maxWidth = 0;
        int maxHeight = components.size() == 1 ? -2 : 0;
        for (TooltipComponent tooltipComponent : components)
        {
            int componentWidth = tooltipComponent.getWidth(this.textRenderer);
            if (componentWidth > maxWidth)
            {
                maxWidth = componentWidth;
            }
            maxHeight += tooltipComponent.getHeight();
        }

        if (x + maxWidth > this.width)
        {
            x -= 28 + maxWidth;
        }

        if (y + maxHeight + 6 > this.height)
        {
            y = this.height - maxHeight - 6;
        }

        matrices.push();
        float itemRendererPrevZ = this.itemRenderer.zOffset;
        float prevZ = this.getZOffset();
        this.itemRenderer.zOffset = 400.0f;
        this.setZOffset(400);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

//        int borderCol = Color.ofRGBA(255, 94, 33, 255).getColor();
        Screen.fill(matrices, x, y, x + maxWidth + 2, y + maxHeight + 2, 0x90000000);
        drawHorizontalLine(matrices, x, x + maxWidth + 2, y, col);
        drawHorizontalLine(matrices, x, x + maxWidth + 2, y + maxHeight + 2, col);
        drawVerticalLine(matrices, x + maxWidth + 2, y, y + maxHeight + 2, col);
        drawVerticalLine(matrices, x, y, y + maxHeight + 2, col);

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        matrices.translate(0.0, 0.0, 400.0);

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        int yAdvance = y + 2;
        for (int index = 0; index < components.size(); ++index)
        {
            TooltipComponent tooltipComponent2 = components.get(index);
            tooltipComponent2.drawText(this.textRenderer, x + 2, yAdvance, matrix4f, immediate);
            yAdvance += tooltipComponent2.getHeight() + (index == 0 ? 2 : 0);
        }

        immediate.draw();
        matrices.pop();
        yAdvance = y;
        for (int index = 0; index < components.size(); ++index)
        {
            TooltipComponent tooltipComponent2 = components.get(index);
            tooltipComponent2.drawItems(this.textRenderer, x, yAdvance, matrices, this.itemRenderer, 400);
            yAdvance += tooltipComponent2.getHeight() + (index == 0 ? 2 : 0);
        }
        this.itemRenderer.zOffset = itemRendererPrevZ;
        this.setZOffset((int) prevZ);
    }

    @Override
    public PLCScreenHandler getScreenHandler()
    {
        return handler;
    }

    class SaveButton extends ClickableWidget
    {
        public SaveButton(int x, int y, int width, int height, Text message)
        {
            super(x, y, width, height, message);
        }

        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
        {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, WIDGETS);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
            int i = this.getYImage(this.isHovered());
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            int thingHeight = 16;
            drawTexture(matrices, this.x, this.y, 0, getU(), getV() + i * thingHeight, this.width, this.height, 256, 256);
            this.renderBackground(matrices, minecraftClient, mouseX, mouseY);

            if (isMouseOver(mouseX, mouseY))
            {
                renderTooltip(matrices, mouseX, mouseY);
            }
        }


        protected int getU()
        {
            return 0;
        }

        protected int getV()
        {
            return 0;
        }

        @Override
        public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY)
        {
            renderTooltipText(matrices, List.of(getMessage()), mouseX, mouseY, borderCol());
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder)
        {

        }
    }

    class RunButton extends SaveButton
    {
        public RunButton(int x, int y, int width, int height, Text message)
        {
            super(x, y, width, height, message);
        }

        @Override
        protected int getU()
        {
            return handler.isRunning() ? 32 : 16;
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            if (handler.isRunning())
                PLCSyncProgram.Client.sendPause(plc);
            else
                PLCSyncProgram.Client.sendRun(plc);
        }
    }

    class StopButton extends SaveButton
    {
        public StopButton(int x, int y, int width, int height, Text message)
        {
            super(x, y, width, height, message);
        }

        @Override
        protected int getU()
        {
            return 80;
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            PLCSyncProgram.Client.sendStop(plc);
        }
    }

    class ImmediateRecordButton extends SaveButton
    {
        public ImmediateRecordButton(int x, int y, int width, int height, Text message)
        {
            super(x, y, width, height, message);
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            RecordMode newMode = RecordMode.cycle(handler.getMode());
            PLCSyncProgram.Client.sendMode(plc, newMode);
        }

        @Override
        public Text getMessage()
        {
            return switch (handler.getMode())
            {
                case RECORD -> Text.of("Mode: Record");
                case IMMEDIATE -> Text.of("Mode: Immediate");
            };
        }

        @Override
        protected int getU()
        {
            return switch(handler.getMode())
            {
                case RECORD -> 64;
                case IMMEDIATE -> 48;
            };
        }
    }

    public static int borderCol()
    {
        return Color.ofRGBA(255, 94, 33, 255).getColor();
    }

    public static int selectedCol()
    {
        return Color.ofRGBA(255, 150, 33, 255).getColor();
    }
}
