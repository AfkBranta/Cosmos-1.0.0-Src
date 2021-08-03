package cope.cosmos.client.clickgui.cosmos.panel;

import net.minecraft.util.math.Vec2f;

public abstract class Panel {

    public final float WIDTH = 150.0F;
    public final float TITLE = 19.0F;
    public final float BAR = 2.0F;
    public final float HEIGHT = 200.0F;

    public abstract void drawPanel(Vec2f vec2f);

    public abstract void handleLeftClick(int i, int j);

    public abstract void handleRightClick(int i, int j);

    public abstract void handleKeyPress(char c0, int i);

    public abstract void handleScroll(int i);

    public float getWidth() {
        return 150.0F;
    }

    public float getHeight() {
        return 200.0F;
    }
}
