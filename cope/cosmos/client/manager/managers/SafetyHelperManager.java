package cope.cosmos.client.manager.managers;

import cope.cosmos.client.events.SettingEnableEvent;
import cope.cosmos.client.features.modules.combat.Surround;
import cope.cosmos.client.features.modules.movement.PacketFlight;
import cope.cosmos.client.manager.Manager;
import cope.cosmos.util.Wrapper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SafetyHelperManager extends Manager implements Wrapper {

    public SafetyHelperManager() {
        super("SafetyHelperManager", "Makes sure certain features are toggled safely", 12);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void initialize(Manager manager) {
        new SafetyHelperManager();
    }

    @SubscribeEvent
    public void onSettingChange(SettingEnableEvent event) {
        if (!SafetyHelperManager.mc.isIntegratedServerRunning() && SafetyHelperManager.mc.getCurrentServerData() != null) {
            String s = SafetyHelperManager.mc.getCurrentServerData().serverIP.toLowerCase();
            byte b0 = -1;

            switch (s.hashCode()) {
            case -1803232345:
                if (s.equals("2b2tpvp.net")) {
                    b0 = 2;
                }
                break;

            case -922552073:
                if (s.equals("9b9t.com")) {
                    b0 = 4;
                }
                break;

            case -437714968:
                if (s.equals("2b2t.org")) {
                    b0 = 3;
                }
                break;

            case 1763967066:
                if (s.equals("crystalpvp.cc")) {
                    b0 = 0;
                }
                break;

            case 1949916426:
                if (s.equals("us.crystalpvp.cc")) {
                    b0 = 1;
                }
            }

            switch (b0) {
            case 0:
            case 1:
                if (event.getSetting().getModule().getName().equals("PacketFlight") && event.getSetting().getName().equals("Mode") && !event.getSetting().getValue().equals(PacketFlight.Mode.PATCH)) {
                    this.pushSafetyNotification("PacketFlight is patched on this server! Using modes other than \'Patch\' may cause issues.");
                } else if (event.getSetting().getModule().getName().equals("Offhand") && event.getSetting().getName().equals("Health") && ((Double) event.getSetting().getValue()).doubleValue() < 14.0D) {
                    this.pushSafetyNotification("Zero tick AutoCrystal\'s are enabled on this server! Using a health less than 14 may cause totem fails.");
                }
                break;

            case 2:
                if (event.getSetting().getModule().getName().equals("Surround") && event.getSetting().getName().equals("Surround") && event.getSetting().getValue().equals(Surround.SurroundVectors.BASE)) {
                    this.pushSafetyNotification("Mode \'Base\' for surround is patched on this server!");
                } else if (event.getSetting().getModule().getName().equals("Burrow")) {
                    this.pushSafetyNotification("Burrow is patched on this server!");
                }
                break;

            case 3:
                if (event.getSetting().getModule().getName().equals("Surround") && event.getSetting().getName().equals("Surround") && event.getSetting().getValue().equals(Surround.SurroundVectors.BASE)) {
                    this.pushSafetyNotification("Mode \'Base\' for surround is patched on this server!");
                }

            case 4:
            }
        }

    }

    public void pushSafetyNotification(String message) {}
}
