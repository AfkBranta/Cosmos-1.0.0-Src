package cope.cosmos.util.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.manager.managers.ModuleManager;
import cope.cosmos.util.Wrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;

public class ChatUtil implements Wrapper {

    private static Map messageMap = new HashMap();

    public static String getPrefix() {
        return ChatFormatting.BLUE + "<" + ChatFormatting.DARK_PURPLE + "Cosmos" + ChatFormatting.BLUE + "> " + ChatFormatting.GRAY;
    }

    public static void sendMessage(String message) {
        ChatUtil.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(getPrefix() + message));
    }

    public static void sendRawMessage(String message) {
        ChatUtil.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(message));
    }

    public static void sendHoverableMessage(String message, String hoverable) {
        (new ChatBuilder()).append(getPrefix() + message, (new Style()).setColor(TextFormatting.DARK_PURPLE).setHoverEvent(new HoverEvent(Action.SHOW_TEXT, (new ChatBuilder()).append("Cosmos", (new Style()).setColor(TextFormatting.DARK_PURPLE)).append("\n" + hoverable, (new Style()).setColor(TextFormatting.BLUE)).component()))).append(" ", (new Style()).setColor(TextFormatting.DARK_PURPLE)).push();
    }

    public static void sendModuleEnableMessage(Module m) {
        ChatUtil.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(getPrefix() + m.getName() + ChatFormatting.GREEN + " enabled."), ((Integer) ChatUtil.messageMap.get(m)).intValue());
    }

    public static void sendModuleDisableMessage(Module m) {
        ChatUtil.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(getPrefix() + m.getName() + ChatFormatting.RED + " disabled."), ((Integer) ChatUtil.messageMap.get(m)).intValue());
    }

    public static String toUnicode(String message) {
        return message.toLowerCase().replace("a", "ᴀ").replace("b", "ʙ").replace("c", "ᴄ").replace("d", "ᴅ").replace("e", "ᴇ").replace("f", "ꜰ").replace("g", "ɢ").replace("h", "ʜ").replace("i", "ɪ").replace("j", "ᴊ").replace("k", "ᴋ").replace("l", "ʟ").replace("m", "�?").replace("n", "ɴ").replace("o", "�?").replace("p", "ᴘ").replace("q", "ǫ").replace("r", "ʀ").replace("s", "ꜱ").replace("t", "ᴛ").replace("u", "ᴜ").replace("v", "ᴠ").replace("w", "ᴡ").replace("x", "ˣ").replace("y", "�?").replace("z", "ᴢ");
    }

    static {
        ModuleManager.getAllModules().forEach((mod) -> {
            Integer integer = (Integer) ChatUtil.messageMap.put(mod, Integer.valueOf(ThreadLocalRandom.current().nextInt(32767)));
        });
    }
}
