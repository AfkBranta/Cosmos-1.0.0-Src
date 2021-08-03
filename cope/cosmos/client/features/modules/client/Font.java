package cope.cosmos.client.features.modules.client;

import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;

public class Font extends Module {

    public static Font INSTANCE;
    public static Setting vanilla = new Setting("Vanilla", "Overrides the minecraft vanilla font", Boolean.valueOf(false));

    public Font() {
        super("Font", Category.CLIENT, "Allows you to customize the client font.");
        this.setDrawn(false);
        this.setExempt(true);
        Font.INSTANCE = this;
    }
}
