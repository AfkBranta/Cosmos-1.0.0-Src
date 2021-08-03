package cope.cosmos.client.clickgui.cosmos.component.components;

import cope.cosmos.client.clickgui.cosmos.component.SettingComponent;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.client.manager.managers.AnimationManager;
import cope.cosmos.util.Wrapper;
import cope.cosmos.util.render.FontUtil;
import cope.cosmos.util.render.RenderUtil;
import cope.cosmos.util.world.SoundUtil;
import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import org.lwjgl.opengl.GL11;

public class ColorComponent extends SettingComponent implements Wrapper {

    float PICKER_HEIGHT = 64.0F;
    private int hoverAnimation = 0;
    private Vec2f selectorPosition;
    private float brightnessPosition;
    private float transparencyPosition;
    private final ColorComponent.ColorHolder selectedColor;
    private final AnimationManager animationManager = new AnimationManager(200, false);
    private boolean open;

    public ColorComponent(Setting setting, ModuleComponent moduleComponent) {
        super(setting, moduleComponent);
        float[] hsbColor = Color.RGBtoHSB(((Color) setting.getValue()).getRed(), ((Color) setting.getValue()).getGreen(), ((Color) setting.getValue()).getBlue(), (float[]) null);

        this.selectedColor = new ColorComponent.ColorHolder(hsbColor[0], hsbColor[1], hsbColor[2], (float) ((Color) setting.getValue()).getAlpha() / 255.0F);
        this.open = false;
        this.animationManager.setStateHard(false);
    }

