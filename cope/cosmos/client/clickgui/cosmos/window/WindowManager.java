package cope.cosmos.client.clickgui.cosmos.window;

import cope.cosmos.client.clickgui.cosmos.window.windows.CategoryWindow;
import cope.cosmos.client.features.modules.Category;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.math.Vec2f;

public class WindowManager {

    private static Window focusedWindow;
    private static final List windows = Arrays.asList(new Window[] { new CategoryWindow(new Vec2f(570.0F, 70.0F), Category.CLIENT), new CategoryWindow(new Vec2f(460.0F, 70.0F), Category.VISUAL), new CategoryWindow(new Vec2f(350.0F, 70.0F), Category.PLAYER), new CategoryWindow(new Vec2f(240.0F, 70.0F), Category.MISC), new CategoryWindow(new Vec2f(130.0F, 70.0F), Category.MOVEMENT), new CategoryWindow(new Vec2f(20.0F, 70.0F), Category.COMBAT)});

    public static Window getFocusedWindow() {
        return WindowManager.focusedWindow;
    }

    public static void setFocusedWindow(Window in) {
        WindowManager.focusedWindow = in;
    }

    public static List getWindows() {
        return WindowManager.windows;
    }
}
