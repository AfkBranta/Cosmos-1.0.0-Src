package cope.cosmos.client.features.modules.combat;

import cope.cosmos.client.Cosmos;
import cope.cosmos.client.events.PacketEvent;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.client.manager.managers.SocialManager;
import cope.cosmos.client.manager.managers.TickManager;
import cope.cosmos.loader.asm.mixins.accessor.ICPacketPlayer;
import cope.cosmos.loader.asm.mixins.accessor.ICPacketUseEntity;
import cope.cosmos.loader.asm.mixins.accessor.IPlayerControllerMP;
import cope.cosmos.util.combat.EnemyUtil;
import cope.cosmos.util.combat.ExplosionUtil;
import cope.cosmos.util.combat.TargetUtil;
import cope.cosmos.util.player.InventoryUtil;
import cope.cosmos.util.player.PlayerUtil;
import cope.cosmos.util.player.Rotation;
import cope.cosmos.util.render.RenderBuilder;
import cope.cosmos.util.render.RenderUtil;
import cope.cosmos.util.system.MathUtil;
import cope.cosmos.util.system.Timer;
import cope.cosmos.util.world.AngleUtil;
import cope.cosmos.util.world.BlockUtil;
import cope.cosmos.util.world.HoleUtil;
import cope.cosmos.util.world.RaytraceUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoCrystal extends Module {

    public static AutoCrystal INSTANCE;
    public static Setting explode = new Setting("Explode", "Explode crystals", Boolean.valueOf(true));
    public static Setting explodeRange = (new Setting("Range", "Range to explode crystals", Double.valueOf(0.0D), Double.valueOf(6.0D), Double.valueOf(8.0D), 1)).setParent(AutoCrystal.explode);
    public static Setting explodeWall = (new Setting("WallRange", "Range to explode crystals through walls", Double.valueOf(0.0D), Double.valueOf(3.5D), Double.valueOf(8.0D), 1)).setParent(AutoCrystal.explode);
    public static Setting explodeDelay = (new Setting("Delay", "Delay to explode crystals", Double.valueOf(0.0D), Double.valueOf(60.0D), Double.valueOf(500.0D), 0)).setParent(AutoCrystal.explode);
    public static Setting explodeSwitch = (new Setting("SwitchDelay", "Delay to wait after switching", Double.valueOf(0.0D), Double.valueOf(0.0D), Double.valueOf(500.0D), 0)).setParent(AutoCrystal.explode);
    public static Setting explodeDamage = (new Setting("Damage", "Required damage to explode a crystal", Double.valueOf(0.0D), Double.valueOf(5.0D), Double.valueOf(36.0D), 1)).setParent(AutoCrystal.explode);
    public static Setting explodeLocal = (new Setting("LocalDamage", "Maximum allowed local damage to the player", Double.valueOf(0.0D), Double.valueOf(5.0D), Double.valueOf(36.0D), 1)).setParent(AutoCrystal.explode);
    public static Setting explodeAttacks = (new Setting("Attacks", "Attacks per crystal", Double.valueOf(1.0D), Double.valueOf(1.0D), Double.valueOf(5.0D), 0)).setParent(AutoCrystal.explode);
    public static Setting explodeLimit = (new Setting("Limit", "Attacks per crystal limiter", Double.valueOf(0.0D), Double.valueOf(10.0D), Double.valueOf(10.0D), 0)).setParent(AutoCrystal.explode);
    public static Setting explodePacket = (new Setting("Packet", "Explode with packets", Boolean.valueOf(true))).setParent(AutoCrystal.explode);
    public static Setting explodeHand = (new Setting("Hand", "Hand to swing when exploding crystals", PlayerUtil.Hand.MAINHAND)).setParent(AutoCrystal.explode);
    public static Setting explodeWeakness = (new Setting("Weakness", "Switch to a tool when weakness is active", InventoryUtil.Switch.NONE)).setParent(AutoCrystal.explode);
    public static Setting place = new Setting("Place", "Place Crystals", Boolean.valueOf(true));
    public static Setting placeRange = (new Setting("Range", "Range to place crystals", Double.valueOf(0.0D), Double.valueOf(5.0D), Double.valueOf(8.0D), 1)).setParent(AutoCrystal.place);
    public static Setting placeWall = (new Setting("WallRange", "Range to place crystals through walls", Double.valueOf(0.0D), Double.valueOf(3.5D), Double.valueOf(8.0D), 1)).setParent(AutoCrystal.place);
    public static Setting placeDelay = (new Setting("Delay", "Delay to place crystals", Double.valueOf(0.0D), Double.valueOf(20.0D), Double.valueOf(500.0D), 0)).setParent(AutoCrystal.place);
    public static Setting placeRandom = (new Setting("RandomDelay", "Randomize the delay slightly to simulate real placements", Double.valueOf(0.0D), Double.valueOf(0.0D), Double.valueOf(500.0D), 0)).setParent(AutoCrystal.place);
    public static Setting placeDamage = (new Setting("Damage", "Required damage to be considered for placement", Double.valueOf(0.0D), Double.valueOf(5.0D), Double.valueOf(36.0D), 1)).setParent(AutoCrystal.place);
    public static Setting placeLocal = (new Setting("LocalDamage", "Maximum allowed local damage to the player", Double.valueOf(0.0D), Double.valueOf(5.0D), Double.valueOf(36.0D), 1)).setParent(AutoCrystal.place);
    public static Setting placeAttempts = (new Setting("Attempts", "Place attempts per cycle", Double.valueOf(1.0D), Double.valueOf(1.0D), Double.valueOf(5.0D), 0)).setParent(AutoCrystal.place);
    public static Setting placePacket = (new Setting("Packet", "Place with packets", Boolean.valueOf(true))).setParent(AutoCrystal.place);
    public static Setting placeDirection = (new Setting("StrictDirection", "Limits the direction of placements to only upward facing", Boolean.valueOf(false))).setParent(AutoCrystal.place);
    public static Setting placeRaytrace = (new Setting("Raytrace", "Mode to verify placements through walls", AutoCrystal.Raytrace.DOUBLE)).setParent(AutoCrystal.place);
    public static Setting placeHand = (new Setting("Hand", "Hand to swing when placing crystals", PlayerUtil.Hand.MAINHAND)).setParent(AutoCrystal.place);
    public static Setting placeSwitch = (new Setting("Switch", "Mode to use when switching to a crystal", InventoryUtil.Switch.NONE)).setParent(AutoCrystal.place);
    public static Setting pause = new Setting("Pause", "When to pause", Boolean.valueOf(true));
    public static Setting pauseHealth = (new Setting("Health", "Pause below this health", Double.valueOf(0.0D), Double.valueOf(10.0D), Double.valueOf(36.0D), 0)).setParent(AutoCrystal.pause);
    public static Setting pauseSafety = (new Setting("Safety", "Pause when the current crystal will kill you", Boolean.valueOf(true))).setParent(AutoCrystal.pause);
    public static Setting pauseEating = (new Setting("Eating", "Pause when eating", Boolean.valueOf(false))).setParent(AutoCrystal.pause);
    public static Setting pauseMining = (new Setting("Mining", "Pause when mining", Boolean.valueOf(false))).setParent(AutoCrystal.pause);
    public static Setting pauseMending = (new Setting("Mending", "Pause when mending", Boolean.valueOf(false))).setParent(AutoCrystal.pause);
    public static Setting override = new Setting("Override", "When to override minimum damage", Boolean.valueOf(true));
    public static Setting overrideHealth = (new Setting("Health", "Override when target is below this health", Double.valueOf(0.0D), Double.valueOf(10.0D), Double.valueOf(36.0D), 0)).setParent(AutoCrystal.override);
    public static Setting overrideThreshold = (new Setting("Threshold", "Override if we can do lethal damage in this amount of crystals", Double.valueOf(0.0D), Double.valueOf(0.0D), Double.valueOf(4.0D), 1)).setParent(AutoCrystal.override);
    public static Setting overrideArmor = (new Setting("Armor", "Override when target\'s armor is below this percent", Double.valueOf(0.0D), Double.valueOf(0.0D), Double.valueOf(100.0D), 0)).setParent(AutoCrystal.override);
    public static Setting rotate = new Setting("Rotation", "Mode for attack and placement rotation", Rotation.Rotate.NONE);
    public static Setting rotateStep = (new Setting("Step", "Number of divisions when sending rotation packets", Float.valueOf(1.0F), Float.valueOf(1.0F), Float.valueOf(10.0F), 0)).setParent(AutoCrystal.rotate);
    public static Setting rotateRandom = (new Setting("Random", "Randomize rotations to simulate real rotations", Double.valueOf(0.0D), Double.valueOf(4.0D), Double.valueOf(10.0D), 1)).setParent(AutoCrystal.rotate);
    public static Setting rotateCenter = (new Setting("Center", "Center rotations on target", Boolean.valueOf(false))).setParent(AutoCrystal.rotate);
    public static Setting rotateWhen = (new Setting("When", "Mode for when to rotate", AutoCrystal.When.BOTH)).setParent(AutoCrystal.rotate);
    public static Setting calculations = new Setting("Calculations", "Preferences for calculations", Boolean.valueOf(true));
    public static Setting prediction = (new Setting("Prediction", "Attempts to account target\'s predicted position into the calculations", Boolean.valueOf(false))).setParent(AutoCrystal.calculations);
    public static Setting ignoreTerrain = (new Setting("IgnoreTerrain", "Ignores terrain when calculating damage", Boolean.valueOf(false))).setParent(AutoCrystal.calculations);
    public static Setting timing = (new Setting("Timing", "Optimizes process at the cost of anti-cheat compatibility", AutoCrystal.Timing.LINEAR)).setParent(AutoCrystal.calculations);
    public static Setting tps = (new Setting("TPS", "Syncs attack timing to current server ticks", TickManager.TPS.NONE)).setParent(AutoCrystal.calculations);
    public static Setting placements = (new Setting("Placements", "Placement calculations for current version", AutoCrystal.Placements.NATIVE)).setParent(AutoCrystal.calculations);
    public static Setting logic = (new Setting("Logic", "Logic for heuristic to prioritize", AutoCrystal.Logic.DAMAGE)).setParent(AutoCrystal.calculations);
    public static Setting sync = (new Setting("Sync", "Sync for broken crystals", AutoCrystal.Sync.SOUND)).setParent(AutoCrystal.calculations);
    public static Setting target = new Setting("Target", "Priority for searching target", TargetUtil.Target.CLOSEST);
    public static Setting targetRange = (new Setting("Range", "Range to consider an entity as a target", Double.valueOf(0.0D), Double.valueOf(10.0D), Double.valueOf(15.0D), 1)).setParent(AutoCrystal.target);
    public static Setting targetPlayers = (new Setting("Players", "Target players", Boolean.valueOf(true))).setParent(AutoCrystal.target);
    public static Setting targetPassives = (new Setting("Passives", "Target passives", Boolean.valueOf(false))).setParent(AutoCrystal.target);
    public static Setting targetNeutrals = (new Setting("Neutrals", "Target neutrals", Boolean.valueOf(false))).setParent(AutoCrystal.target);
    public static Setting targetHostiles = (new Setting("Hostiles", "Target hostiles", Boolean.valueOf(false))).setParent(AutoCrystal.target);
    public static Setting render = new Setting("Render", "Render a visual for calculated placement", Boolean.valueOf(true));
    public static Setting renderMode = (new Setting("Mode", "Style for visual", RenderBuilder.Box.BOTH)).setParent(AutoCrystal.render);
    public static Setting renderText = (new Setting("Text", "Text for the visual", AutoCrystal.Text.NONE)).setParent(AutoCrystal.render);
    public static Setting renderInfo = (new Setting("Info", "Arraylist information", AutoCrystal.Info.NONE)).setParent(AutoCrystal.render);
    public static Setting renderWidth = (new Setting(get<invokedynamic>(), "Width", "Line width for the visual", Double.valueOf(0.0D), Double.valueOf(1.5D), Double.valueOf(3.0D), 1)).setParent(AutoCrystal.render);
    public static Setting renderColor = (new Setting("Color", "Color for the visual", new Color(144, 0, 255, 45))).setParent(AutoCrystal.render);
    private final Timer explodeTimer = new Timer();
    private final Timer switchTimer = new Timer();
    public static AutoCrystal.Crystal explodeCrystal = new AutoCrystal.Crystal((EntityEnderCrystal) null, 0.0D, 0.0D);
    public static Map attemptedExplosions = new HashMap();
    private final Timer placeTimer = new Timer();
    public static AutoCrystal.CrystalPosition placePosition = new AutoCrystal.CrystalPosition(BlockPos.ORIGIN, (EntityPlayer) null, 0.0D, 0.0D);
    private Rotation crystalRotation;

    public AutoCrystal() {
        super("AutoCrystal", Category.COMBAT, "Places and explodes crystals", get<invokedynamic>());
        AutoCrystal.INSTANCE = this;
    }

    public void onUpdate() {
        this.explodeCrystal();
        this.placeCrystal();
    }

    public void onThread() {
        if (((Boolean) AutoCrystal.pause.getValue()).booleanValue()) {
            if (PlayerUtil.isEating() && ((Boolean) AutoCrystal.pauseEating.getValue()).booleanValue() || PlayerUtil.isMining() && ((Boolean) AutoCrystal.pauseMining.getValue()).booleanValue() || PlayerUtil.isMending() && ((Boolean) AutoCrystal.pauseMending.getValue()).booleanValue()) {
                return;
            }

            if (PlayerUtil.getHealth() < ((Double) AutoCrystal.pauseHealth.getValue()).doubleValue() && !AutoCrystal.mc.player.capabilities.isCreativeMode) {
                return;
            }
        }

        AutoCrystal.explodeCrystal = this.searchCrystal();
        AutoCrystal.placePosition = this.searchPosition();
    }

    public void explodeCrystal() {
        if (AutoCrystal.explodeCrystal != null) {
            if (!((Rotation.Rotate) AutoCrystal.rotate.getValue()).equals(Rotation.Rotate.NONE) && (((AutoCrystal.When) AutoCrystal.rotateWhen.getValue()).equals(AutoCrystal.When.BREAK) || ((AutoCrystal.When) AutoCrystal.rotateWhen.getValue()).equals(AutoCrystal.When.BOTH))) {
                float[] scaledDelay = ((Boolean) AutoCrystal.rotateCenter.getValue()).booleanValue() ? AngleUtil.calculateCenter((Entity) AutoCrystal.explodeCrystal.getCrystal()) : AngleUtil.calculateAngles((Entity) AutoCrystal.explodeCrystal.getCrystal());

                this.crystalRotation = new Rotation((float) ((double) scaledDelay[0] + ThreadLocalRandom.current().nextDouble(-((Double) AutoCrystal.rotateRandom.getValue()).doubleValue(), ((Double) AutoCrystal.rotateRandom.getValue()).doubleValue())), (float) ((double) scaledDelay[1] + ThreadLocalRandom.current().nextDouble(-((Double) AutoCrystal.rotateRandom.getValue()).doubleValue(), ((Double) AutoCrystal.rotateRandom.getValue()).doubleValue())), (Rotation.Rotate) AutoCrystal.rotate.getValue());
                if (!Float.isNaN(this.crystalRotation.getYaw()) && !Float.isNaN(this.crystalRotation.getPitch())) {
                    this.crystalRotation.updateModelRotations();
                }
            }

            int explodeAttack;

            if (!((InventoryUtil.Switch) AutoCrystal.explodeWeakness.getValue()).equals(InventoryUtil.Switch.NONE)) {
                PotionEffect potioneffect = AutoCrystal.mc.player.getActivePotionEffect(MobEffects.WEAKNESS);
                PotionEffect strengthEffect = AutoCrystal.mc.player.getActivePotionEffect(MobEffects.STRENGTH);

                if (potioneffect != null && (strengthEffect == null || strengthEffect.getAmplifier() < potioneffect.getAmplifier())) {
                    explodeAttack = InventoryUtil.getItemSlot(Items.DIAMOND_SWORD, InventoryUtil.Inventory.INVENTORY, true);
                    int pickSlot = InventoryUtil.getItemSlot(Items.DIAMOND_SWORD, InventoryUtil.Inventory.INVENTORY, true);

                    if (!InventoryUtil.isHolding(Items.DIAMOND_SWORD) || !InventoryUtil.isHolding(Items.DIAMOND_PICKAXE)) {
                        if (explodeAttack != -1) {
                            InventoryUtil.switchToSlot(explodeAttack, (InventoryUtil.Switch) AutoCrystal.explodeWeakness.getValue());
                        } else if (pickSlot != -1) {
                            InventoryUtil.switchToSlot(pickSlot, (InventoryUtil.Switch) AutoCrystal.explodeWeakness.getValue());
                        }
                    }
                }
            }

            double d0 = ((Double) AutoCrystal.explodeDelay.getValue()).doubleValue();

            if (!((TickManager.TPS) AutoCrystal.tps.getValue()).equals(TickManager.TPS.NONE)) {
                d0 *= (double) (80.0F * (1.0F - Cosmos.INSTANCE.getTickManager().getTPS((TickManager.TPS) AutoCrystal.tps.getValue()) / 20.0F));
            }

            if (this.explodeTimer.passed((long) d0, Timer.Format.SYSTEM) && this.switchTimer.passed((long) ((Double) AutoCrystal.explodeSwitch.getValue()).doubleValue(), Timer.Format.SYSTEM)) {
                if (((Double) AutoCrystal.explodeAttacks.getValue()).doubleValue() > 1.0D) {
                    for (explodeAttack = 0; (double) explodeAttack < ((Double) AutoCrystal.explodeAttacks.getValue()).doubleValue(); ++explodeAttack) {
                        this.explodeCrystal(AutoCrystal.explodeCrystal.getCrystal(), ((Boolean) AutoCrystal.explodePacket.getValue()).booleanValue());
                    }
                } else {
                    this.explodeCrystal(AutoCrystal.explodeCrystal.getCrystal(), ((Boolean) AutoCrystal.explodePacket.getValue()).booleanValue());
                }

                PlayerUtil.swingArm((PlayerUtil.Hand) AutoCrystal.explodeHand.getValue());
                this.explodeTimer.reset();
                AutoCrystal.attemptedExplosions.put(Integer.valueOf(AutoCrystal.explodeCrystal.getCrystal().getEntityId()), Integer.valueOf(AutoCrystal.attemptedExplosions.containsKey(Integer.valueOf(AutoCrystal.explodeCrystal.getCrystal().getEntityId())) ? ((Integer) AutoCrystal.attemptedExplosions.get(Integer.valueOf(AutoCrystal.explodeCrystal.getCrystal().getEntityId()))).intValue() + 1 : 1));
                if (((AutoCrystal.Sync) AutoCrystal.sync.getValue()).equals(AutoCrystal.Sync.INSTANT)) {
                    AutoCrystal.explodeCrystal.getCrystal().setDead();
                }
            }
        }

    }

    public void placeCrystal() {
        if (AutoCrystal.placePosition != null) {
            if (!((Rotation.Rotate) AutoCrystal.rotate.getValue()).equals(Rotation.Rotate.NONE) && (((AutoCrystal.When) AutoCrystal.rotateWhen.getValue()).equals(AutoCrystal.When.PLACE) || ((AutoCrystal.When) AutoCrystal.rotateWhen.getValue()).equals(AutoCrystal.When.BOTH))) {
                float[] facingResult = ((Boolean) AutoCrystal.rotateCenter.getValue()).booleanValue() ? AngleUtil.calculateCenter(AutoCrystal.placePosition.getPosition()) : AngleUtil.calculateAngles(AutoCrystal.placePosition.getPosition());

                this.crystalRotation = new Rotation((float) ((double) facingResult[0] + ThreadLocalRandom.current().nextDouble(-((Double) AutoCrystal.rotateRandom.getValue()).doubleValue(), ((Double) AutoCrystal.rotateRandom.getValue()).doubleValue())), (float) ((double) facingResult[1] + ThreadLocalRandom.current().nextDouble(-((Double) AutoCrystal.rotateRandom.getValue()).doubleValue(), ((Double) AutoCrystal.rotateRandom.getValue()).doubleValue())), (Rotation.Rotate) AutoCrystal.rotate.getValue());
                if (!Float.isNaN(this.crystalRotation.getYaw()) && !Float.isNaN(this.crystalRotation.getPitch())) {
                    this.crystalRotation.updateModelRotations();
                }
            }

            InventoryUtil.switchToSlot(Items.END_CRYSTAL, (InventoryUtil.Switch) AutoCrystal.placeSwitch.getValue());
            ((IPlayerControllerMP) AutoCrystal.mc.playerController).syncCurrentPlayItem();
            if (this.placeTimer.passed((long) (((Double) AutoCrystal.placeDelay.getValue()).doubleValue() + ThreadLocalRandom.current().nextDouble(((Double) AutoCrystal.placeRandom.getValue()).doubleValue() + 1.0D)), Timer.Format.SYSTEM) && InventoryUtil.isHolding(Items.END_CRYSTAL)) {
                EnumFacing placementFacing = EnumFacing.DOWN;

                if (!((Boolean) AutoCrystal.placeDirection.getValue()).booleanValue()) {
                    RayTraceResult raytraceresult = AutoCrystal.mc.world.rayTraceBlocks(new Vec3d(AutoCrystal.mc.player.posX, AutoCrystal.mc.player.posY + (double) AutoCrystal.mc.player.getEyeHeight(), AutoCrystal.mc.player.posZ), new Vec3d((double) AutoCrystal.placePosition.getPosition().getX() + 0.5D, (double) AutoCrystal.placePosition.getPosition().getY() - 0.5D, (double) AutoCrystal.placePosition.getPosition().getZ() + 0.5D));

                    placementFacing = raytraceresult != null && raytraceresult.sideHit != null ? raytraceresult.sideHit : EnumFacing.UP;
                }

                if (((Double) AutoCrystal.placeAttempts.getValue()).doubleValue() > 1.0D) {
                    for (int placeAttempt = 0; (double) placeAttempt < ((Double) AutoCrystal.placeAttempts.getValue()).doubleValue(); ++placeAttempt) {
                        this.placeCrystal(AutoCrystal.placePosition.getPosition(), ((Boolean) AutoCrystal.placeDirection.getValue()).booleanValue() ? EnumFacing.UP : placementFacing, ((Boolean) AutoCrystal.placePacket.getValue()).booleanValue());
                    }
                } else {
                    this.placeCrystal(AutoCrystal.placePosition.getPosition(), ((Boolean) AutoCrystal.placeDirection.getValue()).booleanValue() ? EnumFacing.UP : placementFacing, ((Boolean) AutoCrystal.placePacket.getValue()).booleanValue());
                }

                PlayerUtil.swingArm((PlayerUtil.Hand) AutoCrystal.placeHand.getValue());
                this.placeTimer.reset();
            }
        }

    }

    public AutoCrystal.Crystal searchCrystal() {
        if (((Boolean) AutoCrystal.explode.getValue()).booleanValue()) {
            TreeMap crystalMap = new TreeMap();
            Iterator idealCrystal = AutoCrystal.mc.world.loadedEntityList.iterator();

            while (idealCrystal.hasNext()) {
                Entity calculatedCrystal = (Entity) idealCrystal.next();

                if (calculatedCrystal instanceof EntityEnderCrystal && !calculatedCrystal.isDead) {
                    float distance = AutoCrystal.mc.player.getDistance(calculatedCrystal);

                    if ((double) distance <= ((Double) AutoCrystal.explodeRange.getValue()).doubleValue() && (AutoCrystal.mc.player.canEntityBeSeen(calculatedCrystal) || (double) distance <= ((Double) AutoCrystal.explodeWall.getValue()).doubleValue())) {
                        float localDamage = AutoCrystal.mc.player.capabilities.isCreativeMode ? 0.0F : ExplosionUtil.getDamageFromExplosion(calculatedCrystal.posX, calculatedCrystal.posY, calculatedCrystal.posZ, AutoCrystal.mc.player, ((Boolean) AutoCrystal.ignoreTerrain.getValue()).booleanValue(), false);

                        if ((double) localDamage <= ((Double) AutoCrystal.explodeLocal.getValue()).doubleValue() && ((double) (localDamage + 1.0F) <= PlayerUtil.getHealth() || !((Boolean) AutoCrystal.pauseSafety.getValue()).booleanValue()) && (!AutoCrystal.attemptedExplosions.containsKey(Integer.valueOf(calculatedCrystal.getEntityId())) || ((Double) AutoCrystal.explodeLimit.getValue()).doubleValue() >= 10.0D || (double) ((Integer) AutoCrystal.attemptedExplosions.get(Integer.valueOf(calculatedCrystal.getEntityId()))).intValue() <= ((Double) AutoCrystal.explodeLimit.getValue()).doubleValue())) {
                            Iterator iterator = AutoCrystal.mc.world.playerEntities.iterator();

                            while (iterator.hasNext()) {
                                EntityPlayer calculatedTarget = (EntityPlayer) iterator.next();

                                if (!calculatedTarget.equals(AutoCrystal.mc.player) && !EnemyUtil.isDead(calculatedTarget) && !Cosmos.INSTANCE.getSocialManager().getSocial(calculatedTarget.getName()).equals(SocialManager.Relationship.FRIEND)) {
                                    float targetDistance = AutoCrystal.mc.player.getDistance(calculatedTarget);

                                    if ((double) targetDistance <= ((Double) AutoCrystal.targetRange.getValue()).doubleValue()) {
                                        float targetDamage = this.calculateLogic(ExplosionUtil.getDamageFromExplosion(calculatedCrystal.posX, calculatedCrystal.posY, calculatedCrystal.posZ, calculatedTarget, ((Boolean) AutoCrystal.ignoreTerrain.getValue()).booleanValue(), ((Boolean) AutoCrystal.prediction.getValue()).booleanValue()), localDamage, (double) distance);

                                        crystalMap.put(Float.valueOf(targetDamage), new AutoCrystal.Crystal((EntityEnderCrystal) calculatedCrystal, (double) targetDamage, (double) localDamage));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!crystalMap.isEmpty()) {
                AutoCrystal.Crystal idealCrystal1 = (AutoCrystal.Crystal) crystalMap.lastEntry().getValue();

                if (idealCrystal1.getTargetDamage() >= ((Double) AutoCrystal.explodeDamage.getValue()).doubleValue()) {
                    return idealCrystal1;
                }
            }
        }

        return null;
    }

    public AutoCrystal.CrystalPosition searchPosition() {
        if (((Boolean) AutoCrystal.place.getValue()).booleanValue()) {
            TreeMap positionMap = new TreeMap();
            Iterator idealPosition = BlockUtil.getSurroundingBlocks(AutoCrystal.mc.player, ((Double) AutoCrystal.placeRange.getValue()).doubleValue(), false).iterator();

            while (idealPosition.hasNext()) {
                BlockPos requiredDamage = (BlockPos) idealPosition.next();

                if (this.canPlaceCrystal(requiredDamage, (AutoCrystal.Placements) AutoCrystal.placements.getValue())) {
                    float localDamage = AutoCrystal.mc.player.capabilities.isCreativeMode ? 0.0F : ExplosionUtil.getDamageFromExplosion((double) requiredDamage.getX() + 0.5D, (double) (requiredDamage.getY() + 1), (double) requiredDamage.getZ() + 0.5D, AutoCrystal.mc.player, ((Boolean) AutoCrystal.ignoreTerrain.getValue()).booleanValue(), false);

                    if ((double) localDamage <= ((Double) AutoCrystal.placeLocal.getValue()).doubleValue() && ((double) (localDamage + 1.0F) <= PlayerUtil.getHealth() || !((Boolean) AutoCrystal.pauseSafety.getValue()).booleanValue())) {
                        boolean wallPlacement = !((AutoCrystal.Raytrace) AutoCrystal.placeRaytrace.getValue()).equals(AutoCrystal.Raytrace.NONE) && RaytraceUtil.raytraceBlock(requiredDamage, (AutoCrystal.Raytrace) AutoCrystal.placeRaytrace.getValue());
                        double distance = AutoCrystal.mc.player.getDistance((double) requiredDamage.getX() + 0.5D, (double) (requiredDamage.getY() + 1), (double) requiredDamage.getZ() + 0.5D);

                        if (distance <= ((Double) AutoCrystal.placeWall.getValue()).doubleValue() || !wallPlacement) {
                            Iterator iterator = AutoCrystal.mc.world.playerEntities.iterator();

                            while (iterator.hasNext()) {
                                EntityPlayer calculatedTarget = (EntityPlayer) iterator.next();

                                if (!calculatedTarget.equals(AutoCrystal.mc.player) && !EnemyUtil.isDead(calculatedTarget) && !Cosmos.INSTANCE.getSocialManager().getSocial(calculatedTarget.getName()).equals(SocialManager.Relationship.FRIEND)) {
                                    float targetDistance = AutoCrystal.mc.player.getDistance(calculatedTarget);

                                    if ((double) targetDistance <= ((Double) AutoCrystal.targetRange.getValue()).doubleValue()) {
                                        float targetDamage = this.calculateLogic(ExplosionUtil.getDamageFromExplosion((double) requiredDamage.getX() + 0.5D, (double) (requiredDamage.getY() + 1), (double) requiredDamage.getZ() + 0.5D, calculatedTarget, ((Boolean) AutoCrystal.ignoreTerrain.getValue()).booleanValue(), ((Boolean) AutoCrystal.prediction.getValue()).booleanValue()), localDamage, distance);

                                        positionMap.put(Float.valueOf(targetDamage), new AutoCrystal.CrystalPosition(requiredDamage, calculatedTarget, (double) targetDamage, (double) localDamage));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!positionMap.isEmpty()) {
                AutoCrystal.CrystalPosition idealPosition1 = (AutoCrystal.CrystalPosition) positionMap.lastEntry().getValue();
                double requiredDamage1 = ((Double) AutoCrystal.placeDamage.getValue()).doubleValue();

                if (((Boolean) AutoCrystal.override.getValue()).booleanValue()) {
                    if (idealPosition1.getTargetDamage() * ((Double) AutoCrystal.overrideThreshold.getValue()).doubleValue() >= (double) EnemyUtil.getHealth(idealPosition1.getPlaceTarget())) {
                        requiredDamage1 = 0.5D;
                    }

                    if (HoleUtil.isInHole(idealPosition1.getPlaceTarget())) {
                        if ((double) EnemyUtil.getHealth(idealPosition1.getPlaceTarget()) < ((Double) AutoCrystal.overrideHealth.getValue()).doubleValue()) {
                            requiredDamage1 = 0.5D;
                        }

                        if (EnemyUtil.getArmor(idealPosition1.getPlaceTarget(), ((Double) AutoCrystal.overrideArmor.getValue()).doubleValue())) {
                            requiredDamage1 = 0.5D;
                        }
                    }
                }

                if (idealPosition1.getTargetDamage() > requiredDamage1) {
                    return idealPosition1;
                }
            }
        }

        return null;
    }

    public void onRender3d() {
        if (((Boolean) AutoCrystal.render.getValue()).booleanValue() && !AutoCrystal.placePosition.getPosition().equals(BlockPos.ORIGIN) && InventoryUtil.isHolding(Items.END_CRYSTAL)) {
            RenderUtil.drawBox((new RenderBuilder()).position(AutoCrystal.placePosition.getPosition()).color((Color) AutoCrystal.renderColor.getValue()).box((RenderBuilder.Box) AutoCrystal.renderMode.getValue()).setup().line((float) ((Double) AutoCrystal.renderWidth.getValue()).doubleValue()).cull(((RenderBuilder.Box) AutoCrystal.renderMode.getValue()).equals(RenderBuilder.Box.GLOW) || ((RenderBuilder.Box) AutoCrystal.renderMode.getValue()).equals(RenderBuilder.Box.REVERSE)).shade(((RenderBuilder.Box) AutoCrystal.renderMode.getValue()).equals(RenderBuilder.Box.GLOW) || ((RenderBuilder.Box) AutoCrystal.renderMode.getValue()).equals(RenderBuilder.Box.REVERSE)).alpha(((RenderBuilder.Box) AutoCrystal.renderMode.getValue()).equals(RenderBuilder.Box.GLOW) || ((RenderBuilder.Box) AutoCrystal.renderMode.getValue()).equals(RenderBuilder.Box.REVERSE)).depth(true).blend().texture());
            RenderUtil.drawNametag(AutoCrystal.placePosition.getPosition(), 0.5F, this.getText((AutoCrystal.Text) AutoCrystal.renderText.getValue()));
        }

    }

    @SubscribeEvent(
        priority = EventPriority.HIGHEST
    )
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketPlayer && this.crystalRotation != null && ((Rotation.Rotate) AutoCrystal.rotate.getValue()).equals(Rotation.Rotate.PACKET)) {
            if (Math.abs(this.crystalRotation.getYaw() - AutoCrystal.mc.player.rotationYaw) >= 20.0F || Math.abs(this.crystalRotation.getPitch() - AutoCrystal.mc.player.rotationPitch) >= 20.0F) {
                for (float step = ((Float) AutoCrystal.rotateStep.getValue()).floatValue() - 1.0F; step > 0.0F; --step) {
                    AutoCrystal.mc.player.connection.sendPacket(new net.minecraft.network.play.client.CPacketPlayer.Rotation(this.crystalRotation.getYaw() / step + 1.0F, this.crystalRotation.getPitch() / step + 1.0F, AutoCrystal.mc.player.onGround));
                }
            }

            ((ICPacketPlayer) event.getPacket()).setYaw(this.crystalRotation.getYaw());
            ((ICPacketPlayer) event.getPacket()).setPitch(this.crystalRotation.getPitch());
        }

        if (event.getPacket() instanceof CPacketHeldItemChange) {
            this.switchTimer.reset();
        }

    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketSpawnObject && ((SPacketSpawnObject) event.getPacket()).getType() == 51 && ((AutoCrystal.Timing) AutoCrystal.timing.getValue()).equals(AutoCrystal.Timing.LINEAR) && ((Boolean) AutoCrystal.explode.getValue()).booleanValue()) {
            BlockPos linearPosition = new BlockPos(((SPacketSpawnObject) event.getPacket()).getX(), ((SPacketSpawnObject) event.getPacket()).getY(), ((SPacketSpawnObject) event.getPacket()).getZ());
            boolean wallPlacement = !((AutoCrystal.Raytrace) AutoCrystal.placeRaytrace.getValue()).equals(AutoCrystal.Raytrace.NONE) && RaytraceUtil.raytraceBlock(linearPosition, (AutoCrystal.Raytrace) AutoCrystal.placeRaytrace.getValue());
            double distance = AutoCrystal.mc.player.getDistance((double) linearPosition.getX() + 0.5D, (double) (linearPosition.getY() + 1), (double) linearPosition.getZ() + 0.5D);

            if (distance > ((Double) AutoCrystal.explodeWall.getValue()).doubleValue() && wallPlacement) {
                return;
            }

            float localDamage = ExplosionUtil.getDamageFromExplosion((double) linearPosition.getX() + 0.5D, (double) (linearPosition.getY() + 1), (double) linearPosition.getZ() + 0.5D, AutoCrystal.mc.player, ((Boolean) AutoCrystal.ignoreTerrain.getValue()).booleanValue(), false);

            if ((double) localDamage > ((Double) AutoCrystal.explodeLocal.getValue()).doubleValue() || (double) (localDamage + 1.0F) > PlayerUtil.getHealth() && ((Boolean) AutoCrystal.pauseSafety.getValue()).booleanValue()) {
                return;
            }

            TreeMap linearMap = new TreeMap();
            Iterator idealLinear = AutoCrystal.mc.world.playerEntities.iterator();

            while (idealLinear.hasNext()) {
                EntityPlayer calculatedTarget = (EntityPlayer) idealLinear.next();

                if (!calculatedTarget.equals(AutoCrystal.mc.player) && !EnemyUtil.isDead(calculatedTarget)) {
                    float targetDistance = AutoCrystal.mc.player.getDistance(calculatedTarget);

                    if ((double) targetDistance <= ((Double) AutoCrystal.targetRange.getValue()).doubleValue()) {
                        float targetDamage = this.calculateLogic(ExplosionUtil.getDamageFromExplosion((double) linearPosition.getX() + 0.5D, (double) (linearPosition.getY() + 1), (double) linearPosition.getZ() + 0.5D, calculatedTarget, ((Boolean) AutoCrystal.ignoreTerrain.getValue()).booleanValue(), false), localDamage, distance);

                        linearMap.put(Float.valueOf(targetDamage), Float.valueOf(targetDamage));
                    }
                }
            }

            if (!linearMap.isEmpty()) {
                float idealLinear1 = ((Float) linearMap.lastEntry().getValue()).floatValue();

                if ((double) idealLinear1 > ((Double) AutoCrystal.explodeDamage.getValue()).doubleValue()) {
                    this.explodeCrystal(((SPacketSpawnObject) event.getPacket()).getEntityID());
                    AutoCrystal.attemptedExplosions.put(Integer.valueOf(((SPacketSpawnObject) event.getPacket()).getEntityID()), Integer.valueOf(AutoCrystal.attemptedExplosions.containsKey(Integer.valueOf(((SPacketSpawnObject) event.getPacket()).getEntityID())) ? ((Integer) AutoCrystal.attemptedExplosions.get(Integer.valueOf(((SPacketSpawnObject) event.getPacket()).getEntityID()))).intValue() + 1 : 1));
                }
            }
        }

        if (event.getPacket() instanceof SPacketSoundEffect && ((SPacketSoundEffect) event.getPacket()).getSound().equals(SoundEvents.ENTITY_GENERIC_EXPLODE) && ((SPacketSoundEffect) event.getPacket()).getCategory().equals(SoundCategory.BLOCKS)) {
            AutoCrystal.mc.addScheduledTask(run<invokedynamic>());
        }

    }

    public void placeCrystal(BlockPos placePos, EnumFacing enumFacing, boolean packet) {
        if (packet) {
            AutoCrystal.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, enumFacing, AutoCrystal.mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5F, 0.5F, 0.5F));
        } else {
            AutoCrystal.mc.playerController.processRightClickBlock(AutoCrystal.mc.player, AutoCrystal.mc.world, placePos, enumFacing, new Vec3d(0.0D, 0.0D, 0.0D), AutoCrystal.mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        }

    }

    public void explodeCrystal(EntityEnderCrystal crystal, boolean packet) {
        if (packet) {
            AutoCrystal.mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
        } else {
            AutoCrystal.mc.playerController.attackEntity(AutoCrystal.mc.player, crystal);
        }

    }

    public void explodeCrystal(int entityId) {
        CPacketUseEntity attackPacket = new CPacketUseEntity();

        ((ICPacketUseEntity) attackPacket).setID(entityId);
        ((ICPacketUseEntity) attackPacket).setAction(Action.ATTACK);
        AutoCrystal.mc.player.connection.sendPacket(attackPacket);
    }

    public boolean canPlaceCrystal(BlockPos blockPos, AutoCrystal.Placements placements) {
        try {
            if (!AutoCrystal.mc.world.getBlockState(blockPos).getBlock().equals(Blocks.BEDROCK) && !AutoCrystal.mc.world.getBlockState(blockPos).getBlock().equals(Blocks.OBSIDIAN)) {
                return false;
            } else {
                Iterator ignored = AutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 0))).iterator();

                Entity entity;

                do {
                    do {
                        if (!ignored.hasNext()) {
                            switch (placements) {
                            case NATIVE:
                            default:
                                return AutoCrystal.mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && AutoCrystal.mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR);

                            case UPDATED:
                                return AutoCrystal.mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR);
                            }
                        }

                        entity = (Entity) ignored.next();
                    } while (entity.isDead);
                } while (entity instanceof EntityEnderCrystal && entity.getPosition().equals(blockPos.add(0, 1, 0)));

                return false;
            }
        } catch (Exception exception) {
            return false;
        }
    }

    public float calculateLogic(float targetDamage, float selfDamage, double distance) {
        switch ((AutoCrystal.Logic) AutoCrystal.logic.getValue()) {
        case DAMAGE:
        default:
            return targetDamage;

        case MINIMAX:
            return targetDamage - selfDamage;

        case ATOMIC:
            return targetDamage - selfDamage - (float) distance;

        case VOLATILE:
            return targetDamage - (float) distance;
        }
    }

    public String getRenderInfo() {
        switch ((AutoCrystal.Info) AutoCrystal.renderInfo.getValue()) {
        case DAMAGE:
            return String.valueOf(MathUtil.roundDouble(AutoCrystal.placePosition.getTargetDamage(), 1));

        case LATENCY:
            return String.valueOf(this.explodeTimer.getMS(System.nanoTime() - this.explodeTimer.time) / 100L);

        case TARGET:
            return AutoCrystal.placePosition.getPlaceTarget().getName();

        case NONE:
        default:
            return "";
        }
    }

    public String getText(AutoCrystal.Text text) {
        switch (text) {
        case TARGET:
            return String.valueOf(MathUtil.roundDouble(AutoCrystal.placePosition.getTargetDamage(), 1));

        case SELF:
            return String.valueOf(MathUtil.roundDouble(AutoCrystal.placePosition.getSelfDamage(), 1));

        case BOTH:
            return "Target: " + MathUtil.roundDouble(AutoCrystal.placePosition.getTargetDamage(), 1) + ", Self: " + MathUtil.roundDouble(AutoCrystal.placePosition.getSelfDamage(), 1);

        default:
            return "";
        }
    }

    private static void lambda$onPacketReceive$6() {
        (new ArrayList(AutoCrystal.mc.world.loadedEntityList)).stream().filter(test<invokedynamic>()).filter(test<invokedynamic>()).filter(test<invokedynamic>()).forEach(accept<invokedynamic>());
    }

    private static void lambda$null$5(Entity entity) {
        if (((AutoCrystal.Sync) AutoCrystal.sync.getValue()).equals(AutoCrystal.Sync.SOUND)) {
            entity.setDead();
            AutoCrystal.mc.world.removeEntityFromWorld(entity.getEntityId());
        }

    }

    private static boolean lambda$null$4(Entity entity) {
        return AutoCrystal.attemptedExplosions.containsKey(Integer.valueOf(entity.getEntityId()));
    }

    private static boolean lambda$null$3(Entity entity) {
        return AutoCrystal.mc.player.getDistance(entity) < 6.0F;
    }

    private static boolean lambda$null$2(Entity entity) {
        return entity instanceof EntityEnderCrystal;
    }

    private static Boolean lambda$static$1() {
        return Boolean.valueOf(((RenderBuilder.Box) AutoCrystal.renderMode.getValue()).equals(RenderBuilder.Box.BOTH) || ((RenderBuilder.Box) AutoCrystal.renderMode.getValue()).equals(RenderBuilder.Box.CLAW) || ((RenderBuilder.Box) AutoCrystal.renderMode.getValue()).equals(RenderBuilder.Box.OUTLINE));
    }

    private static String lambda$new$0() {
        return AutoCrystal.INSTANCE.getRenderInfo();
    }

    public static class Crystal {

        private final EntityEnderCrystal crystal;
        private final double targetDamage;
        private final double selfDamage;

        public Crystal(EntityEnderCrystal crystal, double targetDamage, double selfDamage) {
            this.crystal = crystal;
            this.targetDamage = targetDamage;
            this.selfDamage = selfDamage;
        }

        public EntityEnderCrystal getCrystal() {
            return this.crystal;
        }

        public double getTargetDamage() {
            return this.targetDamage;
        }

        public double getSelfDamage() {
            return this.selfDamage;
        }
    }

    public static class CrystalPosition {

        private final BlockPos blockPos;
        private final EntityPlayer placeTarget;
        private final double targetDamage;
        private final double selfDamage;

        public CrystalPosition(BlockPos blockPos, EntityPlayer placeTarget, double targetDamage, double selfDamage) {
            this.blockPos = blockPos;
            this.placeTarget = placeTarget;
            this.targetDamage = targetDamage;
            this.selfDamage = selfDamage;
        }

        public BlockPos getPosition() {
            return this.blockPos;
        }

        public EntityPlayer getPlaceTarget() {
            return this.placeTarget;
        }

        public double getTargetDamage() {
            return this.targetDamage;
        }

        public double getSelfDamage() {
            return this.selfDamage;
        }
    }

    public static enum Raytrace {

        NONE(-1.0D), BASE(0.5D), NORMAL(1.5D), DOUBLE(2.5D), TRIPLE(3.5D);

        private final double offset;

        private Raytrace(double offset) {
            this.offset = offset;
        }

        public double getOffset() {
            return this.offset;
        }
    }

    public static enum Text {

        TARGET, SELF, BOTH, NONE;
    }

    public static enum Info {

        DAMAGE, LATENCY, TARGET, NONE;
    }

    public static enum When {

        BREAK, PLACE, BOTH;
    }

    public static enum Logic {

        DAMAGE, MINIMAX, ATOMIC, VOLATILE;
    }

    public static enum Sync {

        SOUND, INSTANT, NONE;
    }

    public static enum Timing {

        LINEAR, SEQUENTIAL, TICK;
    }

    public static enum Placements {

        NATIVE, UPDATED;
    }
}
