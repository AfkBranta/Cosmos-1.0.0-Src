package cope.cosmos.client.clickgui.cosmos.component;

import cope.cosmos.client.clickgui.cosmos.window.windows.CategoryWindow;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.manager.managers.AnimationManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.Vec2f;

public abstract class Component {

    public final float WIDTH = 100.0F;
    public final float BAR = 2.0F;
    public final float HEIGHT = 14.0F;
    private final CategoryWindow parentWindow;
    private final Module module;
    private AnimationManager animationManager;
    private final List settingComponents = new ArrayList();

    public Component(Module module, CategoryWindow parentWindow) {
        this.module = module;
        this.parentWindow = parentWindow;
        if (module != null) {
            this.animationManager = new AnimationManager(200, false);
        }

    }

    public abstract void drawComponent(Vec2f vec2f);

    public abstract void handleLeftClick(int i, int j);

    public abstract void handleLeftDrag(int i, int j);

    public abstract void handleRightClick(int i, int j);

    public abstract void handleKeyPress(char c0, int i);

    public abstract void handleScroll(int i);

    public CategoryWindow getParentWindow() {
        return this.parentWindow;
    }

    public Module getModule() {
        return this.module;
    }

    public AnimationManager getAnimation() {
        return this.animationManager;
    }

    public List getSettingComponents() {
        return this.settingComponents;
    }
}
