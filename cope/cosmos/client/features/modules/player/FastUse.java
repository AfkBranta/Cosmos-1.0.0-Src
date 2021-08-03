package cope.cosmos.client.features.modules.player;

import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.loader.asm.mixins.accessor.IMinecraft;
import cope.cosmos.util.player.InventoryUtil;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastUse extends Module {

    public static FastUse INSTANCE;
    public static Setting speed = new Setting("Speed", "Place speed", Double.valueOf(0.0D), Double.valueOf(0.0D), Double.valueOf(4.0D), 0);
    public static Setting ghostFix = new Setting("GhostFix", "Fixes the item ghost issue on some servers", Boolean.valueOf(false));
    public static Setting packetUse = new Setting("PacketUse", "Uses packets when using items", Boolean.valueOf(false));
    public static Setting packetGapple = (new Setting("Gapple", "Uses packets when eating gapples", Boolean.valueOf(false))).setParent(FastUse.packetUse);
    public static Setting packetPotion = (new Setting("Potions", "Uses packets when drinking potions", Boolean.valueOf(true))).setParent(FastUse.packetUse);
    public static Setting exp = new Setting("EXP", "Applies fast placements to experience", Boolean.valueOf(true));
    public static Setting bow = new Setting("Bow", "Applies fast placements to bows", Boolean.valueOf(false));
    public static Setting crystals = new Setting("Crystals", "Applies fast placements to crystals", Boolean.valueOf(false));
    public static Setting blocks = new Setting("Blocks", "Applies fast placements to blocks", Boolean.valueOf(false));
    public static Setting spawnEggs = new Setting("SpawnEggs", "Applies fast placements to spawn eggs", Boolean.valueOf(false));
    public static Setting fireworks = new Setting("Fireworks", "Applies fast placements to fireworks", Boolean.valueOf(false));

    public FastUse() {
        super("FastUse", Category.PLAYER, "Allows you to place items and blocks faster");
        FastUse.INSTANCE = this;
    }

    public void onUpdate() {
        if (InventoryUtil.isHolding(Items.EXPERIENCE_BOTTLE) && ((Boolean) FastUse.exp.getValue()).booleanValue() || InventoryUtil.isHolding(Items.END_CRYSTAL) && ((Boolean) FastUse.crystals.getValue()).booleanValue() || InventoryUtil.isHolding(Items.SPAWN_EGG) && ((Boolean) FastUse.spawnEggs.getValue()).booleanValue() || InventoryUtil.isHolding(Items.FIREWORKS) && ((Boolean) FastUse.fireworks.getValue()).booleanValue() || InventoryUtil.isHolding(Item.getItemFromBlock(Blocks.OBSIDIAN)) && ((Boolean) FastUse.blocks.getValue()).booleanValue()) {
            if (((Boolean) FastUse.ghostFix.getValue()).booleanValue() && FastUse.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                FastUse.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            } else {
                ((IMinecraft) FastUse.mc).setRightClickDelayTimer((int) ((Double) FastUse.speed.getValue()).doubleValue());
            }
        }

        if (((Boolean) FastUse.packetUse.getValue()).booleanValue() && FastUse.mc.player.isHandActive()) {
            if (((Boolean) FastUse.packetGapple.getValue()).booleanValue() && !InventoryUtil.isHolding(Items.GOLDEN_APPLE)) {
                return;
            }

            if (((Boolean) FastUse.packetPotion.getValue()).booleanValue() && !InventoryUtil.isHolding(Items.POTIONITEM)) {
                return;
            }

            for (int i = 0; (double) i < ((Double) FastUse.speed.getValue()).doubleValue() * 8.0D; ++i) {
                FastUse.mc.player.connection.sendPacket(new CPacketPlayer());
            }
        }

        if (InventoryUtil.isHolding(Items.BOW) && ((Boolean) FastUse.bow.getValue()).booleanValue() && FastUse.mc.player.isHandActive() && (double) FastUse.mc.player.getItemInUseMaxCount() >= 3.0D - ((Double) FastUse.speed.getValue()).doubleValue()) {
            FastUse.mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, FastUse.mc.player.getHorizontalFacing()));
            FastUse.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(FastUse.mc.player.getActiveHand()));
            FastUse.mc.player.stopActiveHand();
        }

    }

    @SubscribeEvent
    public void onPlayerRightClick(RightClickItem rightClickItemEvent) {
        if (((Boolean) FastUse.packetUse.getValue()).booleanValue()) {
            if (((Boolean) FastUse.packetGapple.getValue()).booleanValue() && !rightClickItemEvent.getItemStack().getItem().equals(Items.GOLDEN_APPLE)) {
                return;
            }

            if (((Boolean) FastUse.packetPotion.getValue()).booleanValue() && !rightClickItemEvent.getItemStack().getItem().equals(Items.POTIONITEM)) {
                return;
            }

            rightClickItemEvent.setCanceled(true);
            rightClickItemEvent.getItemStack().getItem().onItemUseFinish(rightClickItemEvent.getItemStack(), rightClickItemEvent.getWorld(), rightClickItemEvent.getEntityPlayer());
        }

    }
}
