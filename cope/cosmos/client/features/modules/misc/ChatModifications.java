package cope.cosmos.client.features.modules.misc;

import cope.cosmos.client.events.ModuleToggleEvent;
import cope.cosmos.client.events.PacketEvent;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.loader.asm.mixins.accessor.ICPacketChatMessage;
import cope.cosmos.loader.asm.mixins.accessor.ITextComponentString;
import cope.cosmos.util.client.ChatUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatModifications extends Module {

    public static ChatModifications INSTANCE;
    public static Setting time = new Setting("Time", "Time format", ChatModifications.Time.NA);
    public static Setting prefix = new Setting("Prefix", "Add a cosmos prefix before chat messages", Boolean.valueOf(false));
    public static Setting suffix = new Setting("Suffix", "Add a cosmos suffix after chat messages", Boolean.valueOf(true));
    public static Setting colored = new Setting("Colored", "Add a > before public messages", Boolean.valueOf(true));
    public static Setting enableNotify = new Setting("EnableNotify", "Send a chat message when a modules is toggled", Boolean.valueOf(false));

    public ChatModifications() {
        super("ChatModifications", Category.MISC, "Allows you to modify the in-game chat window");
        ChatModifications.INSTANCE = this;
    }

    @SubscribeEvent
    public void onModuleEnable(ModuleToggleEvent.ModuleEnableEvent event) {
        if (((Boolean) ChatModifications.enableNotify.getValue()).booleanValue() && event.getModule().getCategory() != Category.HIDDEN) {
            ChatUtil.sendModuleEnableMessage(event.getModule());
        }

    }

    @SubscribeEvent
    public void onModuleDisable(ModuleToggleEvent.ModuleDisableEvent event) {
        if (((Boolean) ChatModifications.enableNotify.getValue()).booleanValue() && event.getModule().getCategory() != Category.HIDDEN) {
            ChatUtil.sendModuleDisableMessage(event.getModule());
        }

    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketChatMessage) {
            if (((CPacketChatMessage) event.getPacket()).getMessage().startsWith("/") || ((CPacketChatMessage) event.getPacket()).getMessage().startsWith("!") || ((CPacketChatMessage) event.getPacket()).getMessage().startsWith("$") || ((CPacketChatMessage) event.getPacket()).getMessage().startsWith("?") || ((CPacketChatMessage) event.getPacket()).getMessage().startsWith(".") || ((CPacketChatMessage) event.getPacket()).getMessage().startsWith(",")) {
                return;
            }

            ((ICPacketChatMessage) event.getPacket()).setMessage((((Boolean) ChatModifications.colored.getValue()).booleanValue() ? "> " : "") + ((CPacketChatMessage) event.getPacket()).getMessage() + (((Boolean) ChatModifications.suffix.getValue()).booleanValue() ? " â?? " + ChatUtil.toUnicode("Cosmos") : ""));
        }

    }

    @SubscribeEvent
    public void onPacketRecieve(PacketEvent.PacketReceiveEvent event) {
        if (this.nullCheck() && event.getPacket() instanceof SPacketChat && ((SPacketChat) event.getPacket()).getChatComponent() instanceof TextComponentString && !((SPacketChat) event.getPacket()).getType().equals(ChatType.GAME_INFO)) {
            TextComponentString component = (TextComponentString) ((SPacketChat) event.getPacket()).getChatComponent();
            String formattedTime = "";

            switch ((ChatModifications.Time) ChatModifications.time.getValue()) {
            case NA:
                formattedTime = (new SimpleDateFormat("h:mm a")).format(new Date());
                break;

            case EU:
                formattedTime = (new SimpleDateFormat("k:mm")).format(new Date());
            }

            if (component.getText() != null) {
                String formattedText = (!((ChatModifications.Time) ChatModifications.time.getValue()).equals(ChatModifications.Time.NONE) ? TextFormatting.GRAY + "[" + formattedTime + "] " + TextFormatting.RESET : "") + (((Boolean) ChatModifications.prefix.getValue()).booleanValue() ? ChatUtil.getPrefix() : "") + component.getText();

                ((ITextComponentString) component).setText(formattedText);
            }
        }

    }

    public static enum Time {

        NA, EU, NONE;
    }
}
