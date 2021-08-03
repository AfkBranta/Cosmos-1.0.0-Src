package cope.cosmos.util.world;

import com.mojang.authlib.GameProfile;
import cope.cosmos.util.Wrapper;
import net.minecraft.client.entity.EntityOtherPlayerMP;

public class WorldUtil implements Wrapper {

    public static void createFakePlayer(GameProfile gameProfile, int fakeID, boolean inventory, boolean health) {
        EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP(WorldUtil.mc.world, gameProfile);

        fakePlayer.copyLocationAndAnglesFrom(WorldUtil.mc.player);
        fakePlayer.rotationYawHead = WorldUtil.mc.player.rotationYaw;
        if (inventory) {
            fakePlayer.inventory.copyInventory(WorldUtil.mc.player.inventory);
        }

        if (health) {
            fakePlayer.setHealth(WorldUtil.mc.player.getHealth());
            fakePlayer.setAbsorptionAmount(WorldUtil.mc.player.getAbsorptionAmount());
        }

        WorldUtil.mc.world.addEntityToWorld(fakeID, fakePlayer);
    }
}
