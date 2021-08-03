package cope.cosmos.client.clickgui.cosmos.component.components;

import cope.cosmos.client.clickgui.cosmos.component.Component;
import cope.cosmos.client.clickgui.cosmos.component.SettingComponent;
import cope.cosmos.client.clickgui.cosmos.util.Util;
import cope.cosmos.client.clickgui.cosmos.window.windows.CategoryWindow;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.modules.client.ClickGUI;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.util.Wrapper;
import cope.cosmos.util.render.FontUtil;
import cope.cosmos.util.render.RenderUtil;
import cope.cosmos.util.world.SoundUtil;
import java.awt.Color;
import java.util.function.Consumer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import org.lwjgl.opengl.GL11;

public class ModuleComponent extends Component implements Wrapper, Util {

    private Vec2f position;
    private int hoverAnimation;
    private float settingOffset;
    private float settingAnimation;
    private boolean open;

    public ModuleComponent(Module module, CategoryWindow categoryWindow) {
        super(module, categoryWindow);
        this.position = Vec2f.ZERO;
        this.hoverAnimation = 0;
        this.settingOffset = 0.0F;
        this.settingAnimation = 0.0F;
        if (module != null) {
            module.getSettings().forEach((setting) -> {
                if (setting.getValue() instanceof Boolean) {
                    this.getSettingComponents().add(new BooleanComponent(setting, this));
                } else if (setting.getValue() instanceof Enum) {
                    this.getSettingComponents().add(new EnumComponent(setting, this));
                } else if (setting.getValue() instanceof Color) {
                    this.getSettingComponents().add(new ColorComponent(setting, this));
                } else if (setting.getValue() instanceof Double) {
                    this.getSettingComponents().add(new NumberComponent(setting, this));
                } else if (setting.getValue() instanceof Float) {
                    this.getSettingComponents().add(new NumberComponent(setting, this));
                }

            });
            this.getSettingComponents().add(new DrawnComponent(this));
            this.getSettingComponents().add(new BindComponent(this));
        }

        this.open = false;
    }

    public void drawComponent(Vec2f position) {
        this.setPosition(position);
        this.settingAnimation = this.getModule() != null ? (float) MathHelper.clamp(this.getAnimation().getAnimationFactor(), 0.0D, 1.0D) : 0.0F;
        if (this.getModule() != null) {
            if (this.mouseOver(position.x, position.y, 100.0F, 14.0F) && this.hoverAnimation < 25) {
                this.hoverAnimation += 5;
            } else if (!this.mouseOver(position.x, position.y, 100.0F, 14.0F) && this.hoverAnimation > 0) {
                this.hoverAnimation -= 5;
            }
        }

        RenderUtil.drawRect(position.x, position.y, 100.0F, 14.0F, new Color(ClickGUI.INSTANCE.getBackgroundColor().getRed() + this.hoverAnimation, ClickGUI.INSTANCE.getBackgroundColor().getGreen() + this.hoverAnimation, ClickGUI.INSTANCE.getBackgroundColor().getBlue() + this.hoverAnimation));
        if (this.getModule() != null) {
            GL11.glScaled(0.8D, 0.8D, 0.8D);
            float scaledX = (position.x + 4.0F) * 1.25F;
            float scaledY = (position.y + 4.5F) * 1.25F;
            float scaledWidth = (position.x + 100.0F - (float) FontUtil.getStringWidth("...") * 0.8F - 3.0F) * 1.25F;

            FontUtil.drawStringWithShadow(this.getModule().getName(), scaledX, scaledY, this.getModule().isEnabled() ? (new Color(ClickGUI.INSTANCE.getPrimaryColor().getRed(), ClickGUI.INSTANCE.getPrimaryColor().getGreen(), ClickGUI.INSTANCE.getPrimaryColor().getBlue())).getRGB() : (new Color(255, 255, 255)).getRGB());
            FontUtil.drawStringWithShadow("...", scaledWidth, scaledY, (new Color(255, 255, 255)).getRGB());
            GL11.glScaled(1.25D, 1.25D, 1.25D);
            if (this.settingAnimation > 0.0F) {
                this.settingOffset = 0.0F;
                this.getSettingComponents().forEach((settingComponent) -> {
                    if (settingComponent.getSetting().isVisible() && !settingComponent.getSetting().hasParent()) {
                        float visibleY = position.y + 14.0F + this.settingOffset * settingComponent.getHeight();

                        settingComponent.drawSettingComponent(new Vec2f(position.x + 2.0F, visibleY));
                        if (settingComponent instanceof NumberComponent) {
                            ++this.settingOffset;
                            this.getParentWindow().setModuleOffset(this.settingAnimation * 1.42857F);
                        } else {
                            ++this.settingOffset;
                            this.getParentWindow().setModuleOffset(this.settingAnimation);
                        }
                    }

                });
            }

            RenderUtil.drawRect(position.x, position.y + 14.0F, 2.0F, this.settingOffset * 14.0F, new Color(ClickGUI.INSTANCE.getPrimaryColor().getRed(), ClickGUI.INSTANCE.getPrimaryColor().getGreen(), ClickGUI.INSTANCE.getPrimaryColor().getBlue()));
        }

    }

    public void handleLeftClick(int mouseX, int mouseY) {
        if (this.getModule() != null && this.mouseOver(this.position.x, this.position.y, 100.0F, 14.0F)) {
            SoundUtil.clickSound();
            this.getModule().toggle();
        }

        if (this.open) {
            this.getSettingComponents().forEach((settingComponent) -> {
                if (settingComponent.getSetting().isVisible()) {
                    settingComponent.handleLeftClick(mouseX, mouseY);
                }

            });
        }

    }

    public void handleLeftDrag(int mouseX, int mouseY) {
        if (this.open) {
            this.getSettingComponents().forEach((settingComponent) -> {
                if (settingComponent.getSetting().isVisible()) {
                    settingComponent.handleLeftDrag(mouseX, mouseY);
                }

            });
        }

    }

    public void handleRightClick(int mouseX, int mouseY) {
        if (this.mouseOver(this.position.x, this.position.y, 100.0F, 14.0F)) {
            SoundUtil.clickSound();
            this.open = !this.open;
            if (this.getModule() != null) {
                this.getAnimation().setState(this.open);
            }
        }

        if (this.open) {
            this.getSettingComponents().forEach((settingComponent) -> {
                if (settingComponent.getSetting().isVisible()) {
                    settingComponent.handleRightClick(mouseX, mouseY);
                }

            });
        }

    }

    public void handleKeyPress(char typedCharacter, int key) {
        if (this.open) {
            this.getSettingComponents().forEach((settingComponent) -> {
                if (settingComponent.getSetting().isVisible()) {
                    settingComponent.handleKeyPress(typedCharacter, key);
                }

            });
        }

    }

    public void handleScroll(int scroll) {
        if (this.open) {
            this.getSettingComponents().forEach((settingComponent) -> {
                if (settingComponent.getSetting().isVisible()) {
                    settingComponent.handleScroll(scroll);
                }

            });
        }

    }

    public float getHeight() {
        return 14.0F;
    }

    public void setPosition(Vec2f in) {
        this.position = in;
    }

    public Vec2f getPosition() {
        return this.position;
    }

    public void setSettingOffset(float in) {
        this.settingOffset += in;
    }
}
