package cope.cosmos.client.clickgui.cosmos.util;

import cope.cosmos.client.Cosmos;
import cope.cosmos.client.clickgui.cosmos.CosmosGUI;

public interface Util {

    default boolean mouseOver(float x, float y, float width, float height) {
        return !Float.isNaN(this.getGUI().getMouse().getMousePosition().x) && !Float.isNaN(this.getGUI().getMouse().getMousePosition().y) ? this.getGUI().getMouse().getMousePosition().x >= x && this.getGUI().getMouse().getMousePosition().y >= y && this.getGUI().getMouse().getMousePosition().x <= x + width && this.getGUI().getMouse().getMousePosition().y <= y + height : false;
    }

    default CosmosGUI getGUI() {
        return Cosmos.INSTANCE.getCosmosGUI();
    }

    default int getGlobalAnimation() {
        return Cosmos.INSTANCE.getCosmosGUI().getGlobalAnimation();
    }
}
