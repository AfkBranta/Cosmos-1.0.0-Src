package cope.cosmos.client.clickgui.cosmos.window.windows;

import cope.cosmos.client.clickgui.cosmos.component.components.ModuleComponent;
import cope.cosmos.client.clickgui.cosmos.util.Util;
import cope.cosmos.client.clickgui.cosmos.window.Window;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.modules.client.ClickGUI;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.client.manager.managers.AnimationManager;
import cope.cosmos.client.manager.managers.ModuleManager;
import cope.cosmos.util.Wrapper;
import cope.cosmos.util.render.FontUtil;
import cope.cosmos.util.render.RenderUtil;
import cope.cosmos.util.system.Timer;
import cope.cosmos.util.world.SoundUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class CategoryWindow extends Window implements Wrapper, Util {

    private final float WIDTH = 100.0F;
    private final float TITLE = 19.0F;
    private final float BAR = 2.0F;
    private float SCISSOR_HEIGHT;
    private float HEIGHT;
    private final Category category;
    private Vec2f position;
    private Vec2f previousMousePosition;
    private final List moduleComponents;
    private float moduleComponentOffset;
    private float moduleOffset;
    private float scrollSpeed;
    private float moduleScroll;
    private final Timer scrollTimer;
    private final AnimationManager animationManager;
    private boolean open;

    public CategoryWindow(Vec2f position, Category category) {
        this.previousMousePosition = Vec2f.ZERO;
        this.moduleComponents = new ArrayList();
        this.scrollSpeed = 0.0F;
        this.scrollTimer = new Timer();
        this.position = position;
        this.category = category;
        ModuleManager.getModules((module) -> {
            return module.getCategory().equals(category);
        }).forEach((module) -> {
            this.moduleComponents.add(new ModuleComponent(module, this));
        });
        this.HEIGHT = 19.0F + (float) (this.moduleComponents.size() * 14) + 3.0F;
        this.SCISSOR_HEIGHT = this.HEIGHT - 7.0F;

        for (int i = 0; i <= 100; ++i) {
            this.moduleComponents.add(new ModuleComponent((Module) null, this));
        }

        this.open = false;
        this.animationManager = new AnimationManager(200, false);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void drawWindow() {
        if (this.mouseOver(this.position.x, this.position.y, 100.0F, 19.0F) && this.getGUI().getMouse().isLeftHeld()) {
            this.setDragging(true);
        }

        if (this.mouseOver(this.position.x, this.position.y + 19.0F + this.HEIGHT, 100.0F, 10.0F) && this.getGUI().getMouse().isLeftHeld()) {
            this.setExpanding(true);
        }

        if (this.isDragging()) {
            this.setPosition(new Vec2f(this.position.x + (this.getGUI().getMouse().getMousePosition().x - this.previousMousePosition.x), this.position.y + (this.getGUI().getMouse().getMousePosition().y - this.previousMousePosition.y)));
        }

        if (this.isExpanding()) {
            this.HEIGHT = this.getGUI().getMouse().getMousePosition().y - this.position.y;
        }

        this.SCISSOR_HEIGHT = this.HEIGHT - 7.0F;
        GL11.glPushMatrix();
        float categoryAnimation = (float) MathHelper.clamp(this.animationManager.getAnimationFactor(), 0.0D, 1.0D);

        RenderUtil.drawRoundedRect((double) this.position.x, (double) this.position.y, 100.0D, (double) (19.0F + this.HEIGHT * categoryAnimation), 10.0D, new Color(ClickGUI.INSTANCE.getBackgroundColor().getRed(), ClickGUI.INSTANCE.getBackgroundColor().getGreen(), ClickGUI.INSTANCE.getBackgroundColor().getBlue()));
        RenderUtil.drawHalfRoundedRect((double) this.position.x, (double) this.position.y, 100.0D, 19.0D, 10.0D, new Color(ClickGUI.INSTANCE.getAccentColor().getRed(), ClickGUI.INSTANCE.getAccentColor().getGreen(), ClickGUI.INSTANCE.getAccentColor().getBlue()));
        GL11.glScaled(1.05D, 1.05D, 1.05D);
        float scaledX = (this.position.x + (this.position.x + 100.0F - this.position.x) / 2.0F - (float) FontUtil.getStringWidth(Setting.formatEnum(this.category)) / 2.0F - 2.0F) * 0.95238096F;
        float scaledY = (this.position.y + 19.0F - 14.0F) * 0.95238096F;

        FontUtil.drawStringWithShadow(Setting.formatEnum(this.category), scaledX, scaledY, (new Color(255, 255, 255)).getRGB());
        GL11.glScaled(0.95238095D, 0.95238095D, 0.95238095D);
        RenderUtil.drawRect(this.position.x, this.position.y + 19.0F, 100.0F, 2.0F, new Color(ClickGUI.INSTANCE.getPrimaryColor().getRed(), ClickGUI.INSTANCE.getPrimaryColor().getGreen(), ClickGUI.INSTANCE.getPrimaryColor().getBlue()));
        if (categoryAnimation > 0.0F) {
            GL11.glPushAttrib(524288);
            RenderUtil.scissor((int) this.position.x, (int) (this.position.y + 19.0F + 2.0F), (int) (this.position.x + 100.0F), (int) (this.position.y + 19.0F + 2.0F + this.SCISSOR_HEIGHT * categoryAnimation));
            GL11.glEnable(3089);
            this.moduleOffset = 0.0F;
            this.moduleComponentOffset = 0.0F;
            this.moduleComponents.forEach((moduleComponent) -> {
                float visibleY = this.position.y + 2.0F + 19.0F + moduleComponent.getHeight() * this.moduleOffset - this.moduleScroll;

                moduleComponent.drawComponent(new Vec2f(this.position.x, visibleY));
                if (moduleComponent.getModule() != null) {
                    ++this.moduleComponentOffset;
                }

                ++this.moduleOffset;
            });
            GL11.glDisable(3089);
            GL11.glPopAttrib();
        }

        GL11.glPopMatrix();
        this.previousMousePosition = new Vec2f(this.getGUI().getMouse().getMousePosition().x, this.getGUI().getMouse().getMousePosition().y);
    }

    public void handleLeftClick(int mouseX, int mouseY) {
        if (this.open) {
            this.moduleComponents.forEach((moduleComponent) -> {
                if (moduleComponent.getModule() != null) {
                    moduleComponent.handleLeftClick(mouseX, mouseY);
                }

            });
        }

    }

    public void handleLeftDrag(int mouseX, int mouseY) {
        if (this.open) {
            this.moduleComponents.forEach((moduleComponent) -> {
                if (moduleComponent.getModule() != null) {
                    moduleComponent.handleLeftDrag(mouseX, mouseY);
                }

            });
        }

    }

    public void handleRightClick(int mouseX, int mouseY) {
        if (this.mouseOver(this.position.x, this.position.y, 100.0F, 19.0F)) {
            SoundUtil.clickSound();
            this.open = !this.open;
            this.animationManager.setState(this.open);
        }

        if (this.open) {
            this.moduleComponents.forEach((moduleComponent) -> {
                if (moduleComponent.getModule() != null) {
                    moduleComponent.handleRightClick(mouseX, mouseY);
                }

            });
        }

    }

    public void handleKeyPress(char typedCharacter, int key) {
        if (this.open) {
            this.moduleComponents.forEach((moduleComponent) -> {
                if (moduleComponent.getModule() != null) {
                    moduleComponent.handleKeyPress(typedCharacter, key);
                }

            });
        }

    }

    @SubscribeEvent
    public void onTick(ClientTickEvent tickEvent) {
        float upperBound = Math.max(this.position.y + 19.0F + 2.0F + (this.moduleComponentOffset - 2.0F) * 14.0F - this.HEIGHT, 0.01F);

        this.moduleScroll += this.scrollSpeed;
        this.scrollSpeed = (float) ((double) this.scrollSpeed * 0.5D);
        if (this.scrollTimer.passed(100L, Timer.Format.SYSTEM)) {
            if (this.moduleScroll < 0.0F) {
                this.scrollSpeed = this.moduleScroll * -0.25F;
            } else if (this.moduleScroll > upperBound) {
                this.scrollSpeed = (this.moduleScroll - upperBound) * -0.25F;
            }
        }

    }

    public void handleScroll(int scroll) {
        if (this.open) {
            if (Mouse.getEventDWheel() != 0) {
                this.scrollTimer.reset();
                this.scrollSpeed -= (float) scroll * 0.05F;
            }

            this.moduleComponents.forEach((moduleComponent) -> {
                if (moduleComponent.getModule() != null) {
                    moduleComponent.handleScroll(scroll);
                }

            });
        }

    }

    public List getModuleComponents() {
        return this.moduleComponents;
    }

    public float getHeight() {
        return this.HEIGHT + 19.0F + 2.0F;
    }

    public float getUpperHeight() {
        return this.position.y + 19.0F + 2.0F;
    }

    public float getLowerHeight() {
        return this.position.y + 19.0F + 2.0F + this.SCISSOR_HEIGHT;
    }

    public float getCategoryAnimation() {
        return (float) MathHelper.clamp(this.animationManager.getAnimationFactor(), 0.0D, 1.0D);
    }

    public float getWidth() {
        return 100.0F;
    }

    public float getTitle() {
        return 19.0F;
    }

    public void setModuleOffset(float in) {
        this.moduleOffset += in;
        this.moduleComponentOffset += in;
    }

    public float getModuleScroll() {
        return this.moduleScroll;
    }

    public void setModuleScroll(int in) {
        this.moduleScroll = (float) in;
    }

    public Vec2f getPosition() {
        return this.position;
    }

    public void setPosition(Vec2f in) {
        this.position = in;
    }

    public Category getCategory() {
        return this.category;
    }

    public AnimationManager getAnimation() {
        return this.animationManager;
    }

    public void setOpen(boolean in) {
        this.open = in;
    }

    public void setHeight(float in) {
        this.HEIGHT = in;
    }
}
