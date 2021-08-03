package cope.cosmos.client.manager.managers;

import cope.cosmos.client.events.PacketEvent;
import cope.cosmos.client.manager.Manager;
import cope.cosmos.util.system.Timer;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SwitchManager extends Manager {

    Timer switchTimer = new Timer();

    public SwitchManager() {
        super("SwitchManager", "Manages the NCP switch cooldown", 14);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void initialize(Manager manager) {
        new SwitchManager();
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketHeldItemChange) {
            this.switchTimer.reset();
        }

    }

    public boolean switchAttackReady(long switchDelay) {
        return !this.switchTimer.passed(switchDelay, Timer.Format.SYSTEM);
    }
}
