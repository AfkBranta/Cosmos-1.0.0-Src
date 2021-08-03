package cope.cosmos.client.clickgui.cosmos.component.components;

import cope.cosmos.client.clickgui.cosmos.component.SettingComponent;
import cope.cosmos.client.features.modules.client.ClickGUI;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.util.render.FontUtil;
import cope.cosmos.util.render.RenderUtil;
import cope.cosmos.util.system.MathUtil;
import cope.cosmos.util.system.Timer;
import cope.cosmos.util.world.SoundUtil;
import java.awt.Color;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.Vec2f;
import org.lwjgl.opengl.GL11;

public class NumberComponent extends SettingComponent {

    private final float HEIGHT = 20.0F;
    private final float SLIDER_HEIGHT = 6.0F;
    private int hoverAnimation = 0;
    private String typedValue;
    private final Timer insertionTimer = new Timer();
    private boolean insertion = false;
    private boolean typing = false;
    private float pixAdd;
    private float lastDraggingMouseX = 0.0F;

    public NumberComponent(Setting setting, ModuleComponent moduleComponent) {
        super(setting, moduleComponent);
        this.typedValue = String.valueOf(setting.getValue());
    }

    public void drawSettingComponent(Vec2f position) {
        this.setPosition(position);
        this.typedValue = this.getSetting().getValue() + this.getInsertionPoint();
        if (this.mouseOver(position.x, position.y, this.WIDTH, 20.0F) && this.hoverAnimation < 25) {
            this.hoverAnimation += 5;
        } else if (!this.mouseOver(position.x, position.y, this.WIDTH, 20.0F) && this.hoverAnimation > 0) {
            this.hoverAnimation -= 5;
        }

        Color settingColor = this.isSubSetting() ? new Color(ClickGUI.INSTANCE.getSecondaryColor().getRed() + this.hoverAnimation, ClickGUI.INSTANCE.getSecondaryColor().getGreen() + this.hoverAnimation, ClickGUI.INSTANCE.getSecondaryColor().getBlue() + this.hoverAnimation) : new Color(ClickGUI.INSTANCE.getComplexionColor().getRed() + this.hoverAnimation, ClickGUI.INSTANCE.getComplexionColor().getGreen() + this.hoverAnimation, ClickGUI.INSTANCE.getComplexionColor().getBlue() + this.hoverAnimation);

        RenderUtil.drawRect(position.x, position.y, this.WIDTH, 20.0F, settingColor);
        RenderUtil.drawRect(position.x, position.y, this.WIDTH, 20.0F, settingColor);
        GL11.glScaled(0.55D, 0.55D, 0.55D);
        float scaledX = (position.x + 4.0F) * 1.8181818F;
        float scaledWidth = (position.x + this.WIDTH - (float) FontUtil.getStringWidth(this.typedValue) * 0.55F - 3.0F) * 1.8181818F;
        float scaledY = (position.y + 5.0F) * 1.8181818F;

        FontUtil.drawStringWithShadow(this.getSetting().getName(), scaledX, scaledY, -1);
        FontUtil.drawStringWithShadow(this.typedValue, scaledWidth, scaledY, -1);
        GL11.glScaled(1.81818181D, 1.81818181D, 1.81818181D);
        RenderUtil.drawRoundedRect((double) (position.x + 4.0F), (double) (position.y + 14.0F), (double) (this.pixAdd - 2.0F), 3.0D, 2.0D, new Color(ClickGUI.INSTANCE.getPrimaryColor().getRed(), ClickGUI.INSTANCE.getPrimaryColor().getGreen(), ClickGUI.INSTANCE.getPrimaryColor().getBlue()));
        RenderUtil.drawPolygon((double) (position.x + 2.0F + this.pixAdd), (double) position.y + 15.5D, 2.0F, 360, new Color(ClickGUI.INSTANCE.getPrimaryColor().getRed(), ClickGUI.INSTANCE.getPrimaryColor().getGreen(), ClickGUI.INSTANCE.getPrimaryColor().getBlue()));
    }

    public void handleLeftClick(int mouseX, int mouseY) {
        if (this.mouseOver(this.getPosition().x, this.getPosition().y + 6.0F, this.WIDTH, 14.0F)) {
            SoundUtil.clickSound();
        }

    }

