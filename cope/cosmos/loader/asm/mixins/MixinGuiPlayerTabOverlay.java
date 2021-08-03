package cope.cosmos.loader.asm.mixins;

import cope.cosmos.client.events.TabOverlayEvent;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ GuiPlayerTabOverlay.class})
public class MixinGuiPlayerTabOverlay {

    @Inject(
        method = { "getPlayerName"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    public void getPlayerName(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable info) {
        TabOverlayEvent tabOverlayEvent = new TabOverlayEvent(networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName()));

        MinecraftForge.EVENT_BUS.post(tabOverlayEvent);
        if (tabOverlayEvent.isCanceled()) {
            info.cancel();
            info.setReturnValue(tabOverlayEvent.getInformation());
        }

    }
}
