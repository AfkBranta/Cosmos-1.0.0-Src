package cope.cosmos.client.clickgui.cosmos.component;

import cope.cosmos.client.clickgui.cosmos.component.components.BooleanComponent;
import cope.cosmos.client.clickgui.cosmos.component.components.ColorComponent;
import cope.cosmos.client.clickgui.cosmos.component.components.EnumComponent;
import cope.cosmos.client.clickgui.cosmos.component.components.ModuleComponent;
import cope.cosmos.client.clickgui.cosmos.component.components.NumberComponent;
import cope.cosmos.client.clickgui.cosmos.util.Util;
import cope.cosmos.client.features.modules.client.ClickGUI;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.client.manager.managers.AnimationManager;
import cope.cosmos.util.render.RenderUtil;
import cope.cosmos.util.world.SoundUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

public abstract class SettingComponent implements Util {

    public float WIDTH = 98.0F;
    public final float HEIGHT = 14.0F;
    public final float BAR = 2.0F;
    private Vec2f position;
    private final List settingComponents;
    private final ModuleComponent moduleComponent;
    private SettingComponent settingComponent;
    private final Setting setting;
    private final AnimationManager animationManager;
    private boolean open;
    private float settingOffset;

    public SettingComponent(Setting setting, ModuleComponent moduleComponent) {
        this.position = Vec2f.ZERO;
        this.settingComponents = new ArrayList();
        this.settingOffset = 0.0F;
        this.setting = setting;
        this.moduleComponent = moduleComponent;
        setting.getSubSettings().forEach((subSetting) -> {
            Object subSettingComponent = null;

            if (subSetting.getValue() instanceof Boolean) {
                subSettingComponent = new BooleanComponent(subSetting, moduleComponent);
            } else if (subSetting.getValue() instanceof Enum) {
                subSettingComponent = new EnumComponent(subSetting, moduleComponent);
            } else if (subSetting.getValue() instanceof Color) {
                subSettingComponent = new ColorComponent(subSetting, moduleComponent);
            } else if (subSetting.getValue() instanceof Double) {
                subSettingComponent = new NumberComponent(subSetting, moduleComponent);
            } else if (subSetting.getValue() instanceof Float) {
                subSettingComponent = new NumberComponent(subSetting, moduleComponent);
            }

            if (subSettingComponent != null) {
                ((SettingComponent) subSettingComponent).setSettingComponent(this);
                this.settingComponents.add(subSettingComponent);
            }

        });
        if (this.isSubSetting()) {
            this.WIDTH = 96.0F;
        }

        this.open = false;
        this.animationManager = new AnimationManager(200, false);
    }

    public void drawSettingComponent(Vec2f position) {
        float settingAnimation = (float) MathHelper.clamp(this.animationManager.getAnimationFactor(), 0.0D, 1.0D);

        if (settingAnimation > 0.0F) {
            this.settingOffset = 0.0F;
            this.settingComponents.forEach((settingComponent) -> {
                settingComponent.drawSettingComponent(new Vec2f(position.x + 2.0F, position.y + 14.0F + this.settingOffset * 14.0F));
                if (settingComponent instanceof NumberComponent) {
                    ++this.settingOffset;
                    this.moduleComponent.setSettingOffset(settingAnimation * 1.42857F);
                    this.moduleComponent.getParentWindow().setModuleOffset(settingAnimation * 1.42857F);
                } else {
                    ++this.settingOffset;
                    this.moduleComponent.setSettingOffset(settingAnimation);
                    this.moduleComponent.getParentWindow().setModuleOffset(settingAnimation);
                }

            });
        }

        RenderUtil.drawRect(position.x, position.y + 14.0F, 2.0F, this.settingOffset * 14.0F, new Color(ClickGUI.INSTANCE.getPrimaryColor().getRed(), ClickGUI.INSTANCE.getPrimaryColor().getGreen(), ClickGUI.INSTANCE.getPrimaryColor().getBlue()));
    }

    public void handleLeftClick(int mouseX, int mouseY) {
        if (this.open) {
            this.settingComponents.forEach((settingComponent) -> {
                if (settingComponent.getSetting().isVisible()) {
                    settingComponent.handleLeftClick(mouseX, mouseY);
                }

            });
        }

    }

    public void handleLeftDrag(int mouseX, int mouseY) {
        if (this.open) {
            this.settingComponents.forEach((settingComponent) -> {
                if (settingComponent.getSetting().isVisible()) {
                    settingComponent.handleLeftDrag(mouseX, mouseY);
                }

            });
        }

    }

    public void handleRightClick(int mouseX, int mouseY) {
        if (this.mouseOver(this.getPosition().x, this.getPosition().y, this.WIDTH, 14.0F)) {
            SoundUtil.clickSound();
            this.open = !this.open;
            this.animationManager.setState(this.open);
        }

        if (this.open) {
            this.settingComponents.forEach((settingComponent) -> {
                if (settingComponent.getSetting().isVisible()) {
                    settingComponent.handleRightClick(mouseX, mouseY);
                }

            });
        }

    }

    public void handleKeyPress(char typedCharacter, int key) {
        if (this.open) {
            this.settingComponents.forEach((settingComponent) -> {
                if (settingComponent.getSetting().isVisible()) {
                    settingComponent.handleKeyPress(typedCharacter, key);
                }

            });
        }

    }

    public void handleScroll(int scroll) {
        if (this.open) {
            this.settingComponents.forEach((settingComponent) -> {
                if (settingComponent.getSetting().isVisible()) {
                    settingComponent.handleScroll(scroll);
                }

            });
        }

    }

    public void setPosition(Vec2f in) {
        this.position = in;
    }

    public Vec2f getPosition() {
        return this.position;
    }

    public Setting getSetting() {
        return this.setting;
    }

    public boolean isSubSetting() {
        return this.setting.hasParent();
    }

    public ModuleComponent getModuleComponent() {
        return this.moduleComponent;
    }

    public float getWidth() {
        return this.WIDTH;
    }

    public float getHeight() {
        return 14.0F;
    }

    public void setSettingOffset(float in) {
        this.settingOffset += in;
    }

    public SettingComponent getSettingComponent() {
        return this.settingComponent;
    }

    public void setSettingComponent(SettingComponent in) {
        this.settingComponent = in;
    }
}
