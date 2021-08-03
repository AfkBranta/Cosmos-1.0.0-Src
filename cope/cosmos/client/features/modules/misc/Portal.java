package cope.cosmos.client.features.modules.misc;

import cope.cosmos.client.events.PacketEvent;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.loader.asm.mixins.accessor.IEntity;
import net.minecraft.client.audio.ISound;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Portal extends Module {

    public static Portal INSTANCE;
    public static Setting godMode = new Setting("GodMode", "Cancels teleport packets", Boolean.valueOf(false));
    public static Setting screens = new Setting("Screens", "Allow the use of screens in portals", Boolean.valueOf(true));
    public static Setting effect = new Setting("Effect", "Cancels the portal overlay effect", Boolean.valueOf(false));
    public static Setting sounds = new Setting("Sounds", "Cancels portal sounds", Boolean.valueOf(false));

    public Portal() {
        super("Portal", Category.MISC, "Modifies portal behavior");
        Portal.INSTANCE = this;
    }

    public void onUpdate() {
        ((IEntity) Portal.mc.player).setInPortal(!((Boolean) Portal.screens.getValue()).booleanValue() && ((IEntity) Portal.mc.player).getInPortal());
        GuiIngameForge.renderPortal = !((Boolean) Portal.effect.getValue()).booleanValue();
    }

    public void onDisable() {
        super.onDisable();
        GuiIngameForge.renderPortal = true;
    }

    @SubscribeEvent
    public void onSound(PlaySoundEvent event) {
        if (((Boolean) Portal.sounds.getValue()).booleanValue() && (event.getName().equals("block.portal.ambient") || event.getName().equals("block.portal.travel") || event.getName().equals("block.portal.trigger"))) {
            event.setResultSound((ISound) null);
        }

    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        event.setCanceled(this.nullCheck() && event.getPacket() instanceof CPacketConfirmTeleport && Portal.mc.player.timeInPortal > 0.0F && ((Boolean) Portal.godMode.getValue()).booleanValue());
    }
}