    public void drawSettingComponent(Vec2f position) {
        this.setPosition(position);
        if (this.mouseOver(position.x, position.y, this.WIDTH, 14.0F) && this.hoverAnimation < 25) {
            this.hoverAnimation += 5;
        } else if (!this.mouseOver(position.x, position.y, this.WIDTH, 14.0F) && this.hoverAnimation > 0) {
            this.hoverAnimation -= 5;
        }

        Color settingColor = this.isSubSetting() ? new Color(12 + this.hoverAnimation, 12 + this.hoverAnimation, 17 + this.hoverAnimation) : new Color(18 + this.hoverAnimation, 18 + this.hoverAnimation, 24 + this.hoverAnimation);

        RenderUtil.drawRect(position.x, position.y, this.WIDTH, 14.0F, settingColor);
        RenderUtil.drawRect(position.x, position.y, this.WIDTH, 14.0F, settingColor);
        RenderUtil.drawRoundedRect((double) (position.x + this.WIDTH - 12.0F), (double) (position.y + 2.0F), 10.0D, 10.0D, 2.0D, (Color) this.getSetting().getValue());
        GL11.glScaled(0.55D, 0.55D, 0.55D);
        float pickerAnimation = (position.x + 4.0F) * 1.8181818F;
        float PICKER_X = (position.y + 5.0F) * 1.8181818F;

        FontUtil.drawStringWithShadow(this.getSetting().getName(), pickerAnimation, PICKER_X, -1);
        GL11.glScaled(1.81818181D, 1.81818181D, 1.81818181D);
        pickerAnimation = (float) MathHelper.clamp(this.animationManager.getAnimationFactor(), 0.0D, 1.0D);
        PICKER_X = position.x + 2.0F;
        float PICKER_Y = position.y + 14.0F + 2.0F;
        Vec2f centerPosition = new Vec2f(PICKER_X + (this.WIDTH - 34.0F) / 2.0F, PICKER_Y + this.PICKER_HEIGHT / 2.0F);
        float RADIUS = this.PICKER_HEIGHT / 2.0F;

        if (pickerAnimation > 0.0F) {
            if (this.mouseOver(PICKER_X, PICKER_Y, this.WIDTH - 34.0F, this.PICKER_HEIGHT) && this.getGUI().getMouse().isLeftHeld() && !this.getModuleComponent().getParentWindow().isDragging() && this.isWithinCircle((double) centerPosition.x, (double) centerPosition.y, (double) RADIUS, this.getGUI().getMouse().getMousePosition().x, this.getGUI().getMouse().getMousePosition().y)) {
                this.setSelectorPosition(new Vec2f(this.getGUI().getMouse().getMousePosition().x, this.getGUI().getMouse().getMousePosition().y));
                float color = this.selectorPosition.x - centerPosition.x;
                float yDistance = this.selectorPosition.y - centerPosition.y;
                double radius = Math.hypot((double) color, (double) yDistance);
                double angle = -Math.toDegrees(Math.atan2((double) yDistance, (double) color) + 1.5707963267948966D) % 360.0D;

                this.selectedColor.setHue((float) (angle / 360.0D));
                this.selectedColor.setSaturation((float) (radius / (double) RADIUS));
            }

            if (this.mouseOver(PICKER_X + this.WIDTH - 26.0F, PICKER_Y + 2.0F, 3.0F, this.PICKER_HEIGHT - 2.0F) && this.getGUI().getMouse().isLeftHeld() && !this.getModuleComponent().getParentWindow().isDragging() && this.isWithinRect(PICKER_X + this.WIDTH - 26.0F, PICKER_Y, this.PICKER_HEIGHT, this.getGUI().getMouse().getMousePosition().x, this.getGUI().getMouse().getMousePosition().y)) {
                this.setBrightnessPosition(this.getGUI().getMouse().getMousePosition().y);
                this.selectedColor.setBrightness(1.0F - (this.brightnessPosition - (PICKER_Y + 2.0F)) / this.PICKER_HEIGHT);
            }

            if (this.mouseOver(PICKER_X + this.WIDTH - 12.0F, PICKER_Y + 2.0F, 3.0F, this.PICKER_HEIGHT - 2.0F) && this.getGUI().getMouse().isLeftHeld() && !this.getModuleComponent().getParentWindow().isDragging() && this.isWithinRect(PICKER_X + this.WIDTH - 12.0F, PICKER_Y, this.PICKER_HEIGHT, this.getGUI().getMouse().getMousePosition().x, this.getGUI().getMouse().getMousePosition().y)) {
                this.setTransparencyPosition(this.getGUI().getMouse().getMousePosition().y);
                this.selectedColor.setTransparency(1.0F - (this.transparencyPosition - (PICKER_Y + 2.0F)) / this.PICKER_HEIGHT);
            }

            int color1 = Color.HSBtoRGB(this.selectedColor.getHue(), this.selectedColor.getSaturation(), this.selectedColor.getBrightness());

            this.getSetting().setValue(new Color((float) (color1 >> 16 & 255) / 255.0F, (float) (color1 >> 8 & 255) / 255.0F, (float) (color1 & 255) / 255.0F, MathHelper.clamp(this.selectedColor.getTransparency(), 0.0F, 1.0F)));
            this.drawColorPicker((int) PICKER_X, (int) PICKER_Y);
            this.drawSelector((float) ((double) centerPosition.x + (double) (this.selectedColor.getSaturation() * RADIUS) * Math.cos(Math.toRadians((double) (this.selectedColor.getHue() * 360.0F)) + 1.5707963267948966D)), (float) ((double) centerPosition.y - (double) (this.selectedColor.getSaturation() * RADIUS) * Math.sin(Math.toRadians((double) (this.selectedColor.getHue() * 360.0F)) + 1.5707963267948966D)), 1.5F);
            this.drawSlider(PICKER_X + this.WIDTH - 26.0F, PICKER_Y + 2.0F, 3.0F, this.PICKER_HEIGHT - 2.0F, 2, false);
            this.drawSelector(PICKER_X + this.WIDTH - 24.5F, PICKER_Y + 2.0F + this.PICKER_HEIGHT * (1.0F - this.selectedColor.getBrightness()), 2.0F);
            this.drawSlider(PICKER_X + this.WIDTH - 12.0F, PICKER_Y + 2.0F, 3.0F, this.PICKER_HEIGHT - 2.0F, 2, true);
            this.drawSelector(PICKER_X + this.WIDTH - 10.5F, PICKER_Y + 2.0F + this.PICKER_HEIGHT * (1.0F - this.selectedColor.getTransparency()), 2.0F);
            if (this.isSubSetting()) {
                this.getSettingComponent().setSettingOffset(pickerAnimation * 4.857F);
            }

            this.getModuleComponent().getParentWindow().setModuleOffset(pickerAnimation * 4.857F);
            this.getModuleComponent().setSettingOffset(pickerAnimation * 4.857F);
        }

    }

