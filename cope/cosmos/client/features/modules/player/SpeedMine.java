package cope.cosmos.client.features.modules.player;

import cope.cosmos.client.events.BlockResetEvent;
import cope.cosmos.client.events.PacketEvent;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.loader.asm.mixins.accessor.IPlayerControllerMP;
import cope.cosmos.util.player.InventoryUtil;
import cope.cosmos.util.render.RenderBuilder;
import cope.cosmos.util.render.RenderUtil;
import cope.cosmos.util.system.Timer;
import cope.cosmos.util.world.BlockUtil;
import java.awt.Color;
import java.util.Objects;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpeedMine extends Module {

    public static SpeedMine INSTANCE;
    public static Setting mode = new Setting("Mode", "Mode for SpeedMine", SpeedMine.Mine.PACKET);
    public static Setting damage = new Setting(get<invokedynamic>(), "Damage", "Instant block damage", Double.valueOf(0.0D), Double.valueOf(1.0D), Double.valueOf(1.0D), 1);
    public static Setting mineSwitch = new Setting("Switch", "Mode when switching to a pickaxe", InventoryUtil.Switch.NORMAL);
    public static Setting animation = new Setting("Animation", "Cancels swinging packets", Boolean.valueOf(false));
    public static Setting reset = new Setting("Reset", "Doesn\'t allow block break progress to be reset", Boolean.valueOf(false));
    public static Setting doubleBreak = new Setting("DoubleBreak", "Breaks blocks above the one you are mining", Boolean.valueOf(false));
    public static Setting onlyPick = new Setting("OnlyPickaxe", "Only applies speed mine when using a pickaxe", Boolean.valueOf(false));
    public static Setting kickBack = new Setting("KickBack", "Syncs client break progress to server break progress", Boolean.valueOf(false));
    public static Setting render = new Setting("Render", "Renders a visual over current mining block", Boolean.valueOf(true));
    public static Setting renderMode = (new Setting("Mode", "Style for the visual", RenderBuilder.Box.CLAW)).setParent(SpeedMine.render);
    public static Setting renderMine = (new Setting("MineColor", "Color for the mining block", new Color(250, 0, 250, 50))).setParent(SpeedMine.render);
    public static Setting renderAir = (new Setting("AirColor", "Color for the predicted broken block", new Color(144, 0, 255, 45))).setParent(SpeedMine.render);
    Timer mineTimer = new Timer();
    Timer switchTimer = new Timer();
    BlockPos minePosition;
    BlockPos mineBlock;
    EnumFacing mineFacing;
    int previousSlot;

    public SpeedMine() {
        super("SpeedMine", Category.PLAYER, "Mines faster", get<invokedynamic>());
        this.minePosition = BlockPos.ORIGIN;
        this.previousSlot = -1;
        SpeedMine.INSTANCE = this;
    }

    @SubscribeEvent
    public void onLeftClickBlock(LeftClickBlock event) {
        if (!((SpeedMine.Mine) SpeedMine.mode.getValue()).equals(SpeedMine.Mine.VANILLA) && SpeedMine.mc.player.isPotionActive(MobEffects.HASTE)) {
            SpeedMine.mc.player.removePotionEffect(MobEffects.HASTE);
        }

        if (Objects.equals(BlockUtil.getBlockResistance(event.getPos()), BlockUtil.BlockResistance.RESISTANT) || Objects.equals(BlockUtil.getBlockResistance(event.getPos()), BlockUtil.BlockResistance.BREAKABLE)) {
            this.mineBlock = event.getPos();
            this.mineFacing = event.getFace();
            if (((Boolean) SpeedMine.onlyPick.getValue()).booleanValue() && !InventoryUtil.isHolding(Items.DIAMOND_PICKAXE)) {
                return;
            }

            switch ((SpeedMine.Mine) SpeedMine.mode.getValue()) {
            case PACKET:
                this.mineTimer.reset();
                this.switchTimer.reset();
                this.minePosition = this.mineBlock;
                SpeedMine.mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, this.mineBlock, this.mineFacing));
                SpeedMine.mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, this.mineBlock, this.mineFacing));
                if (((Boolean) SpeedMine.kickBack.getValue()).booleanValue()) {
                    SpeedMine.mc.playerController.onPlayerDestroyBlock(this.mineBlock);
                }
                break;

            case DAMAGE:
                ((IPlayerControllerMP) SpeedMine.mc.playerController).setCurrentBlockDamage((float) ((Double) SpeedMine.damage.getValue()).doubleValue());
                if (((Boolean) SpeedMine.kickBack.getValue()).booleanValue()) {
                    SpeedMine.mc.playerController.onPlayerDestroyBlock(this.mineBlock);
                }
                break;

            case VANILLA:
                ((IPlayerControllerMP) SpeedMine.mc.playerController).setBlockHitDelay(0);
                SpeedMine.mc.player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 80950, 1, false, false));
                break;

            case FAKE:
                if (((Boolean) SpeedMine.kickBack.getValue()).booleanValue()) {
                    SpeedMine.mc.playerController.onPlayerDestroyBlock(this.mineBlock);
                }

                SpeedMine.mc.world.setBlockToAir(this.mineBlock);
            }

            if (((Boolean) SpeedMine.doubleBreak.getValue()).booleanValue() && Objects.equals(BlockUtil.getBlockResistance(this.mineBlock.up()), BlockUtil.BlockResistance.BREAKABLE) || Objects.equals(BlockUtil.getBlockResistance(this.mineBlock.up()), BlockUtil.BlockResistance.RESISTANT)) {
                switch ((SpeedMine.Mine) SpeedMine.mode.getValue()) {
                case PACKET:
                    SpeedMine.mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, this.mineBlock.up(), this.mineFacing));
                    SpeedMine.mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, this.mineBlock.up(), this.mineFacing));
                    if (((Boolean) SpeedMine.kickBack.getValue()).booleanValue()) {
                        SpeedMine.mc.playerController.onPlayerDestroyBlock(this.mineBlock);
                    }

                case DAMAGE:
                case VANILLA:
                default:
                    break;

                case FAKE:
                    if (((Boolean) SpeedMine.kickBack.getValue()).booleanValue()) {
                        SpeedMine.mc.playerController.onPlayerDestroyBlock(this.mineBlock);
                    }

                    SpeedMine.mc.world.setBlockToAir(this.mineBlock.up());
                }
            }

            this.previousSlot = SpeedMine.mc.player.inventory.currentItem;
        }

        if (this.mineTimer.passed(2000L, Timer.Format.SYSTEM) && !this.switchTimer.passed(2200L, Timer.Format.SYSTEM) && ((SpeedMine.Mine) SpeedMine.mode.getValue()).equals(SpeedMine.Mine.PACKET)) {
            InventoryUtil.switchToSlot(Items.DIAMOND_PICKAXE, (InventoryUtil.Switch) SpeedMine.mineSwitch.getValue());
        }

        if (this.minePosition != BlockPos.ORIGIN && this.switchTimer.passed(2100L, Timer.Format.SYSTEM) && ((SpeedMine.Mine) SpeedMine.mode.getValue()).equals(SpeedMine.Mine.PACKET) && !((InventoryUtil.Switch) SpeedMine.mineSwitch.getValue()).equals(InventoryUtil.Switch.NONE)) {
            InventoryUtil.switchToSlot(this.previousSlot, InventoryUtil.Switch.NORMAL);
            this.minePosition = BlockPos.ORIGIN;
        }

        if (Objects.equals(BlockUtil.getBlockResistance(this.minePosition), BlockUtil.BlockResistance.BLANK) && ((SpeedMine.Mine) SpeedMine.mode.getValue()).equals(SpeedMine.Mine.PACKET) && ((InventoryUtil.Switch) SpeedMine.mineSwitch.getValue()).equals(InventoryUtil.Switch.NONE) || Objects.equals(BlockUtil.getBlockResistance(this.minePosition), BlockUtil.BlockResistance.BLANK)) {
            this.minePosition = BlockPos.ORIGIN;
        }

    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        event.setCanceled(this.nullCheck() && event.getPacket() instanceof CPacketAnimation && ((Boolean) SpeedMine.animation.getValue()).booleanValue());
    }

    @SubscribeEvent
    public void onBlockReset(BlockResetEvent event) {
        event.setCanceled(this.nullCheck() && ((Boolean) SpeedMine.reset.getValue()).booleanValue());
    }

    public void onDisable() {
        super.onDisable();
        SpeedMine.mc.player.removePotionEffect(MobEffects.HASTE);
        this.minePosition = BlockPos.ORIGIN;
    }

    public void onRender3d() {
        if (this.minePosition != BlockPos.ORIGIN && ((Boolean) SpeedMine.render.getValue()).booleanValue()) {
            RenderUtil.drawBox((new RenderBuilder()).position(this.minePosition).color(this.switchTimer.passed(2000L, Timer.Format.SYSTEM) ? (Color) SpeedMine.renderAir.getValue() : (Color) SpeedMine.renderMine.getValue()).box((RenderBuilder.Box) SpeedMine.renderMode.getValue()).setup().line(1.5F).cull(((RenderBuilder.Box) SpeedMine.renderMode.getValue()).equals(RenderBuilder.Box.GLOW) || ((RenderBuilder.Box) SpeedMine.renderMode.getValue()).equals(RenderBuilder.Box.REVERSE)).shade(((RenderBuilder.Box) SpeedMine.renderMode.getValue()).equals(RenderBuilder.Box.GLOW) || ((RenderBuilder.Box) SpeedMine.renderMode.getValue()).equals(RenderBuilder.Box.REVERSE)).alpha(((RenderBuilder.Box) SpeedMine.renderMode.getValue()).equals(RenderBuilder.Box.GLOW) || ((RenderBuilder.Box) SpeedMine.renderMode.getValue()).equals(RenderBuilder.Box.REVERSE)).depth(true).blend().texture());
        }

    }

    private static Boolean lambda$static$1() {
        return Boolean.valueOf(((SpeedMine.Mine) SpeedMine.mode.getValue()).equals(SpeedMine.Mine.DAMAGE));
    }

    private static String lambda$new$0() {
        return Setting.formatEnum((Enum) SpeedMine.mode.getValue());
    }

    public static enum Mine {

        PACKET, DAMAGE, VANILLA, FAKE;
    }
}