    public void handleLeftDrag(int mouseX, int mouseY) {
        if (this.mouseOver(this.getPosition().x, this.getPosition().y + 6.0F, this.WIDTH, 14.0F) && !this.getModuleComponent().getParentWindow().isDragging() && this.getGUI().getMouse().isLeftHeld()) {
            this.lastDraggingMouseX = (float) mouseX;
            float percentFilled = ((float) mouseX - this.getPosition().x) * 130.0F / (this.getPosition().x + (this.WIDTH - 6.0F) - this.getPosition().x);
            Number max = (Number) this.getSetting().getMax();
            Number min = (Number) this.getSetting().getMin();

            if (this.getSetting().getValue() instanceof Double) {
                this.getSetting().setValue(Double.valueOf(MathUtil.roundDouble((double) percentFilled * ((max.doubleValue() - min.doubleValue()) / 130.0D) + min.doubleValue(), this.getSetting().getRoundingScale())));
            } else if (this.getSetting().getValue() instanceof Float) {
                this.getSetting().setValue(Float.valueOf(MathUtil.roundFloat((double) (percentFilled * (float) ((double) (max.floatValue() - min.floatValue()) / 130.0D) + min.floatValue()), this.getSetting().getRoundingScale())));
            }
        }

        this.pixAdd = this.lastDraggingMouseX == 0.0F ? (this.getPosition().x + (this.WIDTH - 6.0F) - this.getPosition().x) * (((Number) this.getSetting().getValue()).floatValue() - ((Number) this.getSetting().getMin()).floatValue()) / (((Number) this.getSetting().getMax()).floatValue() - ((Number) this.getSetting().getMin()).floatValue()) : this.lastDraggingMouseX - this.getPosition().x;
        if (this.mouseOver(this.getPosition().x, this.getPosition().y + 6.0F, 5.0F, 14.0F)) {
            if (this.getGUI().getMouse().isLeftHeld()) {
                this.getSetting().setValue(this.getSetting().getMin());
            }
        } else if (this.mouseOver(this.getPosition().x + (this.WIDTH - 6.0F), this.getPosition().y + 6.0F, 5.0F, 14.0F) && this.getGUI().getMouse().isLeftHeld()) {
            this.getSetting().setValue(this.getSetting().getMax());
        }

        if (((Number) this.getSetting().getValue()).equals(this.getSetting().getMin())) {
            this.pixAdd = 3.0F;
        }

        if (((Number) this.getSetting().getValue()).equals(this.getSetting().getMax())) {
            this.pixAdd = this.WIDTH - 6.0F;
        }

    }

    public void handleRightClick(int mouseX, int mouseY) {
        if (this.mouseOver(this.getPosition().x + (this.WIDTH - 6.0F), this.getPosition().y, 5.0F, 6.0F)) {
            this.typing = !this.typing;
            this.insertionTimer.reset();
        }

    }

    public void handleKeyPress(char typedCharacter, int key) {
        if (this.typing) {
            if (key == 28) {
                try {
                    if (this.getSetting().getValue() instanceof Double) {
                        double typedText = Double.parseDouble(this.typedValue);

                        if (typedText > ((Double) ((Number) this.getSetting().getMax())).doubleValue()) {
                            this.getSetting().setValue(this.getSetting().getMax());
                            return;
                        }

                        if (typedText < ((Double) ((Number) this.getSetting().getMin())).doubleValue()) {
                            this.getSetting().setValue(this.getSetting().getMin());
                            return;
                        }

                        this.getSetting().setValue(Double.valueOf(typedText));
                    } else if (this.getSetting().getValue() instanceof Float) {
                        float typedText1 = Float.parseFloat(this.typedValue);

                        if (typedText1 > ((Float) ((Number) this.getSetting().getMax())).floatValue()) {
                            this.getSetting().setValue(this.getSetting().getMax());
                            return;
                        }

                        if (typedText1 < ((Float) ((Number) this.getSetting().getMin())).floatValue()) {
                            this.getSetting().setValue(this.getSetting().getMin());
                            return;
                        }

                        this.getSetting().setValue(Float.valueOf(typedText1));
                    }

                    this.typing = false;
                } catch (NumberFormatException numberformatexception) {
                    ;
                }
            } else {
                String typedText2 = "";

                if (ChatAllowedCharacters.isAllowedCharacter(typedCharacter)) {
                    typedText2 = typedCharacter + "";
                } else if (key == 14 && this.typedValue.length() >= 1) {
                    this.typedValue = this.typedValue.substring(0, this.typedValue.length() - 1);
                }

                this.typedValue = this.typedValue + typedText2;
            }
        }

    }

    public void handleScroll(int scroll) {}

    public String getInsertionPoint() {
        if (this.insertionTimer.passed(500L, Timer.Format.SYSTEM)) {
            this.insertionTimer.reset();
            this.insertion = !this.insertion;
        }

        return this.insertion && this.typing ? "｜" : "";
    }
}
