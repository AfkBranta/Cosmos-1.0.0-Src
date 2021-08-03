package cope.cosmos.client.manager.managers;

import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.modules.client.ClickGUI;
import cope.cosmos.client.features.modules.client.DiscordPresence;
import cope.cosmos.client.features.modules.client.Font;
import cope.cosmos.client.features.modules.client.HUD;
import cope.cosmos.client.features.modules.client.Social;
import cope.cosmos.client.features.modules.combat.Aura;
import cope.cosmos.client.features.modules.combat.AutoCrystal;
import cope.cosmos.client.features.modules.combat.Burrow;
import cope.cosmos.client.features.modules.combat.Criticals;
import cope.cosmos.client.features.modules.combat.HoleFill;
import cope.cosmos.client.features.modules.combat.Offhand;
import cope.cosmos.client.features.modules.combat.Surround;
import cope.cosmos.client.features.modules.misc.ChatModifications;
import cope.cosmos.client.features.modules.misc.FakePlayer;
import cope.cosmos.client.features.modules.misc.Portal;
import cope.cosmos.client.features.modules.misc.Timer;
import cope.cosmos.client.features.modules.movement.ElytraFlight;
import cope.cosmos.client.features.modules.movement.PacketFlight;
import cope.cosmos.client.features.modules.movement.ReverseStep;
import cope.cosmos.client.features.modules.movement.Sprint;
import cope.cosmos.client.features.modules.movement.Velocity;
import cope.cosmos.client.features.modules.player.FastUse;
import cope.cosmos.client.features.modules.player.Interact;
import cope.cosmos.client.features.modules.player.SpeedMine;
import cope.cosmos.client.features.modules.visual.Chams;
import cope.cosmos.client.features.modules.visual.HoleESP;
import cope.cosmos.client.features.modules.visual.NoRender;
import cope.cosmos.client.manager.Manager;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModuleManager extends Manager {

    private static final List modules = Arrays.asList(new Module[] { new ClickGUI(), new DiscordPresence(), new Font(), new Social(), new HUD(), new Aura(), new AutoCrystal(), new Burrow(), new Criticals(), new HoleFill(), new Offhand(), new Surround(), new ChatModifications(), new FakePlayer(), new Portal(), new Timer(), new ElytraFlight(), new PacketFlight(), new ReverseStep(), new Sprint(), new Velocity(), new FastUse(), new Interact(), new SpeedMine(), new Chams(), new HoleESP(), new NoRender()});

    public ModuleManager() {
        super("ModuleManager", "Manages all the client modules", 5);
    }

    public static List getAllModules() {
        return ModuleManager.modules;
    }

    public static List getModules(Predicate predicate) {
        return (List) ModuleManager.modules.stream().filter(predicate).collect(Collectors.toList());
    }

    public static Module getModule(Predicate predicate) {
        return (Module) ModuleManager.modules.stream().filter(predicate).findFirst().orElse((Object) null);
    }
}
