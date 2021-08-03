package cope.cosmos.loader;

import cope.cosmos.8;
import cope.cosmos.c;
import cope.cosmos.client.Cosmos;
import java.util.function.Consumer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
    modid = "cosmos",
    name = "Cosmos",
    version = "1.0.0"
)
public class Loader {

    Cosmos cosmos = new Cosmos();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        c.c().forEach((measure) -> {
            try {
                measure.1();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });
        this.cosmos.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        this.cosmos.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        this.cosmos.postInit(event);
        System.out.println("Welcome to Cosmos, " + 8.0());
    }
}
