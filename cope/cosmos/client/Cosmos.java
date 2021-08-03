package cope.cosmos.client;

import com.mojang.brigadier.CommandDispatcher;
import cope.cosmos.client.clickgui.cosmos.CosmosGUI;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.manager.managers.CommandManager;
import cope.cosmos.client.manager.managers.EventManager;
import cope.cosmos.client.manager.managers.FontManager;
import cope.cosmos.client.manager.managers.NotificationManager;
import cope.cosmos.client.manager.managers.PresenceManager;
import cope.cosmos.client.manager.managers.PresetManager;
import cope.cosmos.client.manager.managers.ReloadManager;
import cope.cosmos.client.manager.managers.RotationManager;
import cope.cosmos.client.manager.managers.SafetyHelperManager;
import cope.cosmos.client.manager.managers.SocialManager;
import cope.cosmos.client.manager.managers.SwitchManager;
import cope.cosmos.client.manager.managers.ThreadManager;
import cope.cosmos.client.manager.managers.TickManager;
import cope.cosmos.util.render.FontUtil;
import java.util.Arrays;
import java.util.List;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.opengl.Display;

public class Cosmos {

    public static final String MOD_ID = "cosmos";
    public static final String NAME = "Cosmos";
    public static final String VERSION = "1.0.0";
    private final ProgressBar progressManager = ProgressManager.push("Cosmos", 12);
    public static String PREFIX = "*";
    public static boolean SETUP = false;
    public static Cosmos INSTANCE;
    private CosmosGUI cosmosGUI;
    private TickManager tickManager;
    private SwitchManager switchManager;
    private SocialManager socialManager;
    private PresetManager presetManager;
    private RotationManager rotationManager;
    private ThreadManager threadManager;
    private FontManager fontManager;
    private NotificationManager notificationManager;
    private ReloadManager reloadManager;
    private SafetyHelperManager safetyHelperManager;
    private CommandDispatcher commandDispatcher;

    public Cosmos() {
        Cosmos.INSTANCE = this;
    }

    public void preInit(FMLPreInitializationEvent event) {
        FontUtil.load();
    }

    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(EventManager.INSTANCE);
        this.step("Registering Events");
        this.commandDispatcher = new CommandDispatcher();
        CommandManager.registerCommands();
        this.step("Loading Commands");
        this.tickManager = new TickManager();
        this.step("Setting up Tick Manager");
        this.rotationManager = new RotationManager();
        this.step("Setting up Rotation Manager");
        this.switchManager = new SwitchManager();
        this.step("Setting up Switch Manager");
        this.socialManager = new SocialManager();
        this.step("Setting up Social Manager");
        this.presetManager = new PresetManager();
        this.step("Setting up Config Manager");
        this.cosmosGUI = new CosmosGUI();
        this.step("Setting up GUI\'s");
        this.reloadManager = new ReloadManager();
        this.step("Setting up Reload Manager");
        this.notificationManager = new NotificationManager();
        this.step("Setting up Notification Manager");
        this.safetyHelperManager = new SafetyHelperManager();
        this.step("Setting up Safety Helper");
        this.threadManager = new ThreadManager();
        this.step("Setting up Threads");
        ProgressManager.pop(this.progressManager);
    }

    public void postInit(FMLPostInitializationEvent event) {
        Display.setTitle("Cosmos 1.0.0");
        PresenceManager.startPresence();
    }

    public void step(String info) {
        this.progressManager.step(info);
        System.out.println(info);
    }

    public CosmosGUI getCosmosGUI() {
        return this.cosmosGUI;
    }

    public TickManager getTickManager() {
        return this.tickManager;
    }

    public SwitchManager getSwitchManager() {
        return this.switchManager;
    }

    public SocialManager getSocialManager() {
        return this.socialManager;
    }

    public PresetManager getPresetManager() {
        return this.presetManager;
    }

    public FontManager getFontManager() {
        return this.fontManager;
    }

    public RotationManager getRotationManager() {
        return this.rotationManager;
    }

    public ThreadManager getThreadManager() {
        return this.threadManager;
    }

    public ReloadManager getReloadManager() {
        return this.reloadManager;
    }

    public SafetyHelperManager getSafetyHelperManager() {
        return this.safetyHelperManager;
    }

    public NotificationManager getNotificationManager() {
        return this.notificationManager;
    }

    public CommandDispatcher getCommandDispatcher() {
        return this.commandDispatcher;
    }

    public List getNullSafeMods() {
        return Arrays.asList(new Module[0]);
    }
}
