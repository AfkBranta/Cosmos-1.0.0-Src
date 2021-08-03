package cope.cosmos.client.manager.managers;

import com.mojang.realmsclient.gui.ChatFormatting;
import cope.cosmos.client.Cosmos;
import cope.cosmos.client.events.PacketEvent;
import cope.cosmos.client.events.TotemPopEvent;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.manager.Manager;
import cope.cosmos.util.Wrapper;
import cope.cosmos.util.client.ChatUtil;
import java.util.function.Consumer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;

public class EventManager extends Manager implements Wrapper {

    public static final EventManager INSTANCE = new EventManager();

    public EventManager() {
        super("EventManager", "Manages Forge events", 3);
    }

    @SubscribeEvent
    public void onUpdate(LivingUpdateEvent event) {
        ModuleManager.getAllModules().forEach((mod) -> {
            if (event.getEntity().getEntityWorld().isRemote && event.getEntityLiving().equals(EventManager.mc.player) && (this.nullCheck() || Cosmos.INSTANCE.getNullSafeMods().contains(mod)) && mod.isEnabled()) {
                try {
                    mod.onUpdate();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

        });
    }

    @SubscribeEvent
    public void onRender2d(Text event) {
        ModuleManager.getAllModules().forEach((mod) -> {
            if (this.nullCheck() && mod.isEnabled()) {
                try {
                    mod.onRender2d();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

        });
    }

    @SubscribeEvent
    public void onRender3d(RenderWorldLastEvent event) {
        EventManager.mc.profiler.startSection("cosmos-render");
        ModuleManager.getAllModules().forEach((mod) -> {
            if (this.nullCheck() && mod.isEnabled()) {
                try {
                    mod.onRender3d();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

        });
        EventManager.mc.profiler.endSection();
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        ModuleManager.getAllModules().forEach((mod) -> {
            if (Keyboard.isKeyDown(mod.getKey()) && !Keyboard.isKeyDown(0)) {
                mod.toggle();
            }

        });
    }

    @SubscribeEvent
    public void onChatInput(ClientChatEvent event) {
        if (event.getMessage().startsWith(Cosmos.PREFIX)) {
            event.setCanceled(true);

            try {
                Cosmos.INSTANCE.getCommandDispatcher().execute(Cosmos.INSTANCE.getCommandDispatcher().parse(event.getOriginalMessage().substring(1), Integer.valueOf(1)));
            } catch (Exception exception) {
                exception.printStackTrace();
                ChatUtil.sendHoverableMessage(ChatFormatting.RED + "An error occured!", "No such command was found");
            }
        }

    }

    @SubscribeEvent
    public void onTotemPop(PacketEvent.PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketEntityStatus && ((SPacketEntityStatus) event.getPacket()).getOpCode() == 35) {
            TotemPopEvent totemPopEvent = new TotemPopEvent(((SPacketEntityStatus) event.getPacket()).getEntity(EventManager.mc.world));

            MinecraftForge.EVENT_BUS.post(totemPopEvent);
            if (totemPopEvent.isCanceled()) {
                event.setCanceled(true);
            }
        }

    }
}
