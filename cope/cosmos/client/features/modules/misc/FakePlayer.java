package cope.cosmos.client.features.modules.misc;

import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.util.world.WorldUtil;
import java.util.concurrent.ThreadLocalRandom;

public class FakePlayer extends Module {

    public static FakePlayer INSTANCE;
    public static Setting inventory = new Setting("Inventory", "Sync the fake player inventory", Boolean.valueOf(true));
    public static Setting health = new Setting("Health", "Sync the fakeplayer health", Boolean.valueOf(true));
    public int id = -1;

    public FakePlayer() {
        super("FakePlayer", Category.MISC, "Spawns in a indestructible client-side player");
        FakePlayer.INSTANCE = this;
        this.setExempt(true);
    }

    public void onEnable() {
        super.onEnable();
        WorldUtil.createFakePlayer(FakePlayer.mc.player.getGameProfile(), this.id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE), ((Boolean) FakePlayer.inventory.getValue()).booleanValue(), ((Boolean) FakePlayer.health.getValue()).booleanValue());
    }

    public void onDisable() {
        super.onDisable();
        FakePlayer.mc.world.removeEntityFromWorld(this.id);
        this.id = -1;
    }

    public int getID() {
        return this.id;
    }
}
