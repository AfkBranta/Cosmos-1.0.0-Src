package cope.cosmos.client.features.modules.combat;

import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.modules.movement.ReverseStep;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.util.player.InventoryUtil;
import cope.cosmos.util.player.PlayerUtil;
import cope.cosmos.util.system.Timer;
import cope.cosmos.util.world.HoleUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import org.lwjgl.input.Mouse;

public class Offhand extends Module {

    public static Offhand INSTANCE;
    public static Setting offHandItem = new Setting("Item", "Item to use when not at critical health", Offhand.OffhandItem.CRYSTAL);
    public static Setting fallBack = new Setting("FallBack", "Item to use if you don\'t have the chosen item", Offhand.OffhandItem.GAPPLE);
    public static Setting hole = new Setting("Hole", "Item to use when in hole and at critical health", Offhand.OffhandItem.CRYSTAL);
    public static Setting holeHealth = (new Setting("Health", "Health that is considered critical hole health", Double.valueOf(0.0D), Double.valueOf(6.0D), Double.valueOf(36.0D), 0)).setParent(Offhand.hole);
    public static Setting sync = new Setting("Sync", "Syncs the offhand switch to client processes", Offhand.Sync.NONE);
    public static Setting delay = new Setting("Delay", "Delay when switching items", Double.valueOf(0.0D), Double.valueOf(0.0D), Double.valueOf(1000.0D), 0);
    public static Setting health = new Setting("Health", "Health considered as critical health", Double.valueOf(0.0D), Double.valueOf(16.0D), Double.valueOf(36.0D), 0);
    public static Setting swordGapple = new Setting("SwordGapple", "Use a gapple when holding a sword", Boolean.valueOf(true));
    public static Setting forceGapple = new Setting("ForceGapple", "Use a gapple when holding left click", Boolean.valueOf(false));
    public static Setting patchGapple = new Setting("Patch", "Partial Bypass for offhand patched servers", Boolean.valueOf(false));
    public static Setting recursive = new Setting("Recursive", "Allow the use of hotbar items", Boolean.valueOf(false));
    public static Setting motionStrict = new Setting("MotionStrict", "Stop motion when switching items", Boolean.valueOf(false));
    public static Setting fallSafe = new Setting("FallSafe", "Swaps to a totem when stepping/fast falling, prevents totem fails on crystalpvp.cc", Boolean.valueOf(false));
    public static Setting pause = new Setting("Pause", "When to pause and use a totem", Boolean.valueOf(true));
    public static Setting pauseLiquid = (new Setting("Liquid", "When in liquid", Boolean.valueOf(false))).setParent(Offhand.pause);
    public static Setting pauseAir = (new Setting("Air", "When falling or flying", Boolean.valueOf(true))).setParent(Offhand.pause);
    public static Setting pauseElytra = (new Setting("Elytra", "When elytra flying", Boolean.valueOf(true))).setParent(Offhand.pause);
    Timer offhandTimer = new Timer();

    public Offhand() {
        super("Offhand", Category.COMBAT, "Switches items in the offhand to a totem when low on health", get<invokedynamic>());
        Offhand.INSTANCE = this;
    }

    public void onUpdate() {
        Item offhandItem = ((Offhand.OffhandItem) Offhand.offHandItem.getValue()).getItem();

        if (Offhand.mc.currentScreen == null) {
            if (InventoryUtil.getItemCount(offhandItem) == 0 && !InventoryUtil.isSwitching()) {
                offhandItem = ((Offhand.OffhandItem) Offhand.fallBack.getValue()).getItem();
            }

            if (PlayerUtil.getHealth() <= ((Double) Offhand.health.getValue()).doubleValue() || !this.isSynced() || ((Boolean) Offhand.patchGapple.getValue()).booleanValue() && Offhand.mc.player.getHeldItemMainhand().getItem().equals(Items.GOLDEN_APPLE) || ((Boolean) Offhand.fallSafe.getValue()).booleanValue() && ReverseStep.INSTANCE.isEnabled() && Offhand.mc.player.motionY == ((Double) ReverseStep.speed.getValue()).doubleValue() || this.handlePause()) {
                offhandItem = Items.TOTEM_OF_UNDYING;
            }

            if (InventoryUtil.isHolding(Items.DIAMOND_SWORD) && ((Boolean) Offhand.swordGapple.getValue()).booleanValue() && !((Boolean) Offhand.forceGapple.getValue()).booleanValue() || InventoryUtil.isHolding(Items.DIAMOND_SWORD) && ((Boolean) Offhand.forceGapple.getValue()).booleanValue() && Mouse.isButtonDown(1)) {
                offhandItem = Items.GOLDEN_APPLE;
            }

            if (HoleUtil.isInHole(Offhand.mc.player) && PlayerUtil.getHealth() < ((Double) Offhand.holeHealth.getValue()).doubleValue()) {
                offhandItem = ((Offhand.OffhandItem) Offhand.hole.getValue()).getItem();
            }

            if (InventoryUtil.getItemSlot(offhandItem, InventoryUtil.Inventory.INVENTORY, ((Boolean) Offhand.recursive.getValue()).booleanValue()) != -1 && !Offhand.mc.player.getHeldItemOffhand().getItem().equals(offhandItem) && this.offhandTimer.passed((long) ((Double) Offhand.delay.getValue()).doubleValue(), Timer.Format.SYSTEM)) {
                if (((Boolean) Offhand.motionStrict.getValue()).booleanValue()) {
                    Offhand.mc.player.setVelocity(0.0D, 0.0D, 0.0D);
                }

                InventoryUtil.moveItemToOffhand(offhandItem, !((Boolean) Offhand.recursive.getValue()).booleanValue());
                this.offhandTimer.reset();
            }

        }
    }

    public boolean handlePause() {
        return !((Boolean) Offhand.pause.getValue()).booleanValue() ? false : (PlayerUtil.isInLiquid() && ((Boolean) Offhand.pauseLiquid.getValue()).booleanValue() ? true : (Offhand.mc.player.isElytraFlying() && ((Boolean) Offhand.pauseElytra.getValue()).booleanValue() ? true : Offhand.mc.player.fallDistance > 5.0F && ((Boolean) Offhand.pauseAir.getValue()).booleanValue()));
    }

    public boolean isSynced() {
        switch ((Offhand.Sync) Offhand.sync.getValue()) {
        case NONE:
        default:
            return true;

        case AUTOCRYSTAL:
            return AutoCrystal.INSTANCE.isEnabled();

        case INTERACT:
            return Mouse.isButtonDown(2);
        }
    }

    private static String lambda$new$0() {
        return Setting.formatEnum((Enum) Offhand.offHandItem.getValue());
    }

    public static enum Sync {

        AUTOCRYSTAL, INTERACT, NONE;
    }

    public static enum OffhandItem {

        CRYSTAL(Items.END_CRYSTAL), GAPPLE(Items.GOLDEN_APPLE), TOTEM(Items.TOTEM_OF_UNDYING);

        private final Item item;

        private OffhandItem(Item item) {
            this.item = item;
        }

        public Item getItem() {
            return this.item;
        }
    }
}
