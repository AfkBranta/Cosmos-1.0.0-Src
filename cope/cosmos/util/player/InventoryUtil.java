package cope.cosmos.util.player;

import cope.cosmos.loader.asm.mixins.accessor.IPlayerControllerMP;
import cope.cosmos.util.Wrapper;
import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class InventoryUtil implements Wrapper {

    private static boolean switching = false;

    public static void switchToSlot(int slot, InventoryUtil.Switch switchMode) {
        if (slot != -1 && InventoryUtil.mc.player.inventory.currentItem != slot) {
            switch (switchMode) {
            case NORMAL:
                InventoryUtil.mc.player.inventory.currentItem = slot;
                break;

            case PACKET:
                InventoryUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            }
        }

        InventoryUtil.mc.playerController.updateController();
        ((IPlayerControllerMP) InventoryUtil.mc.playerController).syncCurrentPlayItem();
    }

    public static void switchToSlot(Item item, InventoryUtil.Switch switchMode) {
        if (getItemSlot(item, InventoryUtil.Inventory.HOTBAR, true) != -1 && InventoryUtil.mc.player.inventory.currentItem != getItemSlot(item, InventoryUtil.Inventory.HOTBAR, true)) {
            switchToSlot(getItemSlot(item, InventoryUtil.Inventory.HOTBAR, true), switchMode);
        }

        ((IPlayerControllerMP) InventoryUtil.mc.playerController).syncCurrentPlayItem();
    }

    public static void moveItemToOffhand(Item item, boolean hotbar) {
        moveItemToOffhand(getItemSlot(item, InventoryUtil.Inventory.INVENTORY, hotbar));
    }

    public static void moveItemToOffhand(int slot) {
        int returnSlot = -1;

        if (slot != -1) {
            InventoryUtil.switching = true;
            InventoryUtil.mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, InventoryUtil.mc.player);
            InventoryUtil.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, InventoryUtil.mc.player);

            for (int i = 9; i < 45; ++i) {
                if (InventoryUtil.mc.player.inventory.getStackInSlot(i).isEmpty()) {
                    returnSlot = i;
                    break;
                }
            }

            if (returnSlot != -1) {
                InventoryUtil.mc.playerController.windowClick(0, returnSlot, 0, ClickType.PICKUP, InventoryUtil.mc.player);
            }

            InventoryUtil.switching = false;
        }
    }

    public static int getItemSlot(Item item, InventoryUtil.Inventory inventory, boolean hotbar) {
        int i;

        switch (inventory) {
        case HOTBAR:
            for (i = 0; i < 9; ++i) {
                if (InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem() == item) {
                    return i;
                }
            }

            return -1;

        case INVENTORY:
            for (i = hotbar ? 9 : 0; i < 45; ++i) {
                if (InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem() == item) {
                    return i;
                }
            }
        }

        return -1;
    }

    public static int getBlockSlot(Block block, InventoryUtil.Inventory inventory, boolean hotbar) {
        int i;
        Item item;

        switch (inventory) {
        case HOTBAR:
            for (i = hotbar ? 9 : 0; i < 45; ++i) {
                item = InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem();
                if (item instanceof ItemBlock && ((ItemBlock) item).getBlock().equals(block)) {
                    return i;
                }
            }

            return -1;

        case INVENTORY:
            for (i = 0; i < 9; ++i) {
                item = InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem();
                if (item instanceof ItemBlock && ((ItemBlock) item).getBlock().equals(block)) {
                    return i;
                }
            }
        }

        return -1;
    }

    public static int getItemCount(Item item) {
        return (new ArrayList(InventoryUtil.mc.player.inventory.mainInventory)).stream().filter(test<invokedynamic>(item)).mapToInt(applyAsInt<invokedynamic>()).sum() + (new ArrayList(InventoryUtil.mc.player.inventory.offHandInventory)).stream().filter(test<invokedynamic>(item)).mapToInt(applyAsInt<invokedynamic>()).sum() + (new ArrayList(InventoryUtil.mc.player.inventory.armorInventory)).stream().filter(test<invokedynamic>(item)).mapToInt(applyAsInt<invokedynamic>()).sum();
    }

    public static int getBlockCount(Block block) {
        return (new ArrayList(InventoryUtil.mc.player.inventory.mainInventory)).stream().filter(test<invokedynamic>(block)).mapToInt(applyAsInt<invokedynamic>()).sum() + (new ArrayList(InventoryUtil.mc.player.inventory.offHandInventory)).stream().filter(test<invokedynamic>(block)).mapToInt(applyAsInt<invokedynamic>()).sum();
    }

    public static boolean isHolding32k() {
        for (int i = 0; i < InventoryUtil.mc.player.getHeldItemMainhand().getEnchantmentTagList().tagCount(); ++i) {
            InventoryUtil.mc.player.getHeldItemMainhand().getEnchantmentTagList().getCompoundTagAt(i);
            if (Enchantment.getEnchantmentByID(InventoryUtil.mc.player.getHeldItemMainhand().getEnchantmentTagList().getCompoundTagAt(i).getByte("id")) != null && Enchantment.getEnchantmentByID(InventoryUtil.mc.player.getHeldItemMainhand().getEnchantmentTagList().getCompoundTagAt(i).getShort("id")) != null && !((Enchantment) Objects.requireNonNull(Enchantment.getEnchantmentByID(InventoryUtil.mc.player.getHeldItemMainhand().getEnchantmentTagList().getCompoundTagAt(i).getShort("id")))).isCurse() && InventoryUtil.mc.player.getHeldItemMainhand().getEnchantmentTagList().getCompoundTagAt(i).getShort("lvl") >= 1000) {
                return true;
            }
        }

        return false;
    }

    public static boolean isHolding(Item item) {
        return InventoryUtil.mc.player.getHeldItemMainhand().getItem().equals(item) || InventoryUtil.mc.player.getHeldItemOffhand().getItem().equals(item);
    }

    public static boolean isSwitching() {
        return InventoryUtil.switching;
    }

    private static boolean lambda$getBlockCount$4(Block block, ItemStack itemStack) {
        return itemStack.getItem().equals(Item.getItemFromBlock(block));
    }

    private static boolean lambda$getBlockCount$3(Block block, ItemStack itemStack) {
        return itemStack.getItem().equals(Item.getItemFromBlock(block));
    }

    private static boolean lambda$getItemCount$2(Item item, ItemStack itemStack) {
        return itemStack.getItem().equals(item);
    }

    private static boolean lambda$getItemCount$1(Item item, ItemStack itemStack) {
        return itemStack.getItem().equals(item);
    }

    private static boolean lambda$getItemCount$0(Item item, ItemStack itemStack) {
        return itemStack.getItem().equals(item);
    }

    public static enum Inventory {

        INVENTORY, HOTBAR, CRAFTING;
    }

    public static enum Switch {

        NORMAL, PACKET, NONE;
    }
}
