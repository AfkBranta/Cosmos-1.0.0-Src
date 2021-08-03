package cope.cosmos.client.clickgui.cosmos.component.components;

import cope.cosmos.client.clickgui.cosmos.component.SettingComponent;
import cope.cosmos.client.features.modules.client.ClickGUI;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.util.render.FontUtil;
import cope.cosmos.util.render.RenderUtil;
import cope.cosmos.util.world.SoundUtil;
import java.awt.Color;
import net.minecraft.util.math.Vec2f;
import org.lwjgl.opengl.GL11;

public class EnumComponent extends SettingComponent {

    private int hoverAnimation = 0;

    public EnumComponent(Setting setting, ModuleComponent moduleComponent) {
        super(setting, moduleComponent);
    }

    public void drawSettingComponent(Vec2f position) {
        this.setPosition(position);
        if (this.mouseOver(position.x, position.y, this.WIDTH, 14.0F) && this.hoverAnimation < 25) {
            this.hoverAnimation += 5;
        } else if (!this.mouseOver(position.x, position.y, this.WIDTH, 14.0F) && this.hoverAnimation > 0) {
            this.hoverAnimation -= 5;
        }

        Color settingColor = this.isSubSetting() ? new Color(ClickGUI.INSTANCE.getSecondaryColor().getRed() + this.hoverAnimation, ClickGUI.INSTANCE.getSecondaryColor().getGreen() + this.hoverAnimation, ClickGUI.INSTANCE.getSecondaryColor().getBlue() + this.hoverAnimation) : new Color(ClickGUI.INSTANCE.getComplexionColor().getRed() + this.hoverAnimation, ClickGUI.INSTANCE.getComplexionColor().getGreen() + this.hoverAnimation, ClickGUI.INSTANCE.getComplexionColor().getBlue() + this.hoverAnimation);

        RenderUtil.drawRect(position.x, position.y, this.WIDTH, 14.0F, settingColor);
        RenderUtil.drawRect(position.x, position.y, this.WIDTH, 14.0F, settingColor);
        GL11.glScaled(0.55D, 0.55D, 0.55D);
        float scaledX = (position.x + 4.0F) * 1.8181818F;
        float scaledWidth = (position.x + this.WIDTH - (float) FontUtil.getStringWidth(Setting.formatEnum((Enum) this.getSetting().getValue())) * 0.55F - 3.0F) * 1.8181818F;
        float scaledY = (position.y + 5.0F) * 1.8181818F;

        FontUtil.drawStringWithShadow(this.getSetting().getName(), scaledX, scaledY, -1);
        FontUtil.drawStringWithShadow(Setting.formatEnum((Enum) this.getSetting().getValue()), scaledWidth, scaledY, -1);
        GL11.glScaled(1.81818181D, 1.81818181D, 1.81818181D);
        super.drawSettingComponent(position);
    }

    public void handleLeftClick(int mouseX, int mouseY) {
        if (this.mouseOver(this.getPosition().x, this.getPosition().y, this.WIDTH, 14.0F)) {
            SoundUtil.clickSound();
            Enum nextSettingValue = (Enum) this.getSetting().getNextMode();

            this.getSetting().setValue(nextSettingValue);
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
}
