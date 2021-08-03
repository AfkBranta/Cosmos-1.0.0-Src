package cope.cosmos.client.clickgui.cosmos.navigation;

public abstract class Navigation {

    public float SCREEN_WIDTH;
    public float SCREEN_HEIGHT;

    public abstract void drawNavigation();

    public abstract void handleLeftClick(int i, int j);

    public abstract void handleRightClick(int i, int j);

    public abstract void handleKeyPress(char c0, int i);

    public abstract void handleScroll(int i);
}
