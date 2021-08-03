package cope.cosmos.client.manager.managers;

import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.manager.Manager;
import cope.cosmos.util.Wrapper;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ThreadManager extends Manager {

    ThreadManager.ModuleService moduleService = new ThreadManager.ModuleService();

    public ThreadManager() {
        super("ThreadManager", "Manages the main client service thread", 15);
        this.moduleService.setDaemon(true);
        this.moduleService.start();
    }

    public ThreadManager.ModuleService getService() {
        return this.moduleService;
    }

    public static class ModuleService extends Thread implements Wrapper {

        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (!this.nullCheck()) {
                        yield();
                    } else {
                        ModuleManager.getModules((module) -> {
                            return module.isEnabled();
                        }).forEach((module) -> {
                            try {
                                module.onThread();
                            } catch (Exception exception) {
                                ;
                            }

                        });
                    }
                } catch (Exception exception) {
                    ;
                }
            }

        }
    }
}
