package cope.cosmos.util.client;

import cope.cosmos.util.Wrapper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;

public class ChatBuilder implements Wrapper {

    ITextComponent textComponent = new TextComponentString("");

    public ChatBuilder append(String message, Style style) {
        this.textComponent.appendSibling((new TextComponentString(message)).setStyle(style));
        return this;
    }

    public void push() {
        ChatBuilder.mc.player.sendMessage(this.textComponent);
    }

    public ITextComponent component() {
        return this.textComponent;
    }
}
