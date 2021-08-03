package cope.cosmos.client.clickgui.cosmos.panel;

import cope.cosmos.client.clickgui.cosmos.component.Component;
import cope.cosmos.client.clickgui.cosmos.component.SettingComponent;
import cope.cosmos.client.clickgui.cosmos.util.Util;
import cope.cosmos.util.Wrapper;
import cope.cosmos.util.render.FontUtil;
import cope.cosmos.util.render.RenderUtil;
import java.awt.Color;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.util.math.Vec2f;
import org.lwjgl.opengl.GL11;

public class SettingPanel extends Panel implements Wrapper, Util {

    private Vec2f position;
    private final Component parent;
    private int settingOffset;
    private float settingScroll;
    private final List settingComponents;

    public SettingPanel(Component parent, List settingComponents) {
        this.position = Vec2f.ZERO;
        this.parent = parent;
        this.settingComponents = settingComponents;
    }

    public void drawPanel(Vec2f position) {
        this.setPosition(position);
        GL11.glPushAttrib(524288);
        RenderUtil.scissor((int) position.x, (int) (position.y + 19.0F + 2.0F), (int) (position.x + 150.0F), (int) (position.y + 19.0F + 2.0F + 200.0F));
        GL11.glEnable(3089);
        RenderUtil.drawRoundedRect((double) position.x, (double) position.y, 150.0D, 200.0D, 10.0D, Color.BLACK);
        RenderUtil.drawHalfRoundedRect((double) position.x, (double) position.y, 150.0D, 19.0D, 10.0D, Color.BLACK);
        GL11.glScaled(1.05D, 1.05D, 1.05D);
        float scaledX = (position.x + 7.0F) * 0.95238096F;
        float scaledY = (position.y + 19.0F - 14.0F) * 0.95238096F;

        FontUtil.drawStringWithShadow(this.parent.getModule().getName(), scaledX, scaledY, -1);
        GL11.glScaled(0.95238095D, 0.95238095D, 0.95238095D);
        RenderUtil.drawRect(position.x, position.y + 19.0F, 150.0F, 2.0F, Color.BLACK);
        this.settingOffset = 0;
        this.settingComponents.forEach((settingComponent) -> {
            settingComponent.drawSettingComponent(new Vec2f(position.x, position.y + 2.0F + 19.0F + settingComponent.getHeight() * (float) this.settingOffset + this.settingScroll));
            ++this.settingOffset;
        });
        GL11.glDisable(3089);
        GL11.glPopAttrib();
    }

    public void handleLeftClick(int mouseX, int mouseY) {
        this.settingComponents.forEach((settingComponent) -> {
            settingComponent.handleLeftClick(mouseX, mouseY);
        });
    }

    public void handleRightClick(int mouseX, int mouseY) {
        this.settingComponents.forEach((settingComponent) -> {
            settingComponent.handleRightClick(mouseX, mouseY);
        });
    }

    public void handleKeyPress(char typedCharacter, int key) {
        if (key == 1) {
            this.parent.getAnimation().setStateHard(false);
            if (SettingPanel.mc.entityRenderer.isShaderActive()) {
                SettingPanel.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
            }
        }

        this.settingComponents.forEach((settingComponent) -> {
            settingComponent.handleKeyPress(typedCharacter, key);
        });
    }

    public void handleScroll(int scroll) {}

    public void setPosition(Vec2f in) {
        this.position = in;
    }

    public Vec2f getPosition() {
        return this.position;
    }
}
