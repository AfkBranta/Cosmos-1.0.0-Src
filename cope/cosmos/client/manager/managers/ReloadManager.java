package cope.cosmos.client.manager.managers;

import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.manager.Manager;
import cope.cosmos.util.Wrapper;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class ReloadManager extends Manager implements Wrapper {

    public ReloadManager() {
        super("ReloadManager", "Reloads all modules when loading a new world", 10);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void initialize(Manager manager) {
        new ReloadManager();
    }

    @SubscribeEvent
    public void onJoinWorld(ClientTickEvent event) {
        if (this.nullCheck() && ReloadManager.mc.player.ticksExisted == 10) {
            List enabledModules = ModuleManager.getModules(Module::isEnabled);

            ModuleManager.getAllModules().forEach((module) -> {
                if (!module.isExempt()) {
                    module.disable();
                }

            });
            enabledModules.forEach((module) -> {
                if (!module.isExempt()) {
                    module.enable();
                }

            });
        }

    }
}