    public void drawColorPicker(int x, int y) {
        ColorComponent.mc.getTextureManager().bindTexture(new ResourceLocation("cosmos", "textures/imgs/picker.png"));
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, (int) (this.WIDTH - 34.0F), (int) this.PICKER_HEIGHT, this.WIDTH - 34.0F, this.PICKER_HEIGHT);
    }

    public void drawSlider(float x, float y, float width, float height, int radius, boolean transparency) {
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0F;
        y *= 2.0F;
        width *= 2.0F;
        height *= 2.0F;
        width += x;
        height += y;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBegin(9);
        int color = Color.HSBtoRGB(this.selectedColor.getHue(), this.selectedColor.getSaturation(), 1.0F);
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        GL11.glColor4f(red, green, blue, 1.0F);

        int i;

        for (i = 0; i <= 90; ++i) {
            GL11.glVertex2d((double) (x + (float) radius) + Math.sin((double) i * 3.141592653589793D / 180.0D) * (double) radius * -1.0D, (double) (y + (float) radius) + Math.cos((double) i * 3.141592653589793D / 180.0D) * (double) radius * -1.0D);
        }

        if (transparency) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
        }

        for (i = 90; i <= 180; ++i) {
            GL11.glVertex2d((double) (x + (float) radius) + Math.sin((double) i * 3.141592653589793D / 180.0D) * (double) radius * -1.0D, (double) (height - (float) radius) + Math.cos((double) i * 3.141592653589793D / 180.0D) * (double) radius * -1.0D);
        }

        if (transparency) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
        }

        for (i = 0; i <= 90; ++i) {
            GL11.glVertex2d((double) (width - (float) radius) + Math.sin((double) i * 3.141592653589793D / 180.0D) * (double) radius, (double) (height - (float) radius) + Math.cos((double) i * 3.141592653589793D / 180.0D) * (double) radius);
        }

        GL11.glColor4f(red, green, blue, 1.0F);

        for (i = 90; i <= 180; ++i) {
            GL11.glVertex2d((double) (width - (float) radius) + Math.sin((double) i * 3.141592653589793D / 180.0D) * (double) radius, (double) (y + (float) radius) + Math.cos((double) i * 3.141592653589793D / 180.0D) * (double) radius);
        }

        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glScaled(2.0D, 2.0D, 2.0D);
        GL11.glPopAttrib();
    }

    public void drawSelector(float x, float y, float radius) {
        RenderUtil.drawPolygon((double) x, (double) y, radius, 360, Color.WHITE);
    }

    private boolean isWithinCircle(double x, double y, double radius, float mouseX, float mouseY) {
        return Math.sqrt(Math.pow((double) mouseX - x, 2.0D) + Math.pow((double) mouseY - y, 2.0D)) <= radius;
    }

    private boolean isWithinRect(float x, float y, float height, float mouseX, float mouseY) {
        return mouseX > x && mouseY > y && mouseX < x + 4.0F && mouseY < y + height;
    }

    public Color alphaIntegrate(Color color, float alpha) {
        float red = (float) color.getRed() / 255.0F;
        float green = (float) color.getGreen() / 255.0F;
        float blue = (float) color.getBlue() / 255.0F;

        return new Color(red, green, blue, alpha);
    }

    public void handleLeftClick(int mouseX, int mouseY) {
        if (this.mouseOver(this.getPosition().x, this.getPosition().y, this.WIDTH, this.PICKER_HEIGHT)) {
            SoundUtil.clickSound();
        }

    }

    public void handleLeftDrag(int mouseX, int mouseY) {}

    public void handleRightClick(int mouseX, int mouseY) {
        if (this.mouseOver(this.getPosition().x, this.getPosition().y, this.WIDTH, 14.0F)) {
            SoundUtil.clickSound();
            this.open = !this.open;
            this.animationManager.setState(this.open);
        }

    }

    public void handleKeyPress(char typedCharacter, int key) {}

    public void handleScroll(int scroll) {}

    public void setSelectorPosition(Vec2f in) {
        this.selectorPosition = in;
    }

    public void setBrightnessPosition(float in) {
        this.brightnessPosition = in;
    }

    public void setTransparencyPosition(float in) {
        this.transparencyPosition = in;
    }

    public static class ColorHolder {

        private float hue;
        private float saturation;
        private float brightness;
        private float transparency;

        public ColorHolder(float hue, float saturation, float brightness, float transparency) {
            this.hue = hue;
            this.saturation = saturation;
            this.brightness = brightness;
            this.transparency = transparency;
        }

        public float getHue() {
            return this.hue;
        }

        public void setHue(float in) {
            this.hue = in;
        }

        public float getSaturation() {
            return this.saturation;
        }

        public void setSaturation(float in) {
            this.saturation = in;
        }

        public float getBrightness() {
            return this.brightness;
        }

        public void setBrightness(float in) {
            this.brightness = in;
        }

        public float getTransparency() {
            return this.transparency;
        }

        public void setTransparency(float in) {
            this.transparency = in;
        }
    }
}
