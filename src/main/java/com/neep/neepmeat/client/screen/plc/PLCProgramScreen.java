package com.neep.neepmeat.client.screen.plc;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import com.neep.neepmeat.api.plc.recipe.Workpiece;
import com.neep.neepmeat.client.plc.PLCHudRenderer;
import com.neep.neepmeat.client.plc.PLCMotionController;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.network.plc.PLCSyncProgram;
import com.neep.neepmeat.plc.block.entity.PLCBlockEntity;
import com.neep.neepmeat.plc.component.MutateInPlace;
import com.neep.neepmeat.plc.screen.PLCScreenHandler;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.stream.Collectors;

public class PLCProgramScreen extends Screen implements ScreenHandlerProvider<PLCScreenHandler>
{
    protected static final Identifier VIGNETTE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/plc_robot_vignette.png");
    public static final Identifier WIDGETS = new Identifier(NeepMeat.NAMESPACE, "textures/gui/widget/plc_widgets.png");

//    protected final PLCOperationSelector operationSelector = new PLCOperationSelector(this);
    protected final PLCEditor editor;
//    protected final PLCProgramOutline outline;

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
        this.editor = new PLCEditor(this);
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
//        addDrawableChild(operationSelector);
//        operationSelector.init(client, width, height);
//        operationSelector.setDimensions(width, height);

        addDrawableChild(editor);
        editor.init(client, width, height);
        editor.setDimensions(width, height);

