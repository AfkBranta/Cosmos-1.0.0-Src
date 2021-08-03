package cope.cosmos.client.clickgui.cosmos;

import cope.cosmos.client.Cosmos;
import cope.cosmos.client.clickgui.cosmos.navigation.navs.ControlNavigation;
import cope.cosmos.client.clickgui.cosmos.navigation.navs.PlayerNavigation;
import cope.cosmos.client.clickgui.cosmos.util.Util;
import cope.cosmos.client.clickgui.cosmos.window.Window;
import cope.cosmos.client.clickgui.cosmos.window.WindowManager;
import cope.cosmos.client.clickgui.cosmos.window.windows.CategoryWindow;
import cope.cosmos.client.features.modules.client.ClickGUI;
import java.io.IOException;
import java.util.Iterator;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.Vec2f;
import org.lwjgl.input.Mouse;

public class CosmosGUI extends GuiScreen implements Util {

    private final CosmosGUI.MousePosition mouse;
    PlayerNavigation playerNavigation;
    ControlNavigation controlNavigation;

    public CosmosGUI() {
        this.mouse = new CosmosGUI.MousePosition(Vec2f.ZERO, false, false, false, false);
        this.playerNavigation = new PlayerNavigation();
        this.controlNavigation = new ControlNavigation();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawDefaultBackground();
        this.mouse.setLeftClick(false);
        this.mouse.setRightClick(false);
        this.mouse.setMousePosition(new Vec2f((float) mouseX, (float) mouseY));
        WindowManager.setFocusedWindow((Window) null);
        Iterator scroll = WindowManager.getWindows().iterator();

        while (scroll.hasNext()) {
            Window window = (Window) scroll.next();

            if (this.mouseOver(((CategoryWindow) window).getPosition().x, ((CategoryWindow) window).getPosition().y, ((CategoryWindow) window).getWidth(), ((CategoryWindow) window).getHeight())) {
                WindowManager.setFocusedWindow(window);
                break;
            }
        }

        WindowManager.getWindows().forEach(accept<invokedynamic>(this));
        if (WindowManager.getFocusedWindow() != null && Mouse.hasWheel()) {
            int scroll1 = Mouse.getDWheel();

            WindowManager.getFocusedWindow().handleScroll(scroll1);
            this.controlNavigation.handleScroll(scroll1);
            this.playerNavigation.handleScroll(scroll1);
        }

        this.controlNavigation.drawNavigation();
        this.playerNavigation.drawNavigation();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        switch (mouseButton) {
        case 0:
            this.mouse.setLeftClick(true);
            this.mouse.setLeftHeld(true);
            WindowManager.getWindows().forEach(accept<invokedynamic>(this, mouseX, mouseY));
            this.controlNavigation.handleLeftClick(mouseX, mouseY);
            this.playerNavigation.handleLeftClick(mouseX, mouseY);
            break;

        case 1:
            this.mouse.setRightClick(true);
            this.mouse.setRightHeld(true);
            WindowManager.getWindows().forEach(accept<invokedynamic>(this, mouseX, mouseY));
            this.controlNavigation.handleRightClick(mouseX, mouseY);
            this.playerNavigation.handleRightClick(mouseX, mouseY);
        }

    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (state == 0) {
            this.mouse.setLeftHeld(false);
            this.mouse.setRightHeld(false);
            WindowManager.getWindows().forEach(accept<invokedynamic>());
        }

    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        WindowManager.getWindows().forEach(accept<invokedynamic>(typedChar, keyCode));
        this.controlNavigation.handleKeyPress(typedChar, keyCode);
        this.playerNavigation.handleKeyPress(typedChar, keyCode);
    }

    public void onGuiClosed() {
        super.onGuiClosed();
        ClickGUI.INSTANCE.disable();
        Cosmos.INSTANCE.getPresetManager().save();
        if (this.mc.entityRenderer.isShaderActive()) {
            this.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
        }

    }

    public boolean doesGuiPauseGame() {
        return ((Boolean) ClickGUI.pauseGame.getValue()).booleanValue();
    }

    public CosmosGUI.MousePosition getMouse() {
        return this.mouse;
    }

    private static void lambda$keyTyped$4(char typedChar, int keyCode, Window window) {
        window.handleKeyPress(typedChar, keyCode);
    }

    private static void lambda$mouseReleased$3(Window window) {
        window.setDragging(false);
        window.setExpanding(false);
    }

    private void lambda$mouseClicked$2(int mouseX, int mouseY, Window window) {
        if (this.mouseOver(((CategoryWindow) window).getPosition().x, ((CategoryWindow) window).getPosition().y, ((CategoryWindow) window).getWidth(), ((CategoryWindow) window).getHeight() - 5.0F) && !window.isExpanding()) {
            window.handleRightClick(mouseX, mouseY);
        }

    }

    private void lambda$mouseClicked$1(int mouseX, int mouseY, Window window) {
        if (this.mouseOver(((CategoryWindow) window).getPosition().x, ((CategoryWindow) window).getPosition().y, ((CategoryWindow) window).getWidth(), ((CategoryWindow) window).getHeight() - 5.0F) && !window.isExpanding()) {
            window.handleLeftClick(mouseX, mouseY);
        }

    }

    private void lambda$drawScreen$0(Window window) {
        window.drawWindow();
        if (this.mouseOver(((CategoryWindow) window).getPosition().x, ((CategoryWindow) window).getPosition().y, ((CategoryWindow) window).getWidth(), ((CategoryWindow) window).getHeight() - 5.0F) && !window.isExpanding()) {
            window.handleLeftDrag((int) this.getMouse().mousePosition.x, (int) this.getMouse().mousePosition.y);
        }

    }

    public static class MousePosition {

        private Vec2f mousePosition;
        private boolean leftClick;
        private boolean rightClick;
        private boolean leftHeld;
        private boolean rightHeld;

        public MousePosition(Vec2f mousePosition, boolean leftClick, boolean rightClick, boolean leftHeld, boolean rightHeld) {
            this.mousePosition = mousePosition;
            this.leftClick = leftClick;
            this.rightClick = rightClick;
            this.leftHeld = leftHeld;
            this.rightHeld = rightHeld;
        }

        public boolean isLeftClick() {
            return this.leftClick;
        }

        public void setLeftClick(boolean in) {
            this.leftClick = in;
        }

        public boolean isRightClick() {
            return this.rightClick;
        }

        public void setRightClick(boolean in) {
            this.rightClick = in;
        }

        public boolean isLeftHeld() {
            return this.leftHeld;
        }

        public void setLeftHeld(boolean in) {
            this.leftHeld = in;
        }

        public boolean isRightHeld() {
            return this.rightHeld;
        }

        public void setRightHeld(boolean in) {
            this.rightHeld = in;
        }

        public void setMousePosition(Vec2f in) {
            this.mousePosition = in;
        }

        public Vec2f getMousePosition() {
            return this.mousePosition;
        }
    }
}
