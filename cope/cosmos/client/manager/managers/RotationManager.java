package cope.cosmos.client.manager.managers;

import cope.cosmos.client.events.PacketEvent;
import cope.cosmos.client.events.RenderLivingEntityEvent;
import cope.cosmos.client.manager.Manager;
import cope.cosmos.util.Wrapper;
import cope.cosmos.util.player.Rotation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class RotationManager extends Manager implements Wrapper {

    private float headPitch = -1.0F;
    private Rotation serverRotation;

    public RotationManager() {
        super("RotationManager", "Keeps track of server rotations", 11);
        this.serverRotation = new Rotation(0.0F, 0.0F, Rotation.Rotate.NONE);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void initialize(Manager manager) {
        new RotationManager();
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (this.nullCheck()) {
            this.headPitch = -1.0F;
        }

    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketPlayer) {
            this.serverRotation = new Rotation(((CPacketPlayer) event.getPacket()).getYaw(0.0F), ((CPacketPlayer) event.getPacket()).getPitch(0.0F), Rotation.Rotate.NONE);
        }

    }

    @SubscribeEvent
    public void onRenderLivingEntity(RenderLivingEntityEvent event) {
        if (event.getEntityLivingBase().equals(RotationManager.mc.player)) {
            event.setCanceled(true);
            event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), this.headPitch == -1.0F ? RotationManager.mc.player.rotationPitch : this.headPitch, event.getScaleFactor());
        }

    }

    public void setHeadPitch(float in) {
        this.headPitch = in;
    }

    public Rotation getServerRotation() {
        return this.serverRotation;
    }
}
