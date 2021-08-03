package cope.cosmos.client.features.modules.combat;

import cope.cosmos.client.Cosmos;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.client.manager.managers.NotificationManager;
import cope.cosmos.util.player.InventoryUtil;
import cope.cosmos.util.player.PlayerUtil;
import cope.cosmos.util.world.BlockUtil;
import cope.cosmos.util.world.TeleportUtil;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.math.BlockPos;

public class Burrow extends Module {

    public static Burrow INSTANCE;
    public static Setting mode = new Setting("Mode", "Block to prefer", Burrow.Mode.OBSIDIAN);
    public static Setting swing = new Setting("Swing", "Hand to swing when placing", PlayerUtil.Hand.MAINHAND);
    public static Setting offset = new Setting("Offset", "How high to rubberband", Double.valueOf(-10.0D), Double.valueOf(2.2D), Double.valueOf(10.0D), 1);
    public static Setting packet = new Setting("Packet", "Place with packets", Boolean.valueOf(true));

    public Burrow() {
        super("Burrow", Category.COMBAT, "Instantly burrow into a block.");
        Burrow.INSTANCE = this;
    }

    public void onEnable() {
        super.onEnable();
        Cosmos.INSTANCE.getTickManager().setClientTicks(10.0D);
        BlockPos originalPos = new BlockPos(Burrow.mc.player.getPositionVector());

        Burrow.mc.player.connection.sendPacket(new CPacketPlayer(ThreadLocalRandom.current().nextBoolean()));
        int block = -1;

        switch ((Burrow.Mode) Burrow.mode.getValue()) {
        case OBSIDIAN:
            block = InventoryUtil.getBlockSlot(Blocks.OBSIDIAN, InventoryUtil.Inventory.INVENTORY, false);
            break;

        case E_CHEST:
            block = InventoryUtil.getBlockSlot(Blocks.ENDER_CHEST, InventoryUtil.Inventory.INVENTORY, false);
            break;

        case ANVIL:
            block = InventoryUtil.getBlockSlot(Blocks.ANVIL, InventoryUtil.Inventory.INVENTORY, false);
            break;

        case CHEST:
            block = InventoryUtil.getBlockSlot(Blocks.CHEST, InventoryUtil.Inventory.INVENTORY, false);
        }

        if (Burrow.mc.world.getBlockState(originalPos).getMaterial().isReplaceable() && Burrow.mc.world.isAirBlock(Burrow.mc.player.getPosition().add(0, 3, 0)) && block != -1) {
            Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + 0.41999998688698D, Burrow.mc.player.posZ, true));
            Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + 0.7531999805211997D, Burrow.mc.player.posZ, true));
            Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + 1.00133597911214D, Burrow.mc.player.posZ, true));
            Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + 1.16610926093821D, Burrow.mc.player.posZ, true));
            TeleportUtil.teleportPlayerNoPacket(Burrow.mc.player.posX, Burrow.mc.player.posY + 1.16610926093821D, Burrow.mc.player.posZ);
            int oldSlot = Burrow.mc.player.inventory.currentItem;

            Burrow.mc.player.inventory.currentItem = block;
            Burrow.mc.player.connection.sendPacket(new CPacketHeldItemChange(block));
            BlockUtil.placeBlock(originalPos, ((Boolean) Burrow.packet.getValue()).booleanValue(), false);
            PlayerUtil.swingArm((PlayerUtil.Hand) Burrow.swing.getValue());
            TeleportUtil.teleportPlayerNoPacket(Burrow.mc.player.posX, Burrow.mc.player.posY - 1.16610926093821D, Burrow.mc.player.posZ);
            Burrow.mc.player.inventory.currentItem = oldSlot;
            Burrow.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
            Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + ((Double) Burrow.offset.getValue()).doubleValue(), Burrow.mc.player.posZ, false));
            Cosmos.INSTANCE.getTickManager().setClientTicks(2500.0D);
            this.disable();
        } else {
            Cosmos.INSTANCE.getNotificationManager().pushNotification(new NotificationManager.Notification("Unable to burrow!", NotificationManager.Type.WARNING));
            Cosmos.INSTANCE.getTickManager().setClientTicks(2500.0D);
            this.disable();
        }
    }

    private static enum Mode {

        OBSIDIAN, E_CHEST, ANVIL, CHEST;
    }
}
