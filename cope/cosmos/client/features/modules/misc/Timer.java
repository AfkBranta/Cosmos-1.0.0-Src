package cope.cosmos.client.features.modules.misc;

import cope.cosmos.client.Cosmos;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;

public class Timer extends Module {

    public static Setting multiplier = new Setting("Multiplier", "Multiplier for the client side tick speed", Double.valueOf(0.0D), Double.valueOf(4.0D), Double.valueOf(50.0D), 1);

    public Timer() {
        super("Timer", Category.MISC, "Allows you to change the client side tick speed");
    }

    public void onUpdate() {
        Cosmos.INSTANCE.getTickManager().setClientTicks(((Double) Timer.multiplier.getValue()).doubleValue());
    }
}
