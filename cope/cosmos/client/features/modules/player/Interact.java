package cope.cosmos.client.features.modules.player;

import cope.cosmos.client.events.EntityHitboxSizeEvent;
import cope.cosmos.client.events.LiquidInteractEvent;
import cope.cosmos.client.events.PacketEvent;
import cope.cosmos.client.events.ReachEvent;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.util.player.PlayerUtil;
import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Interact extends Module {

    public static Interact INSTANCE;
    public static Setting reach = new Setting("Reach", "Player reach extension", Double.valueOf(0.0D), Double.valueOf(0.0D), Double.valueOf(3.0D), 2);
    public static Setting hand = new Setting("Hand", "Swinging hand", PlayerUtil.Hand.NONE);
    public static Setting ghostHand = new Setting("GhostHand", "Allows you to interact with blocks through walls", Boolean.valueOf(false));
    public static Setting hitBox = new Setting("HitBox", "Ignores entity hitboxes", Boolean.valueOf(true));
    public static Setting hitBoxExtend = (new Setting("Extend", "Entity hitbox extension", Double.valueOf(0.0D), Double.valueOf(0.0D), Double.valueOf(2.0D), 2)).setParent(Interact.hitBox);
    public static Setting hitBoxPlayers = (new Setting("PlayersOnly", "Only ignores player hitboxes", Boolean.valueOf(true))).setParent(Interact.hitBox);
    public static Setting liquid = new Setting("Liquid", "Allows you to place blocks on liquid", Boolean.valueOf(false));
    public static Setting heightLimit = new Setting("HeightLimit", "Allows you to interact with blocks at height limit", Boolean.valueOf(true));

    public Interact() {
        super("Interact", Category.PLAYER, "Interaction changes & various exploits");
        Interact.INSTANCE = this;
    }

    public void onUpdate() {
        if (!((PlayerUtil.Hand) Interact.hand.getValue()).equals(PlayerUtil.Hand.NONE)) {
            switch ((PlayerUtil.Hand) Interact.hand.getValue()) {
            case MAINHAND:
                Interact.mc.player.swingingHand = EnumHand.MAIN_HAND;
                break;

            case OFFHAND:
                Interact.mc.player.swingingHand = EnumHand.OFF_HAND;
            }
        }

        if (((Boolean) Interact.ghostHand.getValue()).booleanValue()) {
            (new ArrayList(Interact.mc.world.loadedTileEntityList)).forEach(accept<invokedynamic>());
        }

        if (((Boolean) Interact.hitBox.getValue()).booleanValue() && Interact.mc.objectMouseOver != null && Interact.mc.objectMouseOver.typeOfHit.equals(Type.ENTITY)) {
            if (((Boolean) Interact.hitBoxPlayers.getValue()).booleanValue() && !(Interact.mc.objectMouseOver.entityHit instanceof EntityPlayer)) {
                return;
            }

            RayTraceResult hitboxResult = Interact.mc.player.rayTrace(6.0D, Interact.mc.getRenderPartialTicks());

            if (hitboxResult != null && hitboxResult.typeOfHit == Type.BLOCK) {
                BlockPos hitboxPos = hitboxResult.getBlockPos();

                if (Interact.mc.gameSettings.keyBindAttack.isKeyDown()) {
                    Interact.mc.playerController.onPlayerDamageBlock(hitboxPos, EnumFacing.UP);
                    PlayerUtil.swingArm(PlayerUtil.Hand.MAINHAND);
                }
            }
        }

    }

    @SubscribeEvent
    public void onHitboxSize(EntityHitboxSizeEvent event) {
        if (this.nullCheck() && ((Boolean) Interact.hitBox.getValue()).booleanValue()) {
            event.setHitboxSize((float) ((Double) Interact.hitBoxExtend.getValue()).doubleValue());
        }

    }

    @SubscribeEvent
    public void onReach(ReachEvent event) {
        if (this.nullCheck() && ((Boolean) Interact.hitBox.getValue()).booleanValue()) {
            event.setReach((Interact.mc.player.capabilities.isCreativeMode ? 5.0F : 4.5F) + (float) ((Double) Interact.reach.getValue()).doubleValue());
        }

    }

    @SubscribeEvent
    public void onLiquidInteract(LiquidInteractEvent event) {
        event.setCanceled(this.nullCheck() && (((Boolean) Interact.liquid.getValue()).booleanValue() || event.getLiquidLevel() && ((Integer) event.getBlockState().getValue(BlockLiquid.LEVEL)).intValue() == 0));
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (this.nullCheck() && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && ((Boolean) Interact.heightLimit.getValue()).booleanValue() && ((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getPos().getY() == 255 && ((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getDirection().equals(EnumFacing.UP)) {
            Interact.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getPos(), EnumFacing.DOWN, ((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getHand(), ((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getFacingX(), ((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getFacingY(), ((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getFacingZ()));
            event.setCanceled(true);
        }

    }

    private static void lambda$onUpdate$0(TileEntity tileEntity) {
        if (!(new BlockPos(((RayTraceResult) Objects.requireNonNull(Interact.mc.player.rayTrace((double) Interact.mc.playerController.getBlockReachDistance(), Interact.mc.getRenderPartialTicks()))).getBlockPos())).equals(tileEntity.getPos())) {
            RayTraceResult rayTraceResult = Interact.mc.player.rayTrace((double) Interact.mc.playerController.getBlockReachDistance(), Interact.mc.getRenderPartialTicks());

            if (rayTraceResult != null && rayTraceResult.typeOfHit.equals(Type.BLOCK) && rayTraceResult.getBlockPos().getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()) <= 5.0D && Interact.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                Interact.mc.playerController.processRightClickBlock(Interact.mc.player, Interact.mc.world, tileEntity.getPos(), EnumFacing.UP, new Vec3d(0.0D, 0.0D, 0.0D), EnumHand.MAIN_HAND);
            }

        }
    }
}
