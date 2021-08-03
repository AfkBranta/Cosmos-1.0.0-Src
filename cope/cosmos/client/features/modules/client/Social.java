package cope.cosmos.client.features.modules.client;

import cope.cosmos.client.Cosmos;
import cope.cosmos.client.events.TabOverlayEvent;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.client.manager.managers.SocialManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Social extends Module {

    public static Social INSTANCE;
    public static Setting friends = new Setting("Friends", "Allow friends system to function", Boolean.valueOf(true));

    public Social() {
        super("Social", Category.CLIENT, "Allows the social system to function");
        Social.INSTANCE = this;
        this.setExempt(true);
        this.setDrawn(false);
    }

    @SubscribeEvent
    public void onTabOverlay(TabOverlayEvent event) {
        if (this.nullCheck() && Cosmos.INSTANCE.getSocialManager().getSocial(event.getInformation()).equals(SocialManager.Relationship.FRIEND) && ((Boolean) Social.friends.getValue()).booleanValue()) {
            event.setCanceled(true);
            event.setInformation(TextFormatting.AQUA + event.getInformation());
        }

    }
}
