package cope.cosmos.util.world;

import cope.cosmos.util.Wrapper;
import javax.annotation.Nullable;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.client.audio.Sound.Type;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

public class SoundUtil implements Wrapper {

    public static void clickSound() {
        SoundUtil.mc.getSoundHandler().playSound(new ISound() {
            public ResourceLocation getSoundLocation() {
                return new ResourceLocation("cosmos", "sounds/click.ogg");
            }

            @Nullable
            public SoundEventAccessor createAccessor(SoundHandler handler) {
                return new SoundEventAccessor(new ResourceLocation("cosmos", "sounds/click.ogg"), "click");
            }

            public Sound getSound() {
                return new Sound("click", 1.0F, 1.0F, 1, Type.SOUND_EVENT, false);
            }

            public SoundCategory getCategory() {
                return SoundCategory.VOICE;
            }

            public boolean canRepeat() {
                return false;
            }

            public int getRepeatDelay() {
                return 0;
            }

            public float getVolume() {
                return 1.0F;
            }

            public float getPitch() {
                return 1.0F;
            }

            public float getXPosF() {
                return Wrapper.mc.player != null ? (float) Wrapper.mc.player.posX : 0.0F;
            }

            public float getYPosF() {
                return Wrapper.mc.player != null ? (float) Wrapper.mc.player.posY : 0.0F;
            }

            public float getZPosF() {
                return Wrapper.mc.player != null ? (float) Wrapper.mc.player.posZ : 0.0F;
            }

            public AttenuationType getAttenuationType() {
                return AttenuationType.LINEAR;
            }
        });
    }
}