        addDrawableChild(new StopButton(width - 17, 2, 16, 16, Text.of("Stop")));
        addDrawableChild(new RunButton(width - 2 * 17, 2, 16, 16, Text.of("Run")));
        addDrawableChild(new CompileButton(width - 3 * 17, 2, 16, 16, Text.of("Compile")));
        addDrawableChild(new ModeSwitchButton(width - 4 * 17, 2, 16, 16));

    }

    @Override
    public void tick()
    {
        super.tick();
        tickTooltip(mouseX, mouseY);
        editor.tick();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (!editor.isTextSelected() || keyCode == GLFW.GLFW_KEY_ESCAPE)
            return super.keyPressed(keyCode, scanCode, modifiers);

        editor.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        BlockState wallState = getWallState(client.world, client.gameRenderer.getCamera());
        if (wallState != null)
        {
            // Fill screen with the block's particle sprite to prevent xray vision
            Sprite sprite = client.getBlockRenderManager().getModels().getModelParticleSprite(wallState);
//            drawScreenTexture(matrices, sprite.getAtlas().getId(), sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), 0.1f);
            renderInWallOverlay(sprite, matrices);
        }
        else
        {
            // Red fleshy vignette
            drawScreenTexture(matrices, VIGNETTE, 0, 0, 1, 1, 0.9f);
        }

        if (!tooltipText.isEmpty())
        {
            renderTooltipText(matrices, tooltipText, true, mouseX, mouseY, 0);
        }

        MatrixStack ms = new MatrixStack();
        ms.multiplyPositionMatrix(PLCHudRenderer.MODEL_VIEW);

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Nullable
    private static BlockState getWallState(World world, Camera camera)
    {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        float width = 0.8f;
        for (int i = 0; i < 8; ++i)
        {
            Vec3d camPos = camera.getPos();
            double d = camPos.x + (((i >> 0) % 2) - 0.5f) * width * 0.8f;
            double e = camPos.y + (((i >> 1) % 2) - 0.5f) * 0.1f;
            double f = camPos.z + (((i >> 2) % 2) - 0.5f) * width * 0.8f;
            mutable.set(d, e, f);

            BlockState blockState = world.getBlockState(mutable);
            if (blockState.getRenderType() == BlockRenderType.INVISIBLE || !blockState.shouldBlockVision(world, mutable)) continue;
            return blockState;
        }
        return null;
    }

    private void drawScreenTexture(MatrixStack matrices, Identifier texture, float u0, float v0, float u1, float v1, float light)
    {
        float x0 = 0; float y0 = 0; float x1 = width; float y1 = height; float z = 0;
        var matrix = matrices.peek().getPositionMatrix();
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, x0, y1, z).color(light, light, light, 1).texture(u0, v1).next();
        bufferBuilder.vertex(matrix, x1, y1, z).color(light, light, light, 1).texture(u1, v1).next();
        bufferBuilder.vertex(matrix, x1, y0, z).color(light, light, light, 1).texture(u1, v0).next();
        bufferBuilder.vertex(matrix, x0, y0, z).color(light, light, light, 1).texture(u0, v0).next();
        BufferRenderer.drawWithShader(bufferBuilder.end());
    }

    private void renderInWallOverlay(Sprite sprite, MatrixStack matrices)
    {
        float x0 = 0; float y0 = 0; float x1 = width; float y1 = height; float z = 0;
        RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        float l = sprite.getMinU();
        float m = sprite.getMaxU();
        float n = sprite.getMinV();
        float o = sprite.getMaxV();
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferBuilder.vertex(matrix4f, x0, y1, z).color(0.1f, 0.1f, 0.1f, 1.0f).texture(m, o).next();
        bufferBuilder.vertex(matrix4f, x1, y1, z).color(0.1f, 0.1f, 0.1f, 1.0f).texture(l, o).next();
        bufferBuilder.vertex(matrix4f, x1, y0, z).color(0.1f, 0.1f, 0.1f, 1.0f).texture(l, n).next();
        bufferBuilder.vertex(matrix4f, x0, y0, z).color(0.1f, 0.1f, 0.1f, 1.0f).texture(m, n).next();
        BufferRenderer.drawWithShader(bufferBuilder.end());
    }

    private void tickTooltip(double mouseX, double mouseY)
    {
        tooltipText.clear();
        var result = raycastClick(mouseX, mouseY, 15);
        if (result.getType() == HitResult.Type.BLOCK)
        {
            PLCHudRenderer.HIT_RESULT = result;

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
        else
        {
            PLCHudRenderer.HIT_RESULT = null;
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

        var result = raycastClick(mouseX, mouseY, 15);

        if (result.getType() == HitResult.Type.MISS)
        {
            return false;
        }

        addArgument(result);

        for (int i = 0; i < 15; ++i)
        {
            client.getSoundManager().play(PositionedSoundInstance.master(NMSounds.PLC_SELECT, 1.0f));
        }

        return true;
    }

    public void updateInstruction(InstructionProvider provider)
    {
        PLCSyncProgram.Client.switchOperation(provider, plc);
    }

    protected void addArgument(BlockHitResult result)
    {
        editor.argument(new Argument(result.getBlockPos(), result.getSide()));
    }

    protected BlockHitResult raycastClick(double mouseX, double mouseY, double range)
    {
        Vec3d camPos = client.gameRenderer.getCamera().getPos();
        Vec3d farPos = screenToWorld(mouseX, mouseY, 0.4f);
        Vec3d nearPos = screenToWorld(mouseX, mouseY, 0.0f);

        Vec3d newFar = nearPos.add(farPos.subtract(nearPos).normalize().multiply(range));

        RaycastContext raycastContext = new RaycastContext(
                nearPos.add(camPos),
                newFar.add(camPos),
                RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, client.player);
        var result =  client.world.raycast(raycastContext);

        return result;
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
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void removed()
    {
        super.removed();
        if (client.player != null)
            handler.close(client.player);

        PLCHudRenderer.leave();
    }

    @Override
    public void close()
    {
        super.close();
        this.client.player.closeHandledScreen();
        PLCHudRenderer.leave();
    }

    public void renderTooltipText(MatrixStack matrices, List<Text> texts, boolean offset, int x, int y, int col)
    {
        renderTooltipComponents(matrices, texts.stream().map(t -> TooltipComponent.of(t.asOrderedText())).collect(Collectors.toList()), offset, x, y, 0, col);
    }

    public void renderTooltipOrderedText(MatrixStack matrices, List<OrderedText> texts, boolean offset, int x, int y, int width, int col)
    {
        renderTooltipComponents(matrices, texts.stream().map(TooltipComponent::of).collect(Collectors.toList()), offset, x, y, width, col);
    }

    private void renderTooltipComponents(MatrixStack matrices, List<TooltipComponent> components, boolean offset, int x, int y, int maxWidth, int col)
    {
        if (offset)
        {
            x += 12;
            y -= 12;
        }
        if (components.isEmpty())
        {
            return;
        }

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

        for (TooltipComponent tooltipComponent2 : components)
        {
            tooltipComponent2.drawText(this.textRenderer, x + 2, yAdvance, matrix4f, immediate);
            yAdvance += tooltipComponent2.getHeight();
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

        @Override
        public void playDownSound(SoundManager soundManager)
        {
            soundManager.play(PositionedSoundInstance.master(NMSounds.PLC_SELECT, 1.0F));
            soundManager.play(PositionedSoundInstance.master(NMSounds.UI_BEEP, 1.0F));
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
            renderTooltipText(matrices, List.of(getMessage()), true, mouseX, mouseY, PLCCols.BORDER.col);
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

    class CompileButton extends SaveButton
    {
        public CompileButton(int x, int y, int width, int height, Text message)
        {
            super(x, y, width, height, message);
        }

        @Override
        protected int getU()
        {
            return 96;
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            PLCSyncProgram.Client.sendCompile(plc);
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
        protected int getYImage(boolean hovered)
        {
            if (handler.hasProgram() > 0)
            {
                if (isHovered())
                    return 2;
                else
                    return 1;
            }
            return 0;
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            PLCSyncProgram.Client.sendStop(plc);
        }
    }

    class ModeSwitchButton extends SaveButton
    {
        public ModeSwitchButton(int x, int y, int width, int height)
        {
            super(x, y, width, height, Text.empty());
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
                case RECORD -> Text.of("Record Mode: Instructions will be added to the current program");
                case IMMEDIATE -> Text.of("Shell Mode: Instructions will be executed immediately");
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

}
