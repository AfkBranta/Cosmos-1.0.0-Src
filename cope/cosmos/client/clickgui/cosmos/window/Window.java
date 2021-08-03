package cope.cosmos.client.clickgui.cosmos.window;

public abstract class Window {

    private boolean expanding;
    private boolean dragging;

    public abstract void drawWindow();

    public abstract void handleLeftClick(int i, int j);

    public abstract void handleLeftDrag(int i, int j);

    public abstract void handleRightClick(int i, int j);

    public abstract void handleKeyPress(char c0, int i);

    public abstract void handleScroll(int i);

    public boolean isExpanding() {
        return this.expanding;
    }

    public void setExpanding(boolean in) {
        this.expanding = in;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public void setDragging(boolean in) {
        this.dragging = in;
    }
}
