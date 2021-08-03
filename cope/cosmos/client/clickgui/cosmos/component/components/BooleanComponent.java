package cope.cosmos.client.clickgui.cosmos.component.components;

import cope.cosmos.client.clickgui.cosmos.component.SettingComponent;
import cope.cosmos.client.features.modules.client.ClickGUI;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.client.manager.managers.AnimationManager;
import cope.cosmos.util.render.FontUtil;
import cope.cosmos.util.render.RenderUtil;
import cope.cosmos.util.world.SoundUtil;
import java.awt.Color;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import org.lwjgl.opengl.GL11;

public class BooleanComponent extends SettingComponent {

    private final AnimationManager animationManager;
    private int hoverAnimation = 0;

    public BooleanComponent(Setting setting, ModuleComponent moduleComponent) {
        super(setting, moduleComponent);
        this.animationManager = new AnimationManager(100, ((Boolean) setting.getValue()).booleanValue());
    }

    public void drawSettingComponent(Vec2f position) {
        this.setPosition(position);
        if (this.mouseOver(position.x, position.y, this.WIDTH, 14.0F) && this.hoverAnimation < 25) {
            this.hoverAnimation += 5;
        } else if (!this.mouseOver(position.x, position.y, this.WIDTH, 14.0F) && this.hoverAnimation > 0) {
            this.hoverAnimation -= 5;
        }

        float booleanAnimation = (float) MathHelper.clamp(this.animationManager.getAnimationFactor(), 0.0D, 1.0D);
        Color settingColor = this.isSubSetting() ? new Color(ClickGUI.INSTANCE.getSecondaryColor().getRed() + this.hoverAnimation, ClickGUI.INSTANCE.getSecondaryColor().getGreen() + this.hoverAnimation, ClickGUI.INSTANCE.getSecondaryColor().getBlue() + this.hoverAnimation) : new Color(ClickGUI.INSTANCE.getComplexionColor().getRed() + this.hoverAnimation, ClickGUI.INSTANCE.getComplexionColor().getGreen() + this.hoverAnimation, ClickGUI.INSTANCE.getComplexionColor().getBlue() + this.hoverAnimation);

        RenderUtil.drawRect(position.x, position.y, this.WIDTH, 14.0F, settingColor);
        RenderUtil.drawRect(position.x, position.y, this.WIDTH, 14.0F, settingColor);
        RenderUtil.drawRoundedRect((double) (position.x + this.WIDTH - 12.0F), (double) (position.y + 2.0F), 10.0D, 10.0D, 2.0D, this.isSubSetting() ? new Color(16 + this.hoverAnimation, 16 + this.hoverAnimation, 21 + this.hoverAnimation) : new Color(22 + this.hoverAnimation, 22 + this.hoverAnimation, 28 + this.hoverAnimation));
        if (booleanAnimation > 0.0F) {
            RenderUtil.drawRoundedRect((double) (position.x + this.WIDTH - 7.0F - 4.0F * booleanAnimation), (double) (position.y + 7.0F - 4.0F * booleanAnimation), (double) (8.0F * booleanAnimation), (double) (8.0F * booleanAnimation), 2.0D, new Color(ClickGUI.INSTANCE.getPrimaryColor().getRed(), ClickGUI.INSTANCE.getPrimaryColor().getGreen(), ClickGUI.INSTANCE.getPrimaryColor().getBlue()));
        }

        GL11.glScaled(0.55D, 0.55D, 0.55D);
        float scaledX = (position.x + 4.0F) * 1.8181818F;
        float scaledY = (position.y + 5.0F) * 1.8181818F;

        FontUtil.drawStringWithShadow(this.getSetting().getName(), scaledX, scaledY, -1);
        GL11.glScaled(1.81818181D, 1.81818181D, 1.81818181D);
        super.drawSettingComponent(position);
    }

    public void handleLeftClick(int mouseX, int mouseY) {
        if (this.mouseOver(this.getPosition().x, this.getPosition().y, this.WIDTH, 14.0F)) {
            SoundUtil.clickSound();
            boolean currentValue = ((Boolean) this.getSetting().getValue()).booleanValue();

            this.getSetting().setValue(Boolean.valueOf(!currentValue));
            this.animationManager.setState(!currentValue);
        }

        super.handleLeftClick(mouseX, mouseY);
    }

    public void handleLeftDrag(int mouseX, int mouseY) {
        super.handleLeftDrag(mouseX, mouseY);
    }

    public void handleRightClick(int mouseX, int mouseY) {
        super.handleRightClick(mouseX, mouseY);
    }

    public void handleKeyPress(char typedCharacter, int key) {
        super.handleKeyPress(typedCharacter, key);
    }

    public void handleScroll(int scroll) {
        super.handleScroll(scroll);
    }

    public AnimationManager getAnimation() {
        return this.animationManager;
    }
}
