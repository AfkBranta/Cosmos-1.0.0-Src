package cope.cosmos.loader.asm.mixins;

import cope.cosmos.client.events.ModifyFOVEvent;
import cope.cosmos.client.events.SkinLocationEvent;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ AbstractClientPlayer.class})
public class MixinAbstractClientPlayer {

    @Inject(
        method = { "getLocationSkin()Lnet/minecraft/util/ResourceLocation;"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    public void getLocationSkin(CallbackInfoReturnable info) {
        SkinLocationEvent skinLocationEvent = new SkinLocationEvent();

        MinecraftForge.EVENT_BUS.post(skinLocationEvent);
    }

    @Inject(
        method = { "getFovModifier"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    public void getFOVModifier(CallbackInfoReturnable info) {
        ModifyFOVEvent modifyFOVEvent = new ModifyFOVEvent();

        MinecraftForge.EVENT_BUS.post(modifyFOVEvent);
        if (modifyFOVEvent.isCanceled()) {
            info.cancel();
            info.setReturnValue(Float.valueOf(1.0F));
        }

    }
}
